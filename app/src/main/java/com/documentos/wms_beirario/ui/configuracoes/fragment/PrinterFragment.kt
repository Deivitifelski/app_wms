package com.documentos.wms_beirario.ui.configuracoes.fragment

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.FragmentPrinterBinding
import com.documentos.wms_beirario.ui.configuracoes.MenuActivity
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.example.coletorwms.constants.CustomMediaSonsMp3
import com.example.coletorwms.constants.CustomSnackBarCustom
import com.example.coletorwms.view.impressora.ActivityImpressora
import com.example.coletorwms.view.impressora.ImpressorasListAdapter

class PrinterFragment : Fragment() {

    private val listBluetooh: ArrayList<BluetoothDevice> = ArrayList()
    private var mAdapter: ImpressorasListAdapter? = null
    var TAG = "BLUETOOH IMPRESSORA ------>"

    companion object {
        const val REQUEST_ENABLE_BT = 42
        val EXTRA_ADDRESS: String = "Device_address"
//        val REQUEST_QUERY_DEVICES = 142
    }

    private var mBinding: FragmentPrinterBinding? = null
    val binding get() = mBinding
    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Adapter impressora -->
        val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        requireActivity().registerReceiver(mBroadcastReceiver4, filter)

        initAdapter()
        initTolbar()
        atualizar()

        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(
                requireContext(),
                "Device doesn't support Bluetooth",
                Toast.LENGTH_SHORT
            ).show()
        } else if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ActivityImpressora.REQUEST_ENABLE_BT)
        }

        val permission1 =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH)
        val permission2 =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_ADMIN)
        val permission3 =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        val permission4 =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        if (permission1 != PackageManager.PERMISSION_GRANTED
            || permission2 != PackageManager.PERMISSION_GRANTED
            || permission3 != PackageManager.PERMISSION_GRANTED
            || permission4 != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
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
        requireActivity().registerReceiver(receiver, filter2)
        mBinding!!.btConcluido.setOnClickListener { requireActivity().finish() }
        mBinding!!.btCalibrar.setOnClickListener { calibrarImpressora() }
        mBinding!!.btAtualizar.setOnClickListener {
            atualizar()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPrinterBinding.inflate(layoutInflater)
        return binding!!.root
    }

    private fun atualizar() {
        listBluetooh.clear()
        mAdapter?.clear()
        mBinding!!.pbLoadingDiscoverDevices.visibility = View.VISIBLE
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter?.cancelDiscovery()
        }
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        requireActivity().registerReceiver(receiver, filter)
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        requireActivity().registerReceiver(receiver, filter)
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        requireActivity().registerReceiver(receiver, filter)
        bluetoothAdapter?.startDiscovery()
    }


    private fun initAdapter() {
        mAdapter = ImpressorasListAdapter { device ->
            Log.e("------>", "CLICK ADAPTER OK ")
            CustomMediaSonsMp3().somClick(requireContext())
            Log.d(TAG, "Trying to pair with ${device.address}")
            device.createBond()
            CustomAlertDialogCustom().alertDialogBluetoohSelecionado(
                requireContext(),
                device.name ?: "",
                device.address ?: ""
            )
            MenuActivity.applicationPrinterAddress = device.address
        }
        mBinding!!.rvListDevicesBluetooh.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
        mAdapter?.update(listBluetooh)

    }

    private fun calibrarImpressora() {
        try {
            val zpl =
                "! U1 SPEED 1\\n! U1 setvar \"print.tone\" \"20\"\\n ! U1 setvar \"media.type\" \"label\"\\n ! U1 setvar \"device.languages\" \"zpl\"\\n ! U1 setvar \"media.sense_mode\" \"gap\"\\n ~jc^xa^jus^xz\\n"

            val printerConnection = PrinterConnection()
            printerConnection.printZebra(zpl, MenuActivity.applicationPrinterAddress)

        } catch (e: Throwable) {
            CustomSnackBarCustom().snackBarErrorSimples(
                mBinding!!.layoutImpressora,
                "Não foi possível calibrar a impressora."
            )

        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    mBinding!!.pbLoadingDiscoverDevices.visibility = View.GONE
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    var msg = ""
                    msg = if (deviceName.isNullOrBlank()) {
                        deviceHardwareAddress
                    } else {
                        "$deviceName $deviceHardwareAddress"
                    }
                    listBluetooh.add(device)
                    discoverDeviceList()
                    Log.d("DISCOVERING-DEVICE", msg)
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    listBluetooh.clear()
                    mAdapter?.clear()
                    Log.d("DISCOVERING-STARTED", "isDiscovering")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    mBinding!!.pbLoadingDiscoverDevices.visibility = View.GONE
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
                    CustomSnackBarCustom().snackBarErrorSimples(
                        mBinding!!.layoutImpressora,
                        "Impressora conectada com sucesso!"
                    )
//                    showAlertDialogWithAutoDismiss("Impressora conectada com sucesso!", "noDismiss")
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

    private fun initTolbar() {
        mBinding!!.toolbarImpressora.setNavigationOnClickListener {

        }
    }


    private fun discoverDeviceList() {
        if (listBluetooh.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Nenhum dispositivo encontrado!", Toast.LENGTH_SHORT)
                .show()
        } else {
            mAdapter?.update(listBluetooh)
        }
    }

    override fun onDestroy() {
        requireActivity().unregisterReceiver(receiver)
        super.onDestroy()
        requireActivity().unregisterReceiver(mBroadcastReceiver4)
    }
}