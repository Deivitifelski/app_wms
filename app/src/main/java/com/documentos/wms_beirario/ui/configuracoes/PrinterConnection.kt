package com.documentos.wms_beirario.ui.configuracoes

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.BluetoothConnectionInsecure
import com.zebra.sdk.comm.Connection
import com.zebra.sdk.printer.ZebraPrinterFactory
import com.zebra.sdk.printer.ZebraPrinterLinkOs


class PrinterConnection {
    fun printZebra(zpl: String, macAddress: String) {
        Thread {
            try {
                val thePrinterConn: Connection = BluetoothConnection(macAddress)
                Looper.prepare()
                thePrinterConn.open()
                thePrinterConn.write(zpl.toByteArray())
                Thread.sleep(500)
                thePrinterConn.close()
                Looper.myLooper()!!.quit()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun printWithoutPairingZebra(zplData: String, macAddress: String) {
        Thread {
            try {
                val thePrinterConn: Connection =
                    BluetoothConnectionInsecure(macAddress)
                Looper.prepare()
                thePrinterConn.open()
                thePrinterConn.write(zplData.toByteArray())
                Thread.sleep(500)
                thePrinterConn.close()
                Looper.myLooper()!!.quit()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun changeSettings(macAddress: String, tone: String, speed: String) {
        try {
            val thePrinterConn: Connection =
                BluetoothConnectionInsecure(macAddress)

            val linkOsPrinter: ZebraPrinterLinkOs =
                ZebraPrinterFactory.getLinkOsPrinter(thePrinterConn)
            linkOsPrinter.setSetting("print.tone", tone)

            thePrinterConn.open()
            thePrinterConn.write(speed.toByteArray())
            Thread.sleep(500)
            thePrinterConn.close()
            linkOsPrinter.setSetting("media.speed", speed)

        } catch (e: Throwable) {
            Log.d("", "erro" + e)
        }

    }

    fun findPrinters(applicationContext: Context): MutableList<String> {
//        val discoveryHandler: DiscoveryHandler = object : DiscoveryHandler {
//            var printers: MutableList<DiscoveredPrinter> = ArrayList<DiscoveredPrinter>()
//            override fun foundPrinter(printer: DiscoveredPrinter) {
//                printers.add(printer)
//            }
//
//            override fun discoveryFinished() {
//                for (printer in printers) {
//                    System.out.println(printer)
//                }
//                println("Discovered " + printers.size + " printers.")
//                Log.d("printers", "Achou isso ${printers.toString()}")
//            }
//
//            override fun discoveryError(message: String) {
//                println("An error occurred during discovery : $message")
//            }
//        }
//        try {
//            println("Starting printer discovery.")
//            NetworkDiscoverer.findPrinters(discoveryHandler)
//        } catch (e: DiscoveryException) {
//            e.printStackTrace()
//        }
        var ListMacModel = mutableListOf<String>()
        ListMacModel.add("-Selecione a Impressora-")

        val bluetoothManager =
            applicationContext.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager

        var pairedDevices = bluetoothManager.adapter.bondedDevices
        for (device: BluetoothDevice in pairedDevices) {
            ListMacModel.add("Nome: ${device.name} Mac: ${device.address}")
        }
        return ListMacModel
    }
}