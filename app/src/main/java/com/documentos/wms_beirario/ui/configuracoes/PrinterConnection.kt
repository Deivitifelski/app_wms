package com.documentos.wms_beirario.ui.configuracoes

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.BluetoothConnectionInsecure
import com.zebra.sdk.comm.Connection
import com.zebra.sdk.printer.ZebraPrinterFactory
import com.zebra.sdk.printer.ZebraPrinterLinkOs


class PrinterConnection(macAddress: String) {
    private val thePrinterConn: Connection = BluetoothConnection(macAddress)
    fun printZebra(context: Context? = null, zpl: String? = null) {
        Thread {
            try {
                Looper.prepare()
                thePrinterConn.open()
                thePrinterConn.write(zpl!!.toByteArray())
                Thread.sleep(50)
                thePrinterConn.close()
                Looper.myLooper()!!.quit()
                if (context != null) {
                    Toast.makeText(context, "Imprimindo...", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun printZebraLoop(context: Context? = null, mListZpl: List<String>) {
        Thread {
            try {
                val listSize = mListZpl.size
                if (mListZpl.isNotEmpty()) {
                    if (mListZpl.size == 1) {
                        thePrinterConn.open()
                        thePrinterConn.write(mListZpl[0].toByteArray())
                        thePrinterConn.close()
                    }
                    for (i in 0 until listSize) {
                        thePrinterConn.open()
                        thePrinterConn.write(mListZpl[i].toByteArray())
                        thePrinterConn.close()
                    }
                    if (context != null) {
                        Toast.makeText(context, "Imprimindo...", Toast.LENGTH_SHORT).show()
                    }
                }
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

    fun sendZplBluetooth(zplData: String? = null,mListZpl: List<String>? = null) {
        Thread {
            try {
                // Initialize
                Looper.prepare()
                thePrinterConn.maxTimeoutForRead = 200
                thePrinterConn.timeToWaitForMoreData = 100
                // Abra a conexão - a conexão física é estabelecida aqui.
                // Envia os dados para a impressora como um array de bytes.
                thePrinterConn.open()
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
                Thread.sleep(500)
                Log.e("PRINTER", "PRINTER ESPERA --> ${thePrinterConn.timeToWaitForMoreData}")
                // Fecha a conexão para liberar recursos.
                thePrinterConn.close()
                Looper.myLooper()!!.quit()
            } catch (e: java.lang.Exception) {
                // Trata o erro de comunicação aqui.
                e.printStackTrace()
            }
        }.start()
    }

}