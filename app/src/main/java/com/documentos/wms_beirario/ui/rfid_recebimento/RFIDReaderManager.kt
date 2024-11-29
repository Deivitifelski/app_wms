package com.documentos.wms_beirario.ui.rfid_recebimento

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.documentos.wms_beirario.utils.extensions.toastDefault
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RFIDReaderManager private constructor() {

    private var rfidReader: RFIDReader? = null

    companion object {
        @Volatile
        private var instance: RFIDReaderManager? = null
        var GATILHO_CLICADO = false

        fun getInstance(): RFIDReaderManager {
            return instance ?: synchronized(this) {
                instance ?: RFIDReaderManager().also { instance = it }
            }
        }
    }


    fun verifyConnectRfid(
        onResponse: (Boolean) -> Unit,
    ) {
        try {
            if (isConnectedRfid()) {
                onResponse.invoke(true)
            } else {
                onResponse.invoke(false)
            }
        } catch (e: Exception) {
            Log.e("RFIDReaderManager", "Erro ao conectar leitor RFID: ${e.message}")
        }
    }


    private fun isConnectedRfid(): Boolean {
        return try {
            if (rfidReader != null) {
                // Verifique se o leitor responde a um método de ping, se disponível
                val isConnected = rfidReader!!.Actions.purgeTags()
                if (isConnected) {
                    Log.e("RFIDReaderManager", "Leitor RFID está conectado.")
                    true
                } else {
                    Log.e("RFIDReaderManager", "Leitor RFID NÃO está conectado.")
                    false
                }
            } else {
                Log.e("RFIDReaderManager", "Leitor RFID NÃO inicializado.")
                false
            }
        } catch (e: Exception) {
            Log.e("RFIDReaderManager", "Erro específico ao conectar leitor RFID: ${e.message}")
            false
        } catch (e: Exception) {
            Log.e("RFIDReaderManager", "Erro ao conectar leitor RFID: ${e.message}")
            false
        }
    }


    fun connectUsbRfid(
        context: Context,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reader = Readers(context, ENUM_TRANSPORT.SERVICE_USB)
                val readerList = reader.GetAvailableRFIDReaderList()

                if (readerList.isNullOrEmpty()) {
                    onError.invoke("Nenhum dispositivo USB encontrado.")
                    return@launch
                }
                val readerDevice = readerList.first()
                rfidReader = readerDevice.rfidReader
                rfidReader?.connect()

                if (rfidReader?.isConnected == true) {
                    withContext(Dispatchers.Main) {
                        onResult("Conectado com sucesso via USB: ${readerDevice.name}")
                    }
                } else {
                    onError("Não foi possível realizar a conexão com dispositivo USB.")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError("Erro na conexão USB: ${e.message}") }
            }
        }
    }


    fun connectBluetooh(
        context: Context,
        address: String,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reader = Readers(context, ENUM_TRANSPORT.BLUETOOTH)
                val readerList = reader.GetAvailableRFIDReaderList()
                readerList.forEach { data ->
                    Log.e("------->", "Dispositivos zebra Bluetooth pairados: ${data.address} - ${address}")
                    if (data.address == address) {
                        rfidReader = data.rfidReader
                        rfidReader?.connect()
                    }
                }

                if (rfidReader?.isConnected == true) {
                    withContext(Dispatchers.Main) {
                        onResult("Conectado com sucesso via Bluetooth")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onError("Não foi possível realizar a conexão com dispositivo Bluetooth.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError("Erro na conexão Bluetooth: ${e.message}") }
            }
        }
    }


    fun setupVolumeBeep(quiet: Boolean) {
        rfidReader?.Config?.beeperVolume =
            if (quiet) BEEPER_VOLUME.QUIET_BEEP else (BEEPER_VOLUME.MEDIUM_BEEP
                ?: Log.e("RFIDReaderManager", "Erro ao atualizar volume do beep")) as BEEPER_VOLUME?
    }

    fun configureReaderRfid(
        onResultTag: (TagData) -> Unit,
        onResultEvent: (RfidStatusEvents) -> Unit,
        onResultEventClickTrigger: (Boolean) -> Unit,
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
                setupListeners(onResultTag, onResultEvent, onResultEventClickTrigger)
            }
        } catch (e: Exception) {
            Log.e("RFIDReaderManager", "Erro ao configurar leitor: ${e.message}")
        }
    }

    private fun setupListeners(
        onResultTag: (TagData) -> Unit,
        onResultEvent: (RfidStatusEvents) -> Unit,
        onResultEventClickTrigger: (Boolean) -> Unit,

        ) {
        rfidReader?.Events?.addEventsListener(object : RfidEventsListener {
            override fun eventReadNotify(readEvents: RfidReadEvents?) {
                readEvents?.readEventData?.tagData?.let { onResultTag(it) }
            }

            override fun eventStatusNotify(statusEvents: RfidStatusEvents?) {
                statusEvents?.let { onResultEvent(it) }
                handleBatteryAndTriggerEvents(statusEvents, onResultEventClickTrigger)
            }
        })
    }



    private fun handleBatteryAndTriggerEvents(
        statusEvents: RfidStatusEvents?,
        onResultEventClickTrigger: (Boolean) -> Unit
    ) {
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
                            onResultEventClickTrigger.invoke(true)
                            GATILHO_CLICADO = true
                            Log.e("RFIDReaderManager", "Iniciou gatilho.")
                        }

                        HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED -> {
                            rfidReader?.Actions?.Inventory?.stop()
                            onResultEventClickTrigger.invoke(false)
                            GATILHO_CLICADO = false
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
        onResult: (String) -> Unit,
        changed: Boolean? = false
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
                if (changed == true) {
                    onResult.invoke("Configurações aplicadas.")
                }
                Log.d("RFIDReaderManager", "Configurações aplicadas.")
            }
        } catch (e: Exception) {
            onResult("Erro ao configurar o leitor RFID: ${e.message}")
        }
    }
}

