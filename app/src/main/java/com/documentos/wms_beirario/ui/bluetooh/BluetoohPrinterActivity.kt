package com.documentos.wms_beirario.ui.bluetooh

import android.R
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.documentos.wms_beirario.databinding.ActivityBluetoohPrinterBinding
import com.documentos.wms_beirario.databinding.LayoutCustomImpressoraBinding
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.ConnectionException
import com.zebra.sdk.printer.PrinterLanguage
import com.zebra.sdk.printer.ZebraPrinter
import com.zebra.sdk.printer.ZebraPrinterFactory
import java.io.IOException

class BluetoohPrinterActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityBluetoohPrinterBinding
    private lateinit var listView: ListView
    private lateinit var mALert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private val REQUEST_ENABLE_BT = 1
    private val mDeviceList = ArrayList<String>()
    private val mBluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothDeviceAddress = mutableListOf<String>()
    lateinit var printerConnection: PrinterConnection
    private val mListBluetoohPaired: ArrayList<BluetoothDevice> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityBluetoohPrinterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)


        initConst()
        setToolbar()
        sutupButtons()
        checkBluetoothDisabled()
        mBluetoothAdapter?.startDiscovery()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)
        Log.e("TAG", "onCreate -> $filter || $mReceiver ")
        clickItemBluetooh()
        listPaired()
        reflesh()

    }

    override fun onRestart() {
        super.onRestart()
        mBinding.progress.isVisible = false
    }

    private fun setToolbar() {
        mBinding.toolbar6.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun reflesh() {
        mBinding.reflesh.apply {
            setColorSchemeColors(getColor(com.documentos.wms_beirario.R.color.color_default))
            setOnRefreshListener {
                mDeviceList.clear()
                mBluetoothAdapter?.cancelDiscovery()
                listView.adapter = ArrayAdapter(
                    this@BluetoohPrinterActivity,
                    R.layout.simple_list_item_1, mDeviceList
                )
                mBluetoothAdapter?.startDiscovery()
                isRefreshing = false
            }
        }
    }

    private fun initConst() {
        printerConnection = PrinterConnection()
        mBinding.progress.isVisible = true
        listView = mBinding.listView
        mALert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    private fun listPaired() {
        val paired: Set<BluetoothDevice>? = mBluetoothAdapter!!.bondedDevices
        paired!!.forEach { device ->
            mListBluetoohPaired.add(device)
        }
    }

    private fun sutupButtons() {
        /** ATUALIZAR -->*/
        mBinding.btAtualizar.setOnClickListener {
            mDeviceList.clear()
            mBluetoothAdapter?.cancelDiscovery()
            listView.adapter = ArrayAdapter(
                this@BluetoohPrinterActivity,
                R.layout.simple_list_item_1, mDeviceList
            )
            mBluetoothAdapter?.startDiscovery()
            mBinding.progress.isVisible = true
            Handler(Looper.getMainLooper()).postDelayed({
                mBinding.progress.isVisible = false
            }, 500)
        }
        /** CALIBRAR -->*/
        mBinding.btCalibrar.setOnClickListener {
            setupCalibrar()
        }
        /** BUTTON CONCLUIR -->*/
        mBinding.btConcluido.setOnClickListener {
            onBackTransitionExtension()
        }
    }

    /**VERIFICA SE O BLUETOOH ESTA LIGADO ->*/
    private fun checkBluetoothDisabled() {
        if (mBluetoothAdapter == null) {
            mALert.alertMessageAtencao(
                this,
                getString(com.documentos.wms_beirario.R.string.support_bluetooth)
            )
        } else if (!mBluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    private fun clickItemBluetooh() {
        listView.setOnItemClickListener { _, _, position, _ ->
            SetupNamePrinter.applicationPrinterAddress = bluetoothDeviceAddress[position]
            mBluetoothAdapter!!.cancelDiscovery()
            printAction(bDeviceAddress = bluetoothDeviceAddress[position], content = "content")
        }
    }


    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                mDeviceList.add(device!!.name + "\n" + device.address)
                mBinding.progress.isVisible = !mDeviceList.isNotEmpty()
                bluetoothDeviceAddress.add(device.toString())
                if (mListBluetoohPaired.containsAll(listOf(device))) {
                    alertDialogBluetoohSelecionado(
                        this@BluetoohPrinterActivity,
                        device.name,
                        device.address,
                        text = "Impressora conectada anteriormente disponivel,deseja conectar com:",
                        device
                    )
                }
                Log.i("BT", device.name + "\n" + device.address)
                listView.adapter = ArrayAdapter(
                    this@BluetoohPrinterActivity,
                    R.layout.simple_list_item_1, mDeviceList
                )
            }
        }

    }

    fun alertDialogBluetoohSelecionado(
        context: Context,
        devicename: String? = "",
        deviceandress: String? = "",
        text: String = "Deseja selecionar essa impressora",
        device: BluetoothDevice
    ) {
        vibrateExtension(500)
        val mAlert = AlertDialog.Builder(context)
        CustomMediaSonsMp3().somAtencao(context)
        val mBindingAlert = LayoutCustomImpressoraBinding.inflate(LayoutInflater.from(context))
        mAlert.setView(mBindingAlert.root)
        mBindingAlert.textImpressoar1.textSize = 16F
        mAlert.setCancelable(false)
        val mShow = mAlert.show()
        try {
            if (devicename!!.isEmpty() || devicename == "" || devicename.isNullOrEmpty()) {
                mBindingAlert.textImpressoar1.text = text +
                        "$deviceandress?"

            } else {
                mBindingAlert.textImpressoar1.text =
                    "$text\n $devicename ?"
            }

        } catch (e: Exception) {
            mBindingAlert.textImpressoar1.text = "Deseja selecionar essa impressora?"
            mAlert.setCancelable(false)
            val mShow = mAlert.show()
        }
        /**BUTTON SIM ->*/
        mBindingAlert.buttonSimImpressora1.setOnClickListener {
            SetupNamePrinter.applicationPrinterAddress = deviceandress!!
            mBluetoothAdapter!!.cancelDiscovery()
            device.createBond()
            CustomMediaSonsMp3().somClick(context)
            CustomSnackBarCustom().toastCustomSucess(
                context, "Impressora Selecionada!"
            )
            mShow.dismiss()
            Handler(Looper.getMainLooper()).postDelayed({
                onBackPressed()
            }, 1000)


        }
        /**BUTTON NAO ->*/
        mBindingAlert.buttonNaoImpressora1.setOnClickListener {
            CustomMediaSonsMp3().somClick(context)
            mShow.dismiss()

        }
    }


    private fun printAction(bDeviceAddress: String, content: String): ZebraPrinter? {
        var printer: ZebraPrinter? = null
        val printerConnection = BluetoothConnection(bDeviceAddress)
        try {
            printerConnection.open()
            if (printer == null) {
                printer = ZebraPrinterFactory.getInstance(PrinterLanguage.CPCL, printerConnection)
            }
            sendToPrint(printer!!, content)
            printerConnection.close()
        } catch (e: ConnectionException) {
            Log.d("ERROR - ", e.message.toString())
        } finally {

        }
        return printer
    }

    private fun sendToPrint(printer: ZebraPrinter, content: String) {
        try {
            val filepath = getFileStreamPath("TEMP.LBL")
            createFile("TEMP.LBL", content)
            printer.sendFileContents(filepath.absolutePath)
        } catch (e1: ConnectionException) {
            Log.d("ERROR - ", "Error sending file to printer")
        } catch (e: IOException) {
            Log.d("ERROR - ", "Error creating file")
        }
    }

    @Throws(IOException::class)
    private fun createFile(fileName: String, content: String) {
        val os = this.openFileOutput(fileName, Context.MODE_APPEND)
        os.write(content.toByteArray())
        os.flush()
        os.close()
    }

    private fun setupCalibrar() {
        try {
            val zpl =
                "! U1 SPEED 1\\n! U1 setvar \"print.tone\" \"20\"\\n ! U1 setvar \"media.type\" \"label\"\\n ! U1 setvar \"device.languages\" \"zpl\"\\n ! U1 setvar \"media.sense_mode\" \"gap\"\\n ~jc^xa^jus^xz\\n"
            printerConnection.printZebra(zpl, SetupNamePrinter.applicationPrinterAddress)

        } catch (e: Throwable) {
            mErrorToast("Não foi possível calibrar a impressora.")
        }
    }

    private fun mErrorToast(msg: String) {
        vibrateExtension(500)
        mToast.toastCustomError(
            this,
            msg
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }
}