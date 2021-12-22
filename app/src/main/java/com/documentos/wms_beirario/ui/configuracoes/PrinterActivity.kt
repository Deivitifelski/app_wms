package com.documentos.wms_beirario.ui.configuracoes

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.BOND_BONDING
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityPrinterBinding
import com.documentos.wms_beirario.databinding.LayoutCustomImpressoraBinding
import com.documentos.wms_beirario.ui.configuracoes.adapters.ImpressorasListAdapter
import com.documentos.wms_beirario.utils.extensions.onBackTransition
import com.example.br_coletores.models.services.PrinterConnection
import com.example.coletorwms.constants.CustomMediaSonsMp3
import com.example.coletorwms.constants.CustomSnackBarCustom


class PrinterActivity : AppCompatActivity() {

    private val mListBluetooh: ArrayList<BluetoothDevice> = ArrayList()
    private val mListBluetoohPaired: ArrayList<BluetoothDevice> = ArrayList()
    private lateinit var mBinding: ActivityPrinterBinding
    private var mAdapter: ImpressorasListAdapter? = null
    private lateinit var mSharedPreference: CustomSharedPreferences
    var TAG = "BLUETOOH IMPRESSORA ------>"
    private var mBluetoohAdapter: BluetoothAdapter? = null

    companion object {
        const val REQUEST_ENABLE_BT = 42
        val EXTRA_ADDRESS: String = "Device_address"
//        val REQUEST_QUERY_DEVICES = 142
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPrinterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.pbLoadingDiscoverDevices.visibility = View.INVISIBLE
        //Adapter impressora -->
        mSharedPreference = CustomSharedPreferences(this)
        mBluetoohAdapter = BluetoothAdapter.getDefaultAdapter()
        val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        registerReceiver(mBroadcastReceiver4, filter)
        printerValidad()
        initAdapter()
        initTolbar()
        searchDevices()
        if (mBluetoohAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(
                applicationContext,
                "Device doesn't support Bluetooth",
                Toast.LENGTH_SHORT
            ).show()
        } else if (mBluetoohAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        listPaired()
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
            Log.d("DISCOVERING-PERMISSIONS", "Permissions Granted")
        }

        val filter2 = IntentFilter(BluetoothDevice.ACTION_FOUND)
        this.registerReceiver(receiver, filter2)
        mBinding.btConcluido.setOnClickListener { finish() }
        mBinding.btCalibrar.setOnClickListener { calibrarImpressora() }
        mBinding.btAtualizar.setOnClickListener {
            searchDevices()
        }
        //------------------------Final on Create**********
    }

    private fun initTolbar() {
        mBinding.toolbarImpressora.setNavigationOnClickListener {
            onBackTransition()
        }
    }

    private fun listPaired() {
        val paired: Set<BluetoothDevice>? = mBluetoohAdapter!!.bondedDevices
        paired!!.forEach { device ->
            mListBluetoohPaired.add(device)
        }
    }

    private fun searchDevices() {
        mAdapter?.clear()
        mListBluetooh.clear()
        mBinding.pbLoadingDiscoverDevices.visibility = View.VISIBLE
        if (mBluetoohAdapter?.isDiscovering == true) {
            mBluetoohAdapter?.cancelDiscovery()
            mBluetoohAdapter?.startDiscovery()
        }
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        this.registerReceiver(receiver, filter)
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        this.registerReceiver(receiver, filter)
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        this.registerReceiver(receiver, filter)
        mBluetoohAdapter?.startDiscovery()
    }


    private fun initAdapter() {
        /**PAREADOS -->*/
        mAdapter = ImpressorasListAdapter { device ->
            mBluetoohAdapter!!.cancelDiscovery()
            CustomMediaSonsMp3().somClick(this)
            alertDialogBluetoohSelecionado(
                this,
                device.name ?: "",
                device.address ?: ""
            )
        }
        mBinding.rvListDevicesBluetooh.apply {
            layoutManager = LinearLayoutManager(this@PrinterActivity)
            adapter = mAdapter
        }
        mAdapter?.update(mListBluetooh)

    }


    private fun calibrarImpressora() {
        try {
            val zpl =
                "! U1 SPEED 1\\n! U1 setvar \"print.tone\" \"20\"\\n ! U1 setvar \"media.type\" \"label\"\\n ! U1 setvar \"device.languages\" \"zpl\"\\n ! U1 setvar \"media.sense_mode\" \"gap\"\\n ~jc^xa^jus^xz\\n"

            val printerConnection = PrinterConnection()
            printerConnection.printZebra(zpl, MenuActivity.applicationPrinterAddress)

        } catch (e: Throwable) {
            CustomSnackBarCustom().snackBarErrorSimples(
                mBinding.layoutImpressora,
                "Não foi possível calibrar a impressora."
            )
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    if (mListBluetoohPaired.containsAll(listOf(device))) {
                        alertDialogBluetoohSelecionado(
                            this@PrinterActivity,
                            device.name,
                            device.address,
                            text = "Impressora conectada anteriormente disponivel,deseja conectar com:"
                        )

                    }
                    mListBluetooh.add(device)
                    if (mListBluetooh.containsAll(listOf(device))) {
                        mListBluetooh.remove(device)
                        mListBluetooh.add(device)
                    }
                    Log.e("ENTROU AQUI ------------->", "ACTION FOUND")
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.e("ENTROU AQUI ------------->", "DISCOVERY")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    mAdapter!!.update(mListBluetooh)
                    mBluetoohAdapter?.cancelDiscovery()
                    mBinding.pbLoadingDiscoverDevices.visibility = View.INVISIBLE
                    Log.e("ENTROU AQUI ------------->", "FINISH")
                }
            }
        }
    }

    private val mBroadcastReceiver4: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                val mDevice =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                //3 cases:
                //case1: bonded already
                if (mDevice!!.bondState == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.")
                    CustomSnackBarCustom().snackBarErrorSimples(
                        mBinding.layoutImpressora,
                        "Impressora conectada com sucesso!"
                    )
                }
                //case2: creating a bone
                if (mDevice.bondState == BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.")
                }
                //case3: breaking a bond
                if (mDevice.bondState == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.")
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        return true
    }

    fun alertDialogBluetoohSelecionado(
        context: Context,
        devicename: String = "",
        deviceandress: String = "",
        text: String = "Deseja selecionar essa impressora"
    ) {
        val mAlert = AlertDialog.Builder(context)
        CustomMediaSonsMp3().somAtencao(context)
        val mBindingAlert = LayoutCustomImpressoraBinding.inflate(LayoutInflater.from(context))
        mAlert.setView(mBindingAlert.root)
        mBindingAlert.textImpressoar1.textSize = 16F
        mAlert.setCancelable(false)
        val mShow = mAlert.show()
        try {
            if (devicename!!.isNullOrEmpty()) {
                mBindingAlert.textImpressoar1.text = text  +
                        " $deviceandress?"

            } else {
                mBindingAlert.textImpressoar1.text =
                    text + "\n $devicename ?"
            }

        } catch (e: Exception) {
            mBindingAlert.textImpressoar1.text = "Deseja selecionar essa impressora?"
            mAlert.setCancelable(false)
            val mShow = mAlert.show()
        }

        mBindingAlert.buttonSimImpressora1.setOnClickListener {
            MenuActivity.applicationPrinterAddress = deviceandress
            mBluetoohAdapter!!.cancelDiscovery()
            mAdapter!!.clear()
            printerValidad()
            CustomMediaSonsMp3().somClick(context)
            CustomSnackBarCustom().toastCustomSucess(
                context, "Impressora Selecionada!"
            )
            mShow.dismiss()

        }
        mBindingAlert.buttonNaoImpressora1.setOnClickListener {
            CustomMediaSonsMp3().somClick(context)
            mShow.dismiss()

        }
    }


    private fun printerValidad() {
        if (MenuActivity.applicationPrinterAddress.isNotEmpty()) {
            mBinding.btCalibrar.isEnabled = true
            mBinding.txtInfPrinter.text = "Conectado com : ${MenuActivity.applicationPrinterAddress}"
        } else {
            mBinding.btCalibrar.isEnabled = false
            mBinding.txtInfPrinter.text = getString(R.string.get_dispositivo)
        }
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver4)
    }

}




