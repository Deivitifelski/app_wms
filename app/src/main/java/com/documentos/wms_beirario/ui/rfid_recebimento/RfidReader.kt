package com.documentos.wms_beirario.ui.rfid_recebimento

import android.content.Context
import com.zebra.rfid.api3.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RfidReader private constructor()  {
    private var reader: Readers? = null
    private var rfidReader: RFIDReader? = null
    private var readerList: ArrayList<ReaderDevice>? = null

    companion object {
        @Volatile
        private var INSTANCE: RfidReader? = null

        fun getInstance(): RfidReader {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RfidReader().also { INSTANCE = it }
            }
        }
    }

    fun connectReader(context: Context, reconectando: Boolean = false, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            if (reconectando) {
                // Exemplo de como você pode mostrar o estado na UI
                // Aqui você pode usar um binding se estiver utilizando ViewBinding ou Jetpack Compose
                // binding.iconRfidSinal.isVisible = false
                // binding.progressRfid.visibility = View.VISIBLE
                // toastDefault(message = "Buscando Leitor de RFID...")
            }
            // Inicie a operação assíncrona
            withContext(Dispatchers.IO) {
                try {
                    reader = Readers(context, ENUM_TRANSPORT.SERVICE_USB)
                    readerList = reader?.GetAvailableRFIDReaderList()

                    if (readerList.isNullOrEmpty()) {
                        withContext(Dispatchers.Main) {
                            onFailure("Não foi possível conectar, lista de leitores vazia")
                        }
                        return@withContext
                    }

                    val readerDevice: ReaderDevice = readerList!![0]
                    rfidReader = readerDevice.rfidReader
                    withContext(Dispatchers.Main) {
                        // Você pode mostrar um progresso aqui
                        // val progressConnection = progressConected("Conectando a ${readerDevice.name}")
                        // progressConnection.show()

                        rfidReader?.connect()
                        if (rfidReader?.isConnected == true) {
                            onSuccess()
                        } else {
                            onFailure("Não foi possível conectar ao leitor")
                        }
                        // progressConnection.dismiss()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        // progressConnection.dismiss()
                        onFailure("Ocorreu um erro ao conectar:\n${e.localizedMessage}")
                    }
                }
            }
        }
    }

    fun disconnect() {
        rfidReader?.disconnect()
        rfidReader = null
        reader = null
    }

    fun isConnected(): Boolean {
        return rfidReader?.isConnected == true
    }
}
