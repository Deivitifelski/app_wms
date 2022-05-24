package com.documentos.wms_beirario.ui.bluetooh

import android.R
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.*
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.documentos.wms_beirario.data.CustomSharedPreferences
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
import com.zebra.sdk.printer.*
import java.util.*
import kotlin.collections.ArrayList


class BluetoohPrinterActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityBluetoohPrinterBinding
    private val TAG = "BLUETOOH_ACTIVITY"
    private lateinit var listView: ListView
    private lateinit var mALert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private val REQUEST_ENABLE_BT = 1
    private val mDeviceList = ArrayList<String>()
    private var m_bluetoothSocket: BluetoothSocket? = null
    var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private var bluetoothDeviceAddress = mutableListOf<String>()
    lateinit var printerConnection: PrinterConnection
    private val mListBluetoohPaired: ArrayList<BluetoothDevice> = ArrayList()
    private lateinit var mShared: CustomSharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityBluetoohPrinterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)


        initConst()
        setToolbar()
        sutupButtons()
        checkBluetoothDisabled()
        mBluetoothAdapter.startDiscovery()
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
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
                mBluetoothAdapter.cancelDiscovery()
                listView.adapter = ArrayAdapter(
                    this@BluetoohPrinterActivity,
                    R.layout.simple_list_item_1, mDeviceList
                )
                mBluetoothAdapter.startDiscovery()
                isRefreshing = false
            }
        }
    }


    private fun initConst() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mShared = CustomSharedPreferences(this)
        mBinding.progress.isVisible = true
        listView = mBinding.listView
        mALert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    private fun listPaired() {
        val paired: Set<BluetoothDevice>? = mBluetoothAdapter.bondedDevices
        paired!!.forEach { device ->
            mListBluetoohPaired.add(device)
        }
    }

    private fun sutupButtons() {
        /** ATUALIZAR -->*/
        mBinding.btAtualizar.setOnClickListener {
            mDeviceList.clear()
            mBluetoothAdapter.cancelDiscovery()
            listView.adapter = ArrayAdapter(
                this@BluetoohPrinterActivity,
                R.layout.simple_list_item_1, mDeviceList
            )
            mBluetoothAdapter.startDiscovery()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                if (!mBluetoothAdapter.isEnabled) {
                    Toast.makeText(this, "Bluetooh não iniciado!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this,
                        "Bluetooh Ativado!\nClique em atualizar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooh Desativado!", Toast.LENGTH_SHORT).show()
                mBinding.linearTitle.text = "Bluetooh não ativado!\nAtive e clique em atualizar"
                mBinding.progress.isVisible = false
            }
        }
    }


    /** CLIQUE NO ITEM DA LISTA --> */
    private fun clickItemBluetooh() {
        listView.setOnItemClickListener { _, _, position, _ ->
            if (m_bluetoothSocket == null) {
                val device = mBluetoothAdapter.getRemoteDevice(bluetoothDeviceAddress[position])
                m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                SetupNamePrinter.mNamePrinterString = bluetoothDeviceAddress[position]
                device.createBond()
                mBluetoothAdapter.cancelDiscovery()
                setupCalibrar()
                Handler(Looper.getMainLooper()).postDelayed({ onBackPressed() }, 600)
                mToast.toastCustomSucess(
                    this,
                    "Impressora selecionada: ${bluetoothDeviceAddress[position]}"
                )
                Handler(Looper.getMainLooper()).postDelayed({ onBackPressed() }, 250)
            }
        }
    }


    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                mDeviceList.add(device!!.name + "\n" + device.address)
                mBinding.progress.isVisible = !mDeviceList.isNotEmpty()
                bluetoothDeviceAddress.add(device.toString())
                mBinding.linearTitle.text = "Selecione um Dispositivo:"
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
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
                Toast.makeText(this@BluetoohPrinterActivity, "CONECTADO!", Toast.LENGTH_SHORT)
                    .show()
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
            if (m_bluetoothSocket == null) {
                val device = mBluetoothAdapter.getRemoteDevice(deviceandress)
                m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                SetupNamePrinter.mNamePrinterString = deviceandress!!
                device.createBond()
                mBluetoothAdapter.cancelDiscovery()
                setupCalibrar()
                Handler(Looper.getMainLooper()).postDelayed({ onBackPressed() }, 600)
                mToast.toastCustomSucess(
                    this,
                    "Impressora selecionada: $deviceandress"
                )
                mShow.dismiss()
                Handler(Looper.getMainLooper()).postDelayed({ onBackPressed() }, 250)
            }
        }
        /**BUTTON NAO ->*/
        mBindingAlert.buttonNaoImpressora1.setOnClickListener {
            CustomMediaSonsMp3().somClick(context)
            mShow.dismiss()
        }
    }

    private fun setupCalibrar() {
        printerConnection = PrinterConnection(SetupNamePrinter.mNamePrinterString)
        try {
            val zpl =
                "! U1 SPEED 1\\n! U1 setvar \"print.tone\" \"20\"\\n ! U1 setvar \"media.type\" \"label\"\\n ! U1 setvar \"device.languages\" \"zpl\"\\n ! U1 setvar \"media.sense_mode\" \"gap\"\\n ~jc^xa^jus^xz\\n"
            printerConnection.sendZplBluetooth(
                zpl,
                null
            )

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

