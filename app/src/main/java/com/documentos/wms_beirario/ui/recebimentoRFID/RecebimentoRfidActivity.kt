package com.documentos.wms_beirario.ui.recebimentoRFID

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.documentos.wms_beirario.databinding.ActivityRecebimentoRfidBinding
import com.documentos.wms_beirario.utils.extensions.somSucess
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.RFIDReader
import com.zebra.rfid.api3.ReaderDevice
import com.zebra.rfid.api3.Readers
import com.zebra.rfid.api3.RfidEventsListener
import com.zebra.rfid.api3.RfidReadEvents
import com.zebra.rfid.api3.RfidStatusEvents

class RecebimentoRfidActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecebimentoRfidBinding
    private lateinit var rfidReader: RFIDReader
    private val TAG = "com.documentos.wms_beirario.ui.recebimentoRFID.RecebimentoRfidActivity"
    private var readers: Readers? = null
    private var readerList: ArrayList<ReaderDevice>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecebimentoRfidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar a lista de leitores
        initReaders()
        // Chama o método de conexão ao leitor ao iniciar a Activity
        connectReader()
    }

    private fun initReaders() {
        try {
            // Inicializar o gerenciador de leitores (Readers) com o contexto da aplicação
            readers = Readers(this, ENUM_TRANSPORT.SERVICE_SERIAL)
            Log.d(TAG, "Readers inicializados.")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Erro ao inicializar readers: ${e.message}")
            Toast.makeText(this, "Erro ao inicializar readers: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun connectReader() {
        try {
            // Obter a lista de leitores disponíveis
            readerList = readers?.GetAvailableRFIDReaderList()

            if (readerList != null && readerList!!.isNotEmpty()) {
                val readerDevice: ReaderDevice = readerList!![0]
                rfidReader = readerDevice.rfidReader

                // Conectar ao leitor
                rfidReader.connect()

                if (rfidReader.isConnected) {
                    startReading()
                    configureReader()
                }
            } else {
                Toast.makeText(this, "Nenhum leitor RFID encontrado", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Erro ao conectar ao leitor: ${e.message}")
            Toast.makeText(this, "Erro ao conectar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configureReader() {
        try {
            val antennaConfig = rfidReader.Config.Antennas.getAntennaRfConfig(1)
            antennaConfig.transmitPowerIndex = 270
            rfidReader.Config.Antennas.setAntennaRfConfig(1, antennaConfig)
            rfidReader.Events.setHandheldEvent(true)
            rfidReader.Events.addEventsListener(object : RfidEventsListener {
                override fun eventReadNotify(e: RfidReadEvents?) {
                    // Receber as tags lidas
                    handleTagRead()
                }

                override fun eventStatusNotify(e: RfidStatusEvents?) {

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Erro ao configurar o leitor: ${e.message}")
            Toast.makeText(this, "Erro ao configurar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleTagRead() {
        val tags = rfidReader.Actions.getReadTags(100)
        if (tags != null) {
            for (tag in tags) {
                somSucess()
                Log.d(TAG, "Tag lida: ${tag.tagID}")
            }
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

    private fun stopReading() {
        try {
            rfidReader.Actions.Inventory.stop()
            Log.d(TAG, "Leitura interrompida")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Erro ao parar leitura: ${e.message}")
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
}
