package com.documentos.wms_beirario.ui.bluetooh

import android.os.Looper

import com.zebra.sdk.comm.BluetoothConnectionInsecure
import com.zebra.sdk.comm.Connection
import java.lang.Exception


class BluetoothConnectionInsecureExample {
    fun sendZplOverBluetooth(theBtMacAddress: String) {
        Thread {
            try {
                // Instantiate insecure connection for given Bluetooth&reg; MAC Address.
                val thePrinterConn: Connection = BluetoothConnectionInsecure(theBtMacAddress)

                // Initialize
                Looper.prepare()

                // Open the connection - physical connection is established here.
                thePrinterConn.open()

                // This example prints "This is a ZPL test." near the top of the label.
                val zplData = "^XA^FO20,20^A0N,25,25^FDThis is a ZPL test.^FS^XZ"

                // Send the data to printer as a byte array.
                thePrinterConn.write(zplData.toByteArray())

                // Make sure the data got to the printer before closing the connection
                Thread.sleep(500)

                // Close the insecure connection to release resources.
                thePrinterConn.close()
                Looper.myLooper()!!.quit()
            } catch (e: Exception) {
                // Handle communications error here.
                e.printStackTrace()
            }
        }.start()
    }

    fun sendCpclOverBluetooth(theBtMacAddress: String) {
        Thread {
            try {

                // Instantiate insecure connection for given Bluetooth&reg; MAC Address.
                val thePrinterConn: Connection = BluetoothConnectionInsecure(theBtMacAddress)

                // Initialize
                Looper.prepare()

                // Open the connection - physical connection is established here.
                thePrinterConn.open()

                // This example prints "This is a CPCL test." near the top of the label.
                val cpclData = """
                    ! 0 200 200 210 1
                    TEXT 4 0 30 40 This is a CPCL test.
                    FORM
                    PRINT
                    
                    """.trimIndent()

                // Send the data to printer as a byte array.
                thePrinterConn.write(cpclData.toByteArray())

                // Make sure the data got to the printer before closing the connection
                Thread.sleep(500)

                // Close the insecure connection to release resources.
                thePrinterConn.close()
                Looper.myLooper()!!.quit()
            } catch (e: Exception) {

                // Handle communications error here.
                e.printStackTrace()
            }
        }.start()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val example = BluetoothConnectionInsecureExample()
            val theBtMacAddress = "00:11:BB:DD:55:FF"
            example.sendZplOverBluetooth(theBtMacAddress)
            example.sendCpclOverBluetooth(theBtMacAddress)
        }
    }
}
