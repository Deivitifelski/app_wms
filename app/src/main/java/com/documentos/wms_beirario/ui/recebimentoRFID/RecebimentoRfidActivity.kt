package com.documentos.wms_beirario.ui.recebimentoRFID

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.documentos.wms_beirario.databinding.ActivityRecebimentoRfidBinding
import com.documentos.wms_beirario.utils.extensions.alertDefaulSimplesError
import com.documentos.wms_beirario.utils.extensions.somSucess
import com.google.zxing.ReaderException
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.RFIDReader
import com.zebra.rfid.api3.ReaderDevice
import com.zebra.rfid.api3.Readers
import com.zebra.rfid.api3.RfidEventsListener
import com.zebra.rfid.api3.RfidReadEvents
import com.zebra.rfid.api3.RfidStatusEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecebimentoRfidActivity : AppCompatActivity(), RfidEventsListener {

    private lateinit var binding: ActivityRecebimentoRfidBinding
    private lateinit var rfidReader: RFIDReader
    private val TAG = "RFID"
    private var readers: Readers? = null
    private var readerList: ArrayList<ReaderDevice>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecebimentoRfidBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        connectReader()
    }


    private fun connectReader() {
        try {
            readers = Readers(this, ENUM_TRANSPORT.SERVICE_USB)
            // Obter a lista de leitores disponíveis
            readerList = readers?.GetAvailableRFIDReaderList()

            if (readerList != null && readerList!!.isNotEmpty()) {
                val readerDevice: ReaderDevice = readerList!![0]
                rfidReader = readerDevice.rfidReader

                // Conectar ao leitor
                rfidReader.connect()
                somSucess()
                binding.textRfid.text = "Conectado ${rfidReader.hostName}"

                if (rfidReader.isConnected) {
                    configureReader()
                }
            } else {
                binding.textRfid.text = "Nenhum leitor RFID encontrado"
                Toast.makeText(this, "Nenhum leitor RFID encontrado", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Erro ao conectar ao leitor: ${e.message}")
            binding.textRfid.text = "Erro ao conectar: ${e.localizedMessage}"
        }
    }

    private fun configureReader() {
        try {
            val antennaConfig = rfidReader.Config.Antennas.getAntennaRfConfig(1)
//            antennaConfig.transmitPowerIndex = 270
            rfidReader.Events.addEventsListener(this)
            rfidReader.Config.Antennas.setAntennaRfConfig(1, antennaConfig)
            rfidReader.Events.setInventoryStartEvent(true)
            rfidReader.Events.setHandheldEvent(true)
            rfidReader.Events.setTagReadEvent(true)
            rfidReader.Events.setAttachTagDataWithReadEvent(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Erro ao configurar o leitor: ${e.message}")
            Toast.makeText(this, "Erro ao configurar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun startReading() {
        try {
            rfidReader.Actions.Inventory.perform()
            Log.d(TAG, "Iniciando leitura de tags")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Erro ao iniciar leitura: ${e.message}")
        }
    }


    private fun configureAntennaPower(potencia: Int) {
        try {
            // Obtém a configuração da antena 1 (normalmente, o RFD4030 tem uma antena primária)
            val antennaRfConfig = rfidReader.Config.Antennas.getAntennaRfConfig(1)

            // O valor pode variar de 0 a 270, onde 270 é a potência máxima
            antennaRfConfig.transmitPowerIndex = potencia
            // Aplica a nova configuração da antena
            rfidReader.Config.Antennas.setAntennaRfConfig(1, antennaRfConfig)
            // Log para confirmar a alteração
            Log.d(TAG, "Força da antena ajustada para: ${antennaRfConfig.transmitPowerIndex}")

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Erro ao configurar a força da antena: ${e.message}")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Desconectar o leitor ao fechar a Activity
        disconnectReader()
    }

    private fun disconnectReader() {
        try {
            if (this::rfidReader.isInitialized && rfidReader.isConnected) {
                rfidReader.disconnect()
                Log.d(TAG, "Leitor desconectado")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Erro ao desconectar o leitor: ${e.message}")
        }
    }

    override fun eventReadNotify(data: RfidReadEvents?) {
        data.let { epc ->
            GlobalScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main){
                    binding.textRfid.append("Tag recebida: ${epc!!.readEventData.tagData.tagID}\n")
                }
            }

        }
    }

    override fun eventStatusNotify(event: RfidStatusEvents?) {
        event?.let {
            val handheldEvent = it.StatusEventData.HandheldTriggerEventData
            if (handheldEvent != null) {
                if (handheldEvent.handheldEvent.value == 1) {
                    // Gatilho pressionado, iniciar leitura
                    startReading()
                } else if (handheldEvent.handheldEvent.value == 0) {
                    // Gatilho liberado, parar leitura
                    stopReading()
                }
            }
        }
    }

    private fun stopReading() {
        try {
            rfidReader.Actions.Inventory.stop() // Para a leitura
            Log.d(TAG, "Leitura de tags parada")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Erro ao parar leitura: ${e.message}")
        }
    }
}
