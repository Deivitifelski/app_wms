package com.documentos.wms_beirario.ui.configuracoes

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.Connection
import android.widget.Toast
import com.zebra.sdk.comm.ConnectionException


class PrinterConnection(macAddress: String, val context: Context? = null) {
    private val thePrinterConn: Connection = BluetoothConnection(macAddress).apply {
        timeToWaitForMoreData = 5000
    }

    fun sendZplBluetooth(
        zplData: String? = null,
        mListZpl: List<String>? = null
    ) {
        Thread {
            try {
                Looper.prepare()
                thePrinterConn.open()
                Log.e("PRINTER", "ESTA CONECTADO? -->  ${thePrinterConn.isConnected} ")
                if (zplData != null) {
                    thePrinterConn.write(zplData.toByteArray())
                }
                if (mListZpl!!.isNotEmpty()) {
                    val listSize = mListZpl.size
                    for (i in 0 until listSize) {
                        thePrinterConn.open()
                        thePrinterConn.write(mListZpl[i].toByteArray())
                        thePrinterConn.close()
                    }
                }
                // Certifique-se de que os dados chegaram à impressora antes de fechar a conexão
                Thread.sleep(1000)
                thePrinterConn.close()
                Log.e("PRINTER", "PRINTER ESPERA --> ${thePrinterConn.timeToWaitForMoreData}")
                // Fecha a conexão para liberar recursos.
            } catch (e: java.lang.Exception) {
                // Trata o erro de comunicação aqui.
                e.printStackTrace()
            }
        }.start()
    }


    fun sendZplOverBluetooth(
        zplData: String? = null,
        mListZpl: List<String>? = null
    ) {
        Thread {
            try {
                // Instantiate insecure connection for given Bluetooth&reg; MAC Address.
                // Initialize
                thePrinterConn.maxTimeoutForRead = 1000
                thePrinterConn.timeToWaitForMoreData = 1000
                Looper.prepare()
                // Open the connection - physical connection is established here.
                thePrinterConn.open()
                // Send the data to printer as a byte array.
                thePrinterConn.write(zplData?.toByteArray())
                thePrinterConn.close()
                // Make sure the data got to the printer before closing the connection
                Thread.sleep(1000)
                // Close the insecure connection to release resources.
                Looper.myLooper()!!.quit()
            } catch (e: Exception) {
                // Handle communications error here.
                e.printStackTrace()
            }
        }.start()
    }
}