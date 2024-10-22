package com.documentos.wms_beirario.utils.extensions

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE
import com.zebra.rfid.api3.RFIDReader
import com.zebra.rfid.api3.RfidEventsListener
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
