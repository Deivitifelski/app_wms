package com.documentos.wms_beirario.ui.configuracoes

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.ImpressorasBinding
import com.documentos.wms_beirario.ui.configuracoes.adapters.ImpressorasListAdapter
import com.documentos.wms_beirario.utils.extensions.onBackTransition
import com.example.br_coletores.models.services.PrinterConnection


class ImpressorasActivity : BaseActivity(), ImpressorasListAdapter.AdapterListener {
    var TAG = "bluetooth"
    private val list: ArrayList<BluetoothDevice> = ArrayList()

    companion object {
        const val REQUEST_ENABLE_BT = 42
        val EXTRA_ADDRESS: String = "Device_address"
//        val REQUEST_QUERY_DEVICES = 142
    }

    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private lateinit var mBinding: ImpressorasBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ImpressorasBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.rvListBluetooh.layoutManager = LinearLayoutManager(this)
        val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        registerReceiver(mBroadcastReceiver4, filter)

        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(
                applicationContext,
                "Device doesn't support Bluetooth",
                Toast.LENGTH_SHORT
            ).show()
        } else if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

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
        var filter2 = IntentFilter(BluetoothDevice.ACTION_FOUND)
        this.registerReceiver(receiver, filter2)
        mBinding.btConcluido.setOnClickListener { finish() }
        mBinding.btCalibrar.setOnClickListener { calibrarImpressora() }
        mBinding.btAtualizar.setOnClickListener {
            mBinding.pbLoadingDiscoverDevices.visibility = View.VISIBLE
            if (bluetoothAdapter?.isDiscovering == true) {
                bluetoothAdapter?.cancelDiscovery()
            }
            var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            this.registerReceiver(receiver, filter)
            filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            this.registerReceiver(receiver, filter)
            filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            this.registerReceiver(receiver, filter)
            bluetoothAdapter?.startDiscovery()
        }
    }

    override fun onResume() {
        super.onResume()
        setupToolbar()
    }

    private fun setupToolbar() {
        mBinding.toolbarPrinters.setOnClickListener {
            onBackTransition()
        }
    }

    private fun calibrarImpressora() {
        try {
            val zpl =
                "! U1 SPEED 1\\n! U1 setvar \"print.tone\" \"20\"\\n ! U1 setvar \"media.type\" \"label\"\\n ! U1 setvar \"device.languages\" \"zpl\"\\n ! U1 setvar \"media.sense_mode\" \"gap\"\\n ~jc^xa^jus^xz\\n"

            val printerConnection = PrinterConnection()
            printerConnection.printZebra(zpl, MenuActivity.applicationPrinterAddress)

        } catch (e: Throwable) {
            showAlertDialogWithAutoDismiss("Não foi possível calibrar a impressora.")
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    mBinding.pbLoadingDiscoverDevices.visibility = View.GONE
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    var msg = ""
                    if (deviceName.isNullOrBlank()) {
                        msg = deviceHardwareAddress
                    } else {
                        msg = "$deviceName $deviceHardwareAddress"
                    }
                    list.add(device)
                    Log.d("DISCOVERING-DEVICE", msg)
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    list.clear()
                    Log.d("DISCOVERING-STARTED", "isDiscovering")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    mBinding.pbLoadingDiscoverDevices.visibility = View.GONE
                    discoverDeviceList()
                    Log.d("DISCOVERING-FINISHED", "FinishedDiscovering")
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
                    showAlertDialogWithAutoDismiss("Impressora conectada com sucesso!", "noDismiss")
                }
                //case2: creating a bone
                if (mDevice.bondState == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.")
                }
                //case3: breaking a bond
                if (mDevice.bondState == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.")
                }
            }
        }
    }

    private fun discoverDeviceList() {
        mBinding.rvListBluetooh.adapter = ImpressorasListAdapter(list, this)
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver4);
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onClickDevice(device: BluetoothDevice) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "Trying to pair with ${device.address}")
            device.createBond()
            showAlertDialogWithAutoDismiss("Impressora selecionada.")
            MenuActivity.applicationPrinterAddress = device.address
        }
    }

}