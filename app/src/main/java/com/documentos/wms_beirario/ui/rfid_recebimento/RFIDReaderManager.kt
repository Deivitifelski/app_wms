package com.documentos.wms_beirario.ui.rfid_recebimento

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Looper
import android.util.Log
import com.zebra.rfid.api3.BEEPER_VOLUME
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE
import com.zebra.rfid.api3.ENVIRONMENT_MODE
import com.zebra.rfid.api3.HANDHELD_TRIGGER_EVENT_TYPE
import com.zebra.rfid.api3.INVENTORY_STATE
import com.zebra.rfid.api3.RFIDReader
import com.zebra.rfid.api3.Readers
import com.zebra.rfid.api3.RfidEventsListener
import com.zebra.rfid.api3.RfidReadEvents
import com.zebra.rfid.api3.RfidStatusEvents
import com.zebra.rfid.api3.SESSION
import com.zebra.rfid.api3.SL_FLAG
import com.zebra.rfid.api3.START_TRIGGER_TYPE
import com.zebra.rfid.api3.STATUS_EVENT_TYPE
import com.zebra.rfid.api3.STOP_TRIGGER_TYPE
import com.zebra.rfid.api3.TagData
import com.zebra.rfid.api3.TriggerInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class RFIDReaderManager private constructor() {

    private var rfidReader: RFIDReader? = null

    companion object {
        @Volatile
        private var instance: RFIDReaderManager? = null

        fun getInstance(): RFIDReaderManager {
            return instance ?: synchronized(this) {
                instance ?: RFIDReaderManager().also { instance = it }
            }
        }
    }

    fun connectRfid(
        context: Context,
        type: ConnectionType,
        ipBluetoothDevice: BluetoothDevice? = null,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        onResultTag: (TagData) -> Unit,
        onEventResult: (RfidStatusEvents) -> Unit
    ) {
        // Verifica se o leitor já está conectado para evitar reconexões
        if (isReaderConnected()) {
            onSuccess("Leitor já conectado")
            return
        }

        try {
            // Conecta de acordo com o tipo de conexão
            when (type) {
                ConnectionType.USB -> handleUSBConnection(
                    context,
                    onSuccess,
                    onError,
                    onResultTag,
                    onEventResult
                )
                ConnectionType.BLUETOOTH -> {
                    if (ipBluetoothDevice == null) {
                        onError("Dispositivo Bluetooth não fornecido")
                        return
                    }
                    handleBluetoothConnection(
                        context,
                        ipBluetoothDevice,
                        onSuccess,
                        onError,
                        onResultTag,
                        onEventResult
                    )
                }
            }
        } catch (e: IOException) {
            Log.e("RFIDReaderManager", "Erro de I/O: ${e.message}")
            onError("Erro de comunicação com o leitor RFID: ${e.message}")
        } catch (e: IllegalStateException) {
            Log.e("RFIDReaderManager", "Estado inválido: ${e.message}")
            onError("Estado inválido para conexão com o leitor RFID: ${e.message}")
        } catch (e: Exception) {
            Log.e("RFIDReaderManager", "Erro inesperado: ${e.message}")
            onError("Erro inesperado: ${e.message}")
        } finally {
            // Inicia monitoramento da conexão uma vez
            startConnectionCheck(
                context,
                onSuccess,
                onError,
                onResultTag,
                onEventResult,
                type,
                ipBluetoothDevice
            )
        }
    }


    private fun handleUSBConnection(
        context: Context,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        onResultTag: (TagData) -> Unit,
        onEventResult: (RfidStatusEvents) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reader = Readers(context, ENUM_TRANSPORT.SERVICE_USB)
                val readerList = reader.GetAvailableRFIDReaderList()

                if (readerList.isNullOrEmpty()) {
                    onError("Nenhum leitor USB disponível.")
                    return@launch
                }

                val readerDevice = readerList.first()
                rfidReader = readerDevice.rfidReader
                rfidReader?.connect()

                if (rfidReader?.isConnected == true) {
                    configureReader(onResultTag, onEventResult)
                    withContext(Dispatchers.Main) { onSuccess("Conectado com sucesso via USB: ${readerDevice.name}") }
                } else {
                    onError("Não foi possível realizar a conexão com dispositivo USB.")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError("Erro na conexão USB: ${e.message}") }
            }
        }
    }

    private fun handleBluetoothConnection(
        context: Context,
        ipBluetoothDevice: BluetoothDevice?,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        onResultTag: (TagData) -> Unit,
        onEventResult: (RfidStatusEvents) -> Unit
    ) {
        if (ipBluetoothDevice == null) {
            onError("Dispositivo Bluetooth não fornecido.")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val reader = Readers(context, ENUM_TRANSPORT.BLUETOOTH)
                val readerDevice = reader.GetAvailableRFIDReaderList()
                    ?.find { it.address == ipBluetoothDevice.address }

                if (readerDevice == null) {
                    onError("Dispositivo Bluetooth não encontrado.")
                    return@launch
                }

                rfidReader = readerDevice.rfidReader
                rfidReader?.connect()

                if (rfidReader?.isConnected == true) {
                    configureReader(onResultTag, onEventResult)
                    onSuccess("Conectado com sucesso via Bluetooth: ${readerDevice.name}")
                } else {
                    onError("Não foi possível conectar ao dispositivo Bluetooth.")
                }
            } catch (e: Exception) {
                onError("Erro na conexão Bluetooth: ${e.message}")
            }
        }
    }


    private var connectionCheckJob: Job? = null

    fun startConnectionCheck(
        context: Context,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        onResultTag: (TagData) -> Unit,
        onEventResult: (RfidStatusEvents) -> Unit,
        type: ConnectionType,
        ipBluetoothDevice: BluetoothDevice?
    ) {
        stopConnectionCheck()  // Interrompe qualquer checagem anterior antes de iniciar nova
        connectionCheckJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(5000) // Intervalo de verificação
                checkConnection(
                    context,
                    onSuccess,
                    onError,
                    onResultTag,
                    onEventResult,
                    type,
                    ipBluetoothDevice
                )
            }
        }
    }

    private fun stopConnectionCheck() {
        connectionCheckJob?.cancel()
    }

    private suspend fun checkConnection(
        context: Context,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        onResultTag: (TagData) -> Unit,
        onEventResult: (RfidStatusEvents) -> Unit,
        type: ConnectionType,
        ipBluetoothDevice: BluetoothDevice?
    ) {
        try {
            if (rfidReader != null && !isReaderConnected()) {
                withContext(Dispatchers.Main) {
                    when (type) {
                        ConnectionType.USB -> handleUSBConnection(context, onSuccess, onError, onResultTag, onEventResult)
                        ConnectionType.BLUETOOTH -> handleBluetoothConnection(
                            context,
                            ipBluetoothDevice,
                            onSuccess,
                            onError,
                            onResultTag,
                            onEventResult
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("RFIDReaderManager", "Erro ao verificar conexão: ${e.message}")
        }
    }

    fun setupVolumeBeep(quiet: Boolean) {
        rfidReader?.Config?.beeperVolume =
            if (quiet) BEEPER_VOLUME.QUIET_BEEP else (BEEPER_VOLUME.MEDIUM_BEEP
                ?: Log.e("RFIDReaderManager", "Erro ao atualizar volume do beep")) as BEEPER_VOLUME?
    }

    private fun configureReader(
        onResultTag: (TagData) -> Unit,
        onResultEvent: (RfidStatusEvents) -> Unit
    ) {
        try {
            rfidReader?.apply {
                Events.apply {
                    setInventoryStopEvent(true)
                    setInventoryStartEvent(true)
                    setScanDataEvent(true)
                    setReaderDisconnectEvent(true)
                    setBatteryEvent(true)
                    setHandheldEvent(true)
                    setTagReadEvent(true)
                    setInfoEvent(true)
                    setPowerEvent(true)
                    setTemperatureAlarmEvent(true)
                    setAttachTagDataWithReadEvent(true)
                }
                Config.apply {
                    setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, true)
                    val triggerInfo = TriggerInfo().apply {
                        StartTrigger.triggerType = START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE
                        StopTrigger.triggerType = STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE
                    }
                    startTrigger = triggerInfo.StartTrigger
                    stopTrigger = triggerInfo.StopTrigger
                }
                setupListeners(onResultTag, onResultEvent)
            }
        } catch (e: Exception) {
            Log.e("RFIDReaderManager", "Erro ao configurar leitor: ${e.message}")
        }
    }

    private fun setupListeners(
        onResultTag: (TagData) -> Unit,
        onResultEvent: (RfidStatusEvents) -> Unit
    ) {
        rfidReader?.Events?.addEventsListener(object : RfidEventsListener {
            override fun eventReadNotify(readEvents: RfidReadEvents?) {
                readEvents?.readEventData?.tagData?.let { onResultTag(it) }
            }

            override fun eventStatusNotify(statusEvents: RfidStatusEvents?) {
                statusEvents?.let { onResultEvent(it) }
                handleBatteryAndTriggerEvents(statusEvents)
            }
        })
    }

    private fun handleBatteryAndTriggerEvents(statusEvents: RfidStatusEvents?) {
        statusEvents?.StatusEventData?.let { eventData ->
            when (eventData.statusEventType) {
                STATUS_EVENT_TYPE.BATTERY_EVENT -> {
                    val batteryLevel = eventData.BatteryData?.level
                    Log.e("RFIDReaderManager", "Battery: $batteryLevel")
                }

                STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT -> {
                    when (eventData.HandheldTriggerEventData?.handheldEvent) {
                        HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED -> {
                            rfidReader?.Actions?.Inventory?.perform()
                            Log.e("RFIDReaderManager", "Iniciou gatilho.")
                        }

                        HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED -> {
                            rfidReader?.Actions?.Inventory?.stop()
                            Log.e("RFIDReaderManager", "Parou gatilho.")
                        }

                        else -> Unit
                    }
                }

                else -> Unit
            }
        }
    }

    fun configureRfidReader(
        transmitPowerIndex: Int,
        rfModeTableIndex: Int,
        session: SESSION,
        inventoryState: INVENTORY_STATE,
        slFlag: SL_FLAG,
        onResult: (String) -> Unit
    ) {
        try {
            rfidReader?.apply {
                val config = Config.Antennas.getAntennaRfConfig(1).apply {
                    this.transmitPowerIndex = transmitPowerIndex
                    this.environment_mode = ENVIRONMENT_MODE.HIGH_INTERFERENCE
                    setrfModeTableIndex(rfModeTableIndex.toLong())
                }
                Config.Antennas.setAntennaRfConfig(1, config)

                Config.beeperVolume = BEEPER_VOLUME.MEDIUM_BEEP

                val singulationControl = Config.Antennas.getSingulationControl(1).apply {
                    this.session = session
                    this.Action.inventoryState = inventoryState
                    this.Action.slFlag = slFlag
                }
                Config.Antennas.setSingulationControl(1, singulationControl)

                Actions.PreFilters.deleteAll()
                Log.d("RFIDReaderManager", "Configurações aplicadas.")
            }
        } catch (e: Exception) {
            onResult("Erro ao configurar o leitor RFID: ${e.message}")
        }
    }

    private fun isReaderConnected(): Boolean {
        return try {
            // Verifica se o leitor está conectado e tenta acessar o status da bateria
            rfidReader?.let {
                it.Config.beeperVolume = BEEPER_VOLUME.MEDIUM_BEEP // Verifica status da bateria sem iniciar leitura
                true // Se a operação for bem-sucedida, o leitor está conectado
            } ?: false
        } catch (e: Exception) {
            Log.e("RFIDReaderManager", "Leitor desconectado ou operação falhou: ${e.message}")
            false // Se houver uma exceção, considera como desconectado
        }
    }

}

