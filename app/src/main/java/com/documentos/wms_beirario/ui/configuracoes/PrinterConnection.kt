package com.documentos.wms_beirario.ui.configuracoes

import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothServerSocket
import android.os.Looper
import android.system.Os.close
import android.util.Log
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.Connection
import com.zebra.sdk.printer.PrinterLanguage
import com.zebra.sdk.printer.ZebraPrinter
import com.zebra.sdk.printer.ZebraPrinterFactory
import okhttp3.internal.platform.android.AndroidLogHandler.close


class PrinterConnection() {
    private val thePrinterConn: Connection =
        BluetoothConnection(SetupNamePrinter.mNamePrinterString)

    fun sendZplOverBluetoothNet(zplData: String) {
        try {
            var printer: ZebraPrinter? = null
            thePrinterConn.open()
            if (printer == null) {
                printer = ZebraPrinterFactory.getInstance(PrinterLanguage.ZPL, thePrinterConn)
            }
            if (thePrinterConn.isConnected) {
                thePrinterConn.write(zplData.toByteArray())
                thePrinterConn.close()
            } else {
                Log.e("PRINTER", "sendZplOverBluetooth: erro ao tentar imprimir")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun sendZplOverBluetoothListNet(listzplData: MutableList<String>) {
        try {
            var printer: ZebraPrinter? = null
            thePrinterConn.open()
            if (printer == null) {
                printer = ZebraPrinterFactory.getInstance(PrinterLanguage.ZPL, thePrinterConn)
            }
            if (thePrinterConn.isConnected) {
                listzplData.forEach { zpl ->
                    thePrinterConn.write(zpl.toByteArray())
                }
                thePrinterConn.close()
            } else {
                Log.e("PRINTER", "sendZplOverBluetooth: erro ao tentar imprimir")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
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
                thePrinterConn.maxTimeoutForRead = 10000
                thePrinterConn.timeToWaitForMoreData = 10000
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