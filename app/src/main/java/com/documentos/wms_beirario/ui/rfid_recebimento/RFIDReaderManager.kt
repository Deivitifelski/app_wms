package com.documentos.wms_beirario.ui.rfid_recebimento

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.zebra.rfid.api3.BEEPER_VOLUME
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE
import com.zebra.rfid.api3.ENVIRONMENT_MODE
import com.zebra.rfid.api3.HANDHELD_TRIGGER_EVENT_TYPE
import com.zebra.rfid.api3.INVENTORY_STATE
import com.zebra.rfid.api3.RFIDReader
import com.zebra.rfid.api3.ReaderDevice
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

        fun getInstance(): RFIDReaderManager {
            return instance ?: synchronized(this) {
                instance ?: RFIDReaderManager().also { instance = it }
            }
        }
    }

    /**
     * Conecta ao leitor RFID, se ainda não estiver conectado.
     * Retorna true se a conexão foi bem-sucedida ou já estava conectada.
     */
    fun connectRfid(
        context: Context,
        type: ConnectionType,
        ipBluetoothDevice: BluetoothDevice? = null,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        onResultTag: (TagData) -> Unit,
        onEventResult: (RfidStatusEvents) -> Unit
    ) {
        try {
            when (type) {
                ConnectionType.USB -> {
                    if (rfidReader == null) {
                        connectedUSB(context, onError, onSuccess, onResultTag, onEventResult)
                    } else {
                        if (rfidReader!!.isConnected) {
                            onSuccess("Conectado!")
                        } else {
                            connectedUSB(context, onError, onSuccess, onResultTag, onEventResult)
                        }
                    }
                }

                ConnectionType.BLUETOOTH -> {
                    if (rfidReader != null) {
                        if (ipBluetoothDevice == null) {
                            onError("Dispositivo Bluetooth não fornecido.")
                            return
                        }

                        CoroutineScope(Dispatchers.Main).launch {
                            try {
                                val reader = Readers(context, ENUM_TRANSPORT.BLUETOOTH)
                                val readerList = reader.GetAvailableRFIDReaderList()
                                // Filtra o dispositivo correto baseado no endereço do BluetoothDevice
                                val readerDevice = readerList?.find {
                                    it.address == ipBluetoothDevice.address
                                }
                                if (readerDevice == null) {
                                    onError("Dispositivo Bluetooth não encontrado.")
                                    return@launch
                                }
                                rfidReader = readerDevice.rfidReader
                                rfidReader?.connect()
                                if (rfidReader?.isConnected == true) {
                                    onSuccess("Conectado com sucesso via Bluetooth:\n${readerDevice.name}")
                                } else {
                                    onError("Não foi possível conectar ao dispositivo Bluetooth.")
                                }
                            } catch (e: Exception) {
                                onError("Erro na conexão Bluetooth: ${e.message}")
                            }
                        }
                    } else {
                        onSuccess("Já está conectado via Bluetooth.")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onError("Erro inesperado: ${e.message}")
        }
    }

    private fun connectedUSB(
        context: Context,
        onError: (String) -> Unit,
        onSuccess: (String) -> Unit,
        onResultTag: (TagData) -> Unit,
        onEventResult: (RfidStatusEvents) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reader = Readers(context, ENUM_TRANSPORT.SERVICE_USB)
                val readerList = reader.GetAvailableRFIDReaderList()

                // Verifica se há leitores disponíveis
                if (readerList.isNullOrEmpty()) {
                    onError("Nenhum leitor USB disponível.")
                    return@launch
                }

                val readerDevice: ReaderDevice = readerList[0]
                rfidReader = readerDevice.rfidReader
                rfidReader?.connect()

                if (rfidReader?.isConnected == true) {
                    configureReader(
                        onResultEvent = { event ->
                            onEventResult.invoke(event)
                        },
                        onResultTag = { tag ->
                            onResultTag.invoke(tag)
                        }
                    )
                    onSuccess("Conectado com sucesso via USB:\n${readerDevice.name}")
                } else {
                    onError("Não foi possível realizar a conexão com dispositivo USB.")
                }
            } catch (e: Exception) {
                onError("Erro na conexão USB: ${e.message}")
            }
        }
    }



    private fun configureReader(
        onResultTag: (TagData) -> Unit,
        onResultEvent: (RfidStatusEvents) -> Unit
    ) {
        try {
            if (rfidReader != null) {
                val listener = object : RfidEventsListener {
                    override fun eventReadNotify(readEvents: RfidReadEvents?) {
                        readEvents?.let {
                            onResultTag.invoke(it.readEventData.tagData)
                        }
                    }

                    override fun eventStatusNotify(statusEvents: RfidStatusEvents?) {
                        if (statusEvents != null) {
                            onResultEvent.invoke(statusEvents)
                        }
                        val eventType = statusEvents?.StatusEventData?.statusEventType
                        val eventData = statusEvents?.StatusEventData
                        if (eventType == STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {
                            val triggerEvent = eventData?.HandheldTriggerEventData?.handheldEvent
                            if (triggerEvent == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED) {
                                rfidReader?.let { rfid ->
                                    rfid.Actions.Inventory.perform()
                                    Log.e("->", "Iniciou gatilho.")
                                }
                            }
                            if (triggerEvent == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED) {
                                rfidReader?.let { rfid ->
                                    rfid.Actions.Inventory.stop()
                                    Log.e("->", "Parou gatilho.")
                                }
                            }
                        }
                    }
                }
                rfidReader!!.Events.addEventsListener(listener)
                rfidReader!!.Events.apply {
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

                val triggerInfo = TriggerInfo().apply {
                    StartTrigger.triggerType = START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE
                    StopTrigger.triggerType = STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE
                }
                rfidReader!!.Config.setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, true)
                rfidReader!!.Config.startTrigger = triggerInfo.StartTrigger
                rfidReader!!.Config.stopTrigger = triggerInfo.StopTrigger
                // Listener para eventos RFID

            }
        } catch (e: Exception) {
            e.printStackTrace()
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
            if (rfidReader != null) {
                // Configurar a potência de transmissão da antena
                val numAntennas = rfidReader!!.ReaderCapabilities.numAntennaSupported
                Log.e("RFID_CONFIG", "NUM_ANTENNAS: $numAntennas")
                val config = rfidReader!!.Config.Antennas.getAntennaRfConfig(1)
                config.transmitPowerIndex = transmitPowerIndex
                config.environment_mode = ENVIRONMENT_MODE.HIGH_INTERFERENCE
                rfidReader!!.Config.Antennas.setAntennaRfConfig(1, config)
                Log.d("RFID_CONFIG", "Potência ajustada para o índice: $transmitPowerIndex")

                // Volume do Bipe
                rfidReader!!.Config.beeperVolume = BEEPER_VOLUME.MEDIUM_BEEP

                // Configurar o modo RF da antena
                config.setrfModeTableIndex(rfModeTableIndex.toLong())
                rfidReader!!.Config.Antennas.setAntennaRfConfig(1, config)
                Log.d("RFID_CONFIG", "Modo RF ajustado para o índice: $rfModeTableIndex")

                // Configurar o controle de singulação
                val singulationControl = rfidReader!!.Config.Antennas.getSingulationControl(1)
                singulationControl.session = session
                singulationControl.Action.inventoryState = inventoryState
                singulationControl.Action.slFlag = slFlag
                rfidReader!!.Config.Antennas.setSingulationControl(1, singulationControl)

                Log.d(
                    "RFID_CONFIG",
                    "Controle de singulação ajustado: Sessão = $session, Estado do inventário = $inventoryState, SL Flag = $slFlag"
                )
                // Deletar pre-filtros se necessário
                rfidReader!!.Actions.PreFilters.deleteAll()
                Log.d("RFID_CONFIG", "Todos os pre-filtros deletados.")
            }

        } catch (e: Exception) {
            onResult.invoke("Erro ao configurar o leitor RFID: ${e.message}")
        }
    }

}


/**
 * Desconecta do leitor RFID e redefine as configurações.
 */
//fun disconnect() {
//    if (isConnected) {
//        try {
//            rfidReader?.disconnect()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            isConnected = false
//            rfidReader = null
//        }
//    }
//}

/**
 * Atualiza uma configuração específica do leitor RFID.
 * Exemplo de configuração: Potência de transmissão.
 */
//fun updateConfiguration(powerLevel: Int) {
//    if (isConnected && rfidReader != null) {
//        try {
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    } else {
//        throw IllegalStateException("RFIDReader não está conectado.")
//    }
//}

/**
 * Retorna o status de conexão do leitor.
 */

