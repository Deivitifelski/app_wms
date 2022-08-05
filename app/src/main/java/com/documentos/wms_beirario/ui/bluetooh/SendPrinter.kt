package com.documentos.wms_beirario.ui.bluetooh

import android.content.Context
import android.util.Log
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.ConnectionException
import com.zebra.sdk.printer.PrinterLanguage
import com.zebra.sdk.printer.ZebraPrinter
import com.zebra.sdk.printer.ZebraPrinterFactory
import java.io.IOException

class SendPrinter(val context: Context) {

    fun printAction(bDeviceAddress: String, content: String): ZebraPrinter? {
        var printer: ZebraPrinter? = null
        val printerConnection = BluetoothConnection(bDeviceAddress)
        try {
            printerConnection.open()
            if (printer == null) {
                printer = ZebraPrinterFactory.getInstance(PrinterLanguage.ZPL, printerConnection)
            }
            sendToPrint(printer!!, content)
            printerConnection.close()
        } catch (e: ConnectionException) {
            Log.d("ERROR - ", e.message!!)
        } finally {
            printerConnection.close()
        }
        return printer
    }

    private fun sendToPrint(printer: ZebraPrinter, content: String) {
        try {
//            val filepath = context.getFileStreamPath("TEMP.LBL")
//            createFile("TEMP.LBL", content)
//            printer.sendFileContents(filepath.absolutePath)
            printer.sendFileContents(content)
        } catch (e1: ConnectionException) {
            Log.d("ERROR - ", "Error sending file to printer")
        } catch (e: IOException) {
            Log.d("ERROR - ", "Error creating file")
        }
    }

    @Throws(IOException::class)
    private fun createFile(fileName: String, content: String) {
        val os = context.openFileOutput(fileName, Context.MODE_APPEND)
        os.write(content.toByteArray())
        os.flush()
        os.close()
    }
}