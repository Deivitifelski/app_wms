package com.documentos.wms_beirario.ui.bluetooh

import android.Manifest
import android.R
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityBluetoohPrinterBinding
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import com.github.douglasjunior.bluetoothclassiclibrary.*
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
    private val mDeviceListAdress = ArrayList<String>()
    private val mListBluetoohSelect: ArrayList<BluetoothDevice> = ArrayList()
    private var bluetoothDeviceAddress = mutableListOf<String>()
    private lateinit var mShared: CustomSharedPreferences
    private var service: BluetoothService? = null
    private lateinit var writer: BluetoothWriter
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mDeviceShared: String

    companion object {
        var STATUS = ""
        var NAME_DEVICE_CONNECTED = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityBluetoohPrinterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mSharedPreferences = CustomSharedPreferences(this)
        mDeviceShared =
            mSharedPreferences.getString(CustomSharedPreferences.DEVICE_PRINTER).toString() ?: ""
        val config = BluetoothConfiguration()
        config.context = applicationContext
        config.bluetoothServiceClass = BluetoothClassicService::class.java
        config.bufferSize = 1024
        config.characterDelimiter = '\n'
        config.deviceName = "Your App Name"
        config.callListenersInMainThread = true
        config.uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        BluetoothClassicService.init(config)
        service = BluetoothClassicService.getDefaultInstance()
        writer = BluetoothWriter(service)
        setupBluetoohStatus()
        initConst()
        setupPermission()
        setToolbar()
        checkBluetoothDisabled()
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addCategory(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addCategory(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        this.registerReceiver(receiver, filter)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            mBluetoothAdapter.startDiscovery()
        }

        Log.e("TAG", "onCreate -> $filter || $receiver ")
        clickItemBluetooh()
        reflesh()
    }

    private fun setupBluetoohStatus() {
        service?.setOnEventCallback(object : BluetoothService.OnBluetoothEventCallback {
            override fun onDataRead(buffer: ByteArray?, length: Int) {

            }

            override fun onStatusChange(status: BluetoothStatus?) {
                STATUS = status.toString()
                when {
                    status.toString() == "NONE" -> {
                        mBinding.btCalibrar.isEnabled = false
                        mBinding.linearTitleText.apply {
                            setTextColor(Color.BLACK)
                            text = "Selecione um dispositivo"
                        }
                    }
                    status.toString() == "CONNECTED" -> {
                        mDeviceShared =
                            mSharedPreferences.getString(CustomSharedPreferences.DEVICE_PRINTER)
                                .toString()
                        mBinding.btCalibrar.isEnabled = true
                        Handler(Looper.myLooper()!!).postDelayed({
                            mBinding.linearTitleText.apply {
                                setTextColor(getColor(R.color.holo_green_dark))
                                text = "Conectado com: $mDeviceShared"
                            }
                        }, 1000)
                    }

                    status.toString() == "CONNECTING" -> {
                        mBinding.btCalibrar.isEnabled = false
                        mBinding.linearTitleText.apply {
                            setTextColor(Color.RED)
                            text = "Tentando se conectar"
                        }
                    }
                    else -> {
                        mBinding.btCalibrar.isEnabled = false
                        Toast.makeText(
                            this@BluetoohPrinterActivity,
                            "Desconectado",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        mBinding.linearTitleText.text = "Selecione um dispositivo"
                    }
                }
            }

            override fun onDeviceName(deviceName: String?) {
                NAME_DEVICE_CONNECTED = deviceName.toString()
                mSharedPreferences.saveString(
                    CustomSharedPreferences.DEVICE_PRINTER,
                    deviceName.toString()
                )

            }

            override fun onToast(message: String?) {

            }

            override fun onDataWrite(buffer: ByteArray?) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        sutupButtons()
        if (STATUS == "CONNECTED") {
            mBinding.linearTitleText.apply {
                setTextColor(getColor(R.color.holo_green_dark))
                text = "Conectado com:$NAME_DEVICE_CONNECTED"
            }
        }
    }

    private fun setupPermission() {
        val permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
        val permission2 =
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
        val permission3 =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val permission4 =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission1 != PackageManager.PERMISSION_GRANTED
            || permission2 != PackageManager.PERMISSION_GRANTED
            || permission3 != PackageManager.PERMISSION_GRANTED
            || permission4 != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                642
            )
        } else {
            Log.e("DISCOVERING-PERMISSIONS", "Permissions Granted")
        }
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
                if (ActivityCompat.checkSelfPermission(
                        this@BluetoohPrinterActivity,
                        Manifest.permission.BLUETOOTH_SCAN
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
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
    }


    private fun initConst() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mShared = CustomSharedPreferences(this)
        mBinding.progress.isVisible = true
        listView = mBinding.listView
        mALert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()

    }

    private fun sutupButtons() {
        /** ATUALIZAR -->*/
        mBinding.btAtualizar.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this@BluetoohPrinterActivity,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (mBluetoothAdapter == null) {
                    mALert.alertMessageAtencao(
                        this,
                        getString(com.documentos.wms_beirario.R.string.support_bluetooth)
                    )
                } else if (!mBluetoothAdapter.isEnabled) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                } else {
                    mBinding.linearTitleText.text = "Selecione um dispositivo"
                    mDeviceList.clear()
                    mListBluetoohSelect.clear()
                    mDeviceListAdress.clear()
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
    }


    /**VERIFICA SE O BLUETOOH ESTA LIGADO ->*/
    private fun checkBluetoothDisabled() {
        if (ActivityCompat.checkSelfPermission(
                this@BluetoohPrinterActivity,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                if (!mBluetoothAdapter.isEnabled) {
                    Toast.makeText(this, "Bluetooh não iniciado!", Toast.LENGTH_SHORT).show()
                } else {
                    alertBluetoohEnablueTrue()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooh Desativado!", Toast.LENGTH_SHORT).show()
                mBinding.linearTitleText.text = "Bluetooh não ativado!\nAtive e clique em atualizar"
                mBinding.progress.isVisible = false
            }
        }
    }

    /**FUNÇÃO CASO BLUETOOH SEJA ATIVADO APÓS INICIO DA TELA--> */
    private fun alertBluetoohEnablueTrue() {
        val mAlertDialog = android.app.AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("Bluetooh ativado")
            .setIcon(com.documentos.wms_beirario.R.drawable.ic_bluetooth_24)
            .setMessage("Clique OK para reiniciar a buscar!")
            .setPositiveButton("Ok") { dialog, which ->
                mBinding.linearTitleText.text = "Selecione um dispositivo"
                mBluetoothAdapter.startDiscovery()
                dialog.dismiss()
            }
        mAlertDialog.create().show()

    }


    /** CLIQUE NO ITEM DA LISTA --> */
    private fun clickItemBluetooh() {
        if (ActivityCompat.checkSelfPermission(
                this@BluetoohPrinterActivity,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            listView.setOnItemClickListener { _, _, position, _ ->
                service?.connect(mListBluetoohSelect[position])
//                enableButtonCalibrate()
            }
        }
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                if (ActivityCompat.checkSelfPermission(
                        this@BluetoohPrinterActivity,
                        Manifest.permission.BLUETOOTH_SCAN
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    mDeviceList.add(device!!.name + "\n" + device.address)
                    mBinding.progress.isVisible = !mDeviceList.isNotEmpty()
                    bluetoothDeviceAddress.add(device.toString())
                    mDeviceListAdress.add(device.address)
                    mListBluetoohSelect.add(device)
                    if (device.name == mDeviceShared) {
                        service?.connect(device)
                        mBinding.linearTitleText.apply {
                            setTextColor(getColor(R.color.holo_red_dark))
                            text = "Tentando conectar com: ${mDeviceShared.toString()}"
                        }
                        Handler(Looper.myLooper()!!).postDelayed({
                            mBinding.linearTitleText.apply {
                                setTextColor(getColor(R.color.holo_green_dark))
                                text = "Conectado com: $mDeviceShared"
                            }
//                            enableButtonCalibrate()
                        }, 2000)

                    }
                    Log.i("BT", device.name + "\n" + device.address)
                    listView.adapter = ArrayAdapter(
                        this@BluetoohPrinterActivity,
                        R.layout.simple_list_item_1, mDeviceList
                    )

                }

            } else if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
                Toast.makeText(this@BluetoohPrinterActivity, "CONECTADO!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setupCalibrar() {
        try {
            val zpl =
                "! U1 SPEED 1\\n! U1 setvar \"print.tone\" \"20\"\\n ! U1 setvar \"media.type\" \"label\"\\n ! U1 setvar \"device.languages\" \"zpl\"\\n ! U1 setvar \"media.sense_mode\" \"gap\"\\n ~jc^xa^jus^xz\\n"
            writer.write(zpl)

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
        unregisterReceiver(receiver)
    }
}

