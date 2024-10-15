package com.documentos.wms_beirario.ui.recebimentoRFID

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.documentos.wms_beirario.databinding.ActivityRecebimentoRfidBinding
import com.documentos.wms_beirario.utils.extensions.somSucess
import com.documentos.wms_beirario.utils.extensions.somWarning
import com.documentos.wms_beirario.utils.extensions.toastDefault
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.RFIDReader
import com.zebra.rfid.api3.ReaderDevice
import com.zebra.rfid.api3.Readers
import com.zebra.rfid.api3.Readers.RFIDReaderEventHandler
import com.zebra.rfid.api3.RfidEventsListener
import com.zebra.rfid.api3.RfidReadEvents
import com.zebra.rfid.api3.RfidStatusEvents
import com.zebra.rfid.api3.START_TRIGGER_TYPE
import com.zebra.rfid.api3.STOP_TRIGGER_TYPE
import com.zebra.rfid.api3.TriggerInfo

class RecebimentoRfidActivity : AppCompatActivity(), RfidEventsListener {

    private lateinit var binding: ActivityRecebimentoRfidBinding
    private lateinit var bluetooh :String
    private lateinit var readers: Readers
    private lateinit var rfidReader: RFIDReader
    private lateinit var listRfid: List<ReaderDevice>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecebimentoRfidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()


    }

    private fun getIntentData() {
        if (intent != null){
            bluetooh = intent.getStringExtra("BLUETOOH").toString()
        }
    }

    override fun onResume() {
        super.onResume()
        /**Tenta faezr a conexão*/
        connectRfid()
    }

    private fun connectRfid() {
        try {

            readers = Readers(this, ENUM_TRANSPORT.SERVICE_SERIAL)
            listRfid = readers.GetAvailableRFIDReaderList()

            if (listRfid.isNotEmpty()) {
                rfidReader = listRfid[0].rfidReader
                rfidReader.connect()

                rfidReader.Events.addEventsListener(this)
                rfidReader.Events.setHandheldEvent(true)
                rfidReader.Events.setTagReadEvent(true)

                rfidReader.Events.setAttachTagDataWithReadEvent(true)

                val triggerInfo = TriggerInfo()
                triggerInfo.StartTrigger.triggerType = START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE
                triggerInfo.StopTrigger.triggerType = STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE


                rfidReader.Config.startTrigger = triggerInfo.StartTrigger
                rfidReader.Config.stopTrigger = triggerInfo.StopTrigger

                binding.textRfid.text = "Precionado gatilho"

            } else {
                toastDefault(message = "Nenhum leitor encontrado")
            }

        } catch (e: Exception) {
            // Handle specific RFID communication exceptions
            toastDefault(message = "Erro ao conectar leitor: ${e.message}")
        }
    }

    override fun eventReadNotify(data: RfidReadEvents?) {
        somSucess()
        toastDefault(message = data?.readEventData?.tagData.toString())
    }

    override fun eventStatusNotify(p0: RfidStatusEvents?) {
        somWarning()
        toastDefault(message = p0?.StatusEventData.toString())
    }
}