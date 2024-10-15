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
import com.zebra.rfid.api3.RfidEventsListener
import com.zebra.rfid.api3.RfidReadEvents
import com.zebra.rfid.api3.RfidStatusEvents
import com.zebra.rfid.api3.START_TRIGGER_TYPE
import com.zebra.rfid.api3.STOP_TRIGGER_TYPE
import com.zebra.rfid.api3.TriggerInfo

class RecebimentoRfidActivity : AppCompatActivity(), RfidEventsListener {

    private lateinit var binding: ActivityRecebimentoRfidBinding
    private lateinit var readers: Readers
    private lateinit var rfidReader: RFIDReader
    private lateinit var listRfid: List<ReaderDevice>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecebimentoRfidBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        connectRfid()


    }

    override fun onPause() {
        super.onPause()
        disconnectRfid()
    }

    private fun connectRfid() {
        try {
            readers = Readers(this, ENUM_TRANSPORT.SERVICE_SERIAL)
            listRfid = readers.GetAvailableRFIDReaderList()

            if (listRfid.isNotEmpty()) {
                rfidReader = listRfid[0].rfidReader
                rfidReader.connect()
                rfidReader.Events.addEventsListener(object : RfidEventsListener {
                    override fun eventReadNotify(p0: RfidReadEvents?) {
                        toastDefault(message = p0?.readEventData?.tagData.toString())
                    }

                    override fun eventStatusNotify(statusEvent: RfidStatusEvents?) {
                        toastDefault(message = statusEvent?.StatusEventData.toString())
                    }
                })

                // Configura eventos de leitura e status
                rfidReader.Events.addEventsListener(this)
                rfidReader.Events.setHandheldEvent(true)
                rfidReader.Events.setTagReadEvent(true)
                rfidReader.Events.setAttachTagDataWithReadEvent(true)

                // Configuração do gatilho
                val triggerInfo = TriggerInfo().apply {
                    StartTrigger.triggerType = START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE
                    StopTrigger.triggerType = STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE
                }

                rfidReader.Config.startTrigger = triggerInfo.StartTrigger
                rfidReader.Config.stopTrigger = triggerInfo.StopTrigger

                binding.textRfid.text = "Pressione o gatilho para ler"
            } else {
                toastDefault(message = "Nenhum leitor encontrado")
            }

        } catch (e: Exception) {
            toastDefault(message = "Erro ao conectar ao leitor RFID: ${e.localizedMessage}")
        }
    }

    private fun disconnectRfid() {
        try {
            if (::rfidReader.isInitialized && rfidReader.isConnected) {
                rfidReader.disconnect()
            }
        } catch (e: Exception) {
            toastDefault(message = "Erro ao desconectar do leitor RFID: ${e.localizedMessage}")
        }
    }

    override fun eventReadNotify(data: RfidReadEvents?) {
        data?.readEventData?.tagData?.let {
            somSucess()
            toastDefault(message = it.toString())
        }
    }

    override fun eventStatusNotify(statusEvent: RfidStatusEvents?) {
        statusEvent?.StatusEventData?.let {
            somWarning()
            toastDefault(message = it.toString())
        }
    }
}
