package layout.printer

import ImpressorasListAdapter
import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.*
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
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import com.documentos.wms_beirario.utils.extensions.vibrateExtension


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
        mBinding.pbBluetoohDiscoverDevices.visibility = View.INVISIBLE
        //Adapter impressora -->
        mSharedPreference = CustomSharedPreferences(this)
        mBluetoohAdapter = BluetoothAdapter.getDefaultAdapter()
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

        mBinding.btConcluido.setOnClickListener { finish() }
        mBinding.btCalibrar.setOnClickListener { calibrarImpressora() }
        mBinding.btAtualizar.setOnClickListener {
            searchDevices()
        }
        //------------------------Final on Create**********
    }

    private fun initTolbar() {
        mBinding.toolbarImpressora.setNavigationOnClickListener {
            onBackTransitionExtension()
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
        mBinding.pbBluetoohDiscoverDevices.visibility = View.VISIBLE
        if (mBluetoohAdapter?.isDiscovering == true) {
            mBluetoohAdapter?.cancelDiscovery()
            mBluetoohAdapter?.startDiscovery()
        }
        val filter = IntentFilter(ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        filter.addAction(ACTION_CONNECTION_STATE_CHANGED)
        filter.addAction(ACTION_ACL_CONNECTED)
        filter.addAction(ACTION_ACL_DISCONNECT_REQUESTED)
        filter.addAction(ACTION_ACL_DISCONNECTED)
        this.registerReceiver(receiver, filter)
        mBluetoohAdapter?.startDiscovery()
    }


    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                //START -->
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.e("ENTROU AQUI ------------->", "DISCOVERY")
                }
                //ACHOU DISPOSITIVO -->
                ACTION_FOUND -> {
                    val device: BluetoothDevice = intent.getParcelableExtra(EXTRA_DEVICE)!!
                    if (mListBluetoohPaired.containsAll(listOf(device))) {
                        alertDialogBluetoohSelecionado(
                            this@PrinterActivity,
                            device.name,
                            device.address,
                            text = "Impressora conectada anteriormente disponivel,deseja conectar com:",
                            device
                        )

                    }
                    mListBluetooh.add(device)
                    if (mListBluetooh.containsAll(listOf(device))) {
                        mListBluetooh.remove(device)
                        mListBluetooh.add(device)
                    }
                    Log.e("ENTROU AQUI ------------->", "ACTION FOUND")
                }
                //FIM DA PESQUISA -->
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    mAdapter!!.update(mListBluetooh)
                    mBluetoohAdapter?.cancelDiscovery()
                    mBinding.pbBluetoohDiscoverDevices.visibility = View.INVISIBLE
                    Log.e("ENTROU AQUI ------------->", "FINISH")
                }
                BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED -> {
                    vibrateExtension(500)
                    CustomAlertDialogCustom().alertMessageAtencao(this@PrinterActivity, "DESCONECT")
                }
                ACTION_ACL_CONNECTED -> {
                    Log.e("ENTROU AQUI ------------->", "ACTION_ACL_CONNECTED")
                }
                ACTION_ACL_DISCONNECT_REQUESTED -> {
                    Log.e("ENTROU AQUI ------------->", "ACTION_ACL_DISCONNECT_REQUESTED")
                }
                ACTION_ACL_DISCONNECTED -> {
                    Log.e("ENTROU AQUI ------------->", "ACTION_ACL_DISCONNECTED")
                }
            }
        }
    }

    private fun initAdapter() {
        /**PAREADOS -->*/
        mAdapter = ImpressorasListAdapter { device ->
            mBluetoohAdapter!!.cancelDiscovery()
            CustomMediaSonsMp3().somClick(this)
            alertDialogBluetoohSelecionado(
                this,
                device.name,
                device.address,
                device = device
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

            val printerConnection = PrinterConnection(SetupNamePrinter.mNamePrinterString)
            printerConnection.sendZplBluetooth(this, zpl)

        } catch (e: Throwable) {
            CustomSnackBarCustom().snackBarErrorSimples(
                mBinding.layoutImpressora,
                "Não foi possível calibrar a impressora."
            )
        }
    }


    private val mBroadcastReceiver4: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val mDevice = intent.getParcelableExtra<BluetoothDevice>(EXTRA_DEVICE)
                if (mDevice!!.bondState == BOND_BONDED) {
                    CustomAlertDialogCustom().alertMessageAtencao(this@PrinterActivity, "CONECT")
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.")
                }
                if (mDevice.bondState == BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.")
                }
            }
        }
    }

    fun alertDialogBluetoohSelecionado(
        context: Context,
        devicename: String = "",
        deviceandress: String = "",
        text: String = "Deseja selecionar essa impressora",
        device: BluetoothDevice
    ) {
        val mAlert = AlertDialog.Builder(context)
        CustomMediaSonsMp3().somAtencao(context)
        val mBindingAlert = LayoutCustomImpressoraBinding.inflate(LayoutInflater.from(context))
        mAlert.setView(mBindingAlert.root)
        mBindingAlert.textImpressoar1.textSize = 16F
        mAlert.setCancelable(false)
        val mShow = mAlert.show()
        try {
            if (devicename.isNullOrEmpty()) {
                mBindingAlert.textImpressoar1.text = text +
                        " $deviceandress?"

            } else {
                mBindingAlert.textImpressoar1.text =
                    "$text\n $devicename ?"
            }

        } catch (e: Exception) {
            mBindingAlert.textImpressoar1.text = "Deseja selecionar essa impressora?"
            mAlert.setCancelable(false)
            val mShow = mAlert.show()
        }

        mBindingAlert.buttonSimImpressora1.setOnClickListener {
            SetupNamePrinter.mNamePrinterString = deviceandress
            mBluetoohAdapter!!.cancelDiscovery()
            mAdapter!!.clear()
            device.createBond()
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
        if (SetupNamePrinter.mNamePrinterString.isNotEmpty()) {
            mBinding.btCalibrar.isEnabled = true
            mBinding.txtInfPrinter.text =
                "Conectado com : ${SetupNamePrinter.mNamePrinterString}"
        } else {
            mBinding.btCalibrar.isEnabled = false
            mBinding.txtInfPrinter.text = getString(R.string.get_dispositivo)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        unregisterReceiver(mBroadcastReceiver4)
    }

}




