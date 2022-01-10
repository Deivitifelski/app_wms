package com.documentos.wms_beirario.ui.bluetooh

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityBluetoohTestBinding
import com.documentos.wms_beirario.databinding.LayoutCustomImpressoraBinding
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.ui.configuracoes.adapters.ImpressorasListAdapter
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import com.example.coletorwms.constants.CustomMediaSonsMp3
import com.example.coletorwms.constants.CustomSnackBarCustom

class BluetoohTestActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityBluetoohTestBinding
    private val REQUEST_ENABLE_BT = 1
    private val mBluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val mListBluetooh: ArrayList<BluetoothDevice> = ArrayList()
    private val mListBluetoohPaired: ArrayList<BluetoothDevice> = ArrayList()
    private var mAdapter: ImpressorasListAdapter? = null
    lateinit var printerConnection: PrinterConnection
    private val TAG = "ENTA AQUI --->"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBluetoohTestBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        printerConnection = PrinterConnection()
        setupToolbar()
        setupRecyclerView()
        listPaired()
        checkBluetoothDisabled()
        printerValidad()
        setupBluetoohSupport()
        sharedDevices()
        setupButtons()
    }

    private fun listPaired() {
        val paired: Set<BluetoothDevice>? = mBluetoothAdapter!!.bondedDevices
        paired!!.forEach { device ->
            mListBluetoohPaired.add(device)
        }
    }

    private fun setupButtons() {
        /**BUTTON ATUALIZAR -->*/
        mBinding.btAtualizar.setOnClickListener {
            setupUpdate()
        }
        /**BUTTON CALIBRAR -->*/
        mBinding.btCalibrar.setOnClickListener {
            setupCalibrar()
        }
        /**BUTTON CONCLUIR -->*/
        mBinding.btConcluido.setOnClickListener {
            onBackTransitionExtension()
        }
    }

    private fun setupRecyclerView() {
        mAdapter = ImpressorasListAdapter { deviceClick ->
            alertDialogBluetoohSelecionado(
                this@BluetoohTestActivity,
                deviceClick.name,
                deviceClick.address,
                text = "Impressora conectada anteriormente disponivel,deseja conectar com:",
                deviceClick
            )
        }
        mBinding.rvListDevicesBluetooh.apply {
            layoutManager = LinearLayoutManager(this@BluetoohTestActivity)
            adapter = mAdapter
        }
    }

    private fun setupToolbar() {
        mBinding.toolbarImpressora.setNavigationOnClickListener {
            onBackTransitionExtension()
        }
    }

    private fun checkBluetoothDisabled() {
        if (mBluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    private fun setupBluetoohSupport() {
        if (mBluetoothAdapter == null) {
            CustomAlertDialogCustom().alertMessageAtencao(
                this,
                getString(R.string.support_bluetooth)
            )
        }
    }

    private fun sharedDevices() {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        registerReceiver(receiver, filter)
        mBluetoothAdapter?.startDiscovery()
    }

    /**ACTION_FOUND --> */
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    printerValidad()
                    CustomSnackBarCustom().snackBarSimplesBlack(mBinding.root, "CONECT")
                }

                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    Toast.makeText(this@BluetoohTestActivity, "Desconectado!", Toast.LENGTH_SHORT)
                        .show()
                    SetupNamePrinter.applicationPrinterAddress = ""
                    printerValidad()
                }
                /** INICIANDO BUSCA POR  DEVICES BLUETOOH --> */
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    mBinding.pbBluetoohDiscoverDevices.visibility = View.VISIBLE
                }

                /** DESCOBRIU DEVICES BLUETOOH --> */
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (mListBluetoohPaired.containsAll(listOf(device))) {
                        alertDialogBluetoohSelecionado(
                            this@BluetoohTestActivity,
                            device!!.name,
                            device.address,
                            text = "Impressora conectada anteriormente disponivel,deseja conectar com:",
                            device
                        )
                    }

                    mListBluetooh.add(device!!)
                    if (mListBluetooh.containsAll(listOf(device))) {
                        mListBluetooh.remove(device)
                        mListBluetooh.add(device)
                    }
                }

                /** FIM DA BUSCA POR BLUETOOH --> */
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    mBluetoothAdapter?.cancelDiscovery()
                    mAdapter!!.update(mListBluetooh = mListBluetooh)
                    mBinding.pbBluetoohDiscoverDevices.visibility = View.INVISIBLE
                }
            }
        }
    }


    private fun setupCalibrar() {
        try {
            val zpl =
                "! U1 SPEED 1\\n! U1 setvar \"print.tone\" \"20\"\\n ! U1 setvar \"media.type\" \"label\"\\n ! U1 setvar \"device.languages\" \"zpl\"\\n ! U1 setvar \"media.sense_mode\" \"gap\"\\n ~jc^xa^jus^xz\\n"
            printerConnection.printZebra(zpl, SetupNamePrinter.applicationPrinterAddress)

        } catch (e: Throwable) {
            CustomSnackBarCustom().snackBarErrorSimples(
                mBinding.layoutImpressora,
                "Não foi possível calibrar a impressora."
            )
        }
    }

    private fun setupUpdate() {
        mAdapter!!.clear()
        mListBluetooh.clear()
        sharedDevices()
    }


    fun alertDialogBluetoohSelecionado(
        context: Context,
        devicename: String = "",
        deviceandress: String = "",
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
            if (devicename.isEmpty()) {
                mBindingAlert.textImpressoar1.text = text +
                        "$deviceandress?"

            } else {
                mBindingAlert.textImpressoar1.text =
                    text + "\n $devicename ?"
            }

        } catch (e: Exception) {
            mBindingAlert.textImpressoar1.text = "Deseja selecionar essa impressora?"
            mAlert.setCancelable(false)
            val mShow = mAlert.show()
        }
        /**BUTTON SIM ->*/
        mBindingAlert.buttonSimImpressora1.setOnClickListener {
            SetupNamePrinter.applicationPrinterAddress = deviceandress
            printerConnection.printZebra(null, SetupNamePrinter.applicationPrinterAddress)
            mBluetoothAdapter!!.cancelDiscovery()
            mAdapter!!.clear()
            device.createBond()
            printerValidad()
            CustomMediaSonsMp3().somClick(context)
            CustomSnackBarCustom().toastCustomSucess(
                context, "Impressora Selecionada!"
            )
            mShow.dismiss()

        }
        /**BUTTON NAO ->*/
        mBindingAlert.buttonNaoImpressora1.setOnClickListener {
            CustomMediaSonsMp3().somClick(context)
            mShow.dismiss()

        }
    }

    private fun printerValidad() {
        if (SetupNamePrinter.applicationPrinterAddress.isNotEmpty()) {
            mBinding.btCalibrar.isEnabled = true
            mBinding.txtInfPrinter.text =
                "Conectado com : ${SetupNamePrinter.applicationPrinterAddress}"
        } else {
            mBinding.btCalibrar.isEnabled = false
            mBinding.txtInfPrinter.text = getString(R.string.get_dispositivo)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}