package com.documentos.wms_beirario.utils.extensions

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.zebra.rfid.api3.BEEPER_VOLUME
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE
import com.zebra.rfid.api3.ENVIRONMENT_MODE
import com.zebra.rfid.api3.INVENTORY_STATE
import com.zebra.rfid.api3.RFIDReader
import com.zebra.rfid.api3.RfidEventsListener
import com.zebra.rfid.api3.SESSION
import com.zebra.rfid.api3.SL_FLAG
import com.zebra.rfid.api3.START_TRIGGER_TYPE
import com.zebra.rfid.api3.STOP_TRIGGER_TYPE
import com.zebra.rfid.api3.TriggerInfo

fun RFIDReader.configureReader(context: Context) {
    try {
        this.Events.addEventsListener(context as RfidEventsListener)
        this.Events.setInventoryStopEvent(true)
        this.Events.setInventoryStartEvent(true)
        this.Events.setScanDataEvent(true)
        this.Events.setReaderDisconnectEvent(true)
        this.Events.setBatteryEvent(true)
        this.Events.setHandheldEvent(true)
        this.Events.setTagReadEvent(true)
        this.Events.setInfoEvent(true)
        this.Events.setPowerEvent(true)
        this.Events.setTemperatureAlarmEvent(true)
        this.Events.setAttachTagDataWithReadEvent(true)

        val triggerInfo = TriggerInfo().apply {
            StartTrigger.triggerType = START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE
            StopTrigger.triggerType = STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE
        }

        this.Config.setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, true)
        this.Config.startTrigger = triggerInfo.StartTrigger
        this.Config.stopTrigger = triggerInfo.StopTrigger

    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("RFIDReader", "Erro ao configurar o leitor: ${e.message}")
        Toast.makeText(context, "Erro ao configurar: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}



fun RFIDReader.configureRfidReader(
    transmitPowerIndex: Int,
    rfModeTableIndex: Int,
    session: SESSION,
    inventoryState: INVENTORY_STATE,
    slFlag: SL_FLAG
) {
    try {
        // Configurar a potência de transmissão da antena
        val numAntennas = this.ReaderCapabilities.numAntennaSupported
        Log.e("RFID_CONFIG", "NUM_ANTENNAS: $numAntennas")
        val config = this.Config.Antennas.getAntennaRfConfig(1)
        config.transmitPowerIndex = transmitPowerIndex
        config.environment_mode = ENVIRONMENT_MODE.HIGH_INTERFERENCE
        this.Config.Antennas.setAntennaRfConfig(1, config)
        Log.d("RFID_CONFIG", "Potência ajustada para o índice: $transmitPowerIndex")

        // Volume do Bipe
        this.Config.beeperVolume = BEEPER_VOLUME.MEDIUM_BEEP

        // Configurar o modo RF da antena
        config.setrfModeTableIndex(rfModeTableIndex.toLong())
        this.Config.Antennas.setAntennaRfConfig(1, config)
        Log.d("RFID_CONFIG", "Modo RF ajustado para o índice: $rfModeTableIndex")

        // Configurar o controle de singulação
        val singulationControl = this.Config.Antennas.getSingulationControl(1)
        singulationControl.session = session
        singulationControl.Action.inventoryState = inventoryState
        singulationControl.Action.slFlag = slFlag
        this.Config.Antennas.setSingulationControl(1, singulationControl)

        Log.d(
            "RFID_CONFIG",
            "Controle de singulação ajustado: Sessão = $session, Estado do inventário = $inventoryState, SL Flag = $slFlag"
        )

        // Deletar pre-filtros se necessário
        this.Actions.PreFilters.deleteAll()
        Log.d("RFID_CONFIG", "Todos os pre-filtros deletados.")

    } catch (e: Exception) {
        Log.e("RFID_CONFIG_ERROR", "Erro ao configurar o leitor RFID: ${e.message}")
        e.printStackTrace()
    }
}


