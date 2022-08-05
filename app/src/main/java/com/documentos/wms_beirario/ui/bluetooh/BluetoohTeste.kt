package com.documentos.wms_beirario.ui.bluetooh

import android.Manifest
import android.R
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import app.akexorcist.bluetotohspp.library.BluetoothState.REQUEST_ENABLE_BT
import com.documentos.wms_beirario.databinding.ActivityBluetoohPrinterBinding
import com.documentos.wms_beirario.databinding.LayoutCustomImpressoraBinding
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.ui.configuracoes.temperature.ControlActivity
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BluetoohTeste : AppCompatActivity() {

    private lateinit var mBinding: ActivityBluetoohPrinterBinding
    private lateinit var listView: ListView
    private val mDeviceList = ArrayList<String>()
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothDeviceAddress = mutableListOf<String>()
    private lateinit var mPrinter: PrinterConnection
    private val mListBluetoohPaired: ArrayList<BluetoothDevice> = ArrayList()
    private val mListBluetoohSelect: ArrayList<BluetoothDevice> = ArrayList()
    private lateinit var mALert: CustomAlertDialogCustom

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityBluetoohPrinterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        setToolbar()
        mALert = CustomAlertDialogCustom()
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBluetoothAdapter?.startDiscovery()
        checkBluetooh()
        mPrinter = PrinterConnection(SetupNamePrinter.mNamePrinterString)
        clickButtons()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)
        listView = mBinding.listView
        listPaired()
        listView.setOnItemClickListener { _, _, position, _ ->
            SetupNamePrinter.mNamePrinterString = bluetoothDeviceAddress[position]
            mListBluetoohSelect[position].createBond()
            mSucessToastExtension(
                this,
                "Impressora conectada: ${SetupNamePrinter.mNamePrinterString}"
            )
            onBackPressed()
        }
        reflesh()
    }

    private fun setToolbar() {
        mBinding.toolbar6.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    /**VERIFICANDO SE BLUETOOH DISPONIVEL -->*/
    private fun checkBluetooh() {
        if (mBluetoothAdapter == null) {
            mALert.alertMessageAtencao(
                this,
                getString(com.documentos.wms_beirario.R.string.support_bluetooth)
            )
        } else if (!mBluetoothAdapter!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    /**RESPONSE SISTEMA --> */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                if (!mBluetoothAdapter!!.isEnabled) {
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
                mBinding.linearTitleText.text = "Bluetooh não ativado!\nAtive e clique em atualizar"
                mBinding.progress.isVisible = false
            }
        }
    }

    /**REFLESH LIST -->*/
    private fun reflesh() {
        mBinding.reflesh.apply {
            setColorSchemeColors(getColor(com.documentos.wms_beirario.R.color.color_default))
            setOnRefreshListener {
                mDeviceList.clear()
                mBluetoothAdapter?.cancelDiscovery()
                listView.adapter = ArrayAdapter(
                    this@BluetoohTeste,
                    R.layout.simple_list_item_1, mDeviceList
                )
                mBluetoothAdapter?.startDiscovery()
                isRefreshing = false
            }
        }
    }

    private fun listPaired() {
        val paired: Set<BluetoothDevice>? = mBluetoothAdapter?.bondedDevices
        paired!!.forEach { device ->
            mListBluetoohPaired.add(device)
        }
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                mDeviceList.add(device?.name + "\n" + device?.address)
                if (device != null) {
                    mListBluetoohSelect.add(device)
                }
                device?.let { bluetoothDeviceAddress.add(it.address) }
                Log.i("BT", device?.name + "\n" + device?.address)
                if (mListBluetoohPaired.containsAll(listOf(device))) {
                    alertDialogBluetoohSelecionado(
                        this@BluetoohTeste,
                        text = "Impressora conectada anteriormente disponivel,deseja conectar com:",
                        device
                    )
                }
                listView.adapter = ArrayAdapter(
                    this@BluetoohTeste,
                    android.R.layout.simple_list_item_1, mDeviceList
                )
                mBinding.progress.isVisible = mDeviceList.isEmpty()

            }
        }
    }

    /**ALERTA PARA APARELHOS ANTERIORMENTE CONECTADOS --> **/
    fun alertDialogBluetoohSelecionado(
        context: Context,
        text: String = "Deseja selecionar essa impressora",
        device: BluetoothDevice?
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
            if (device?.name!!.isEmpty() || device.name == "" || device.name.isNullOrEmpty()) {
                mBindingAlert.textImpressoar1.text = text +
                        "${device.address}?"

            } else {
                mBindingAlert.textImpressoar1.text =
                    "$text\n ${device.name} ?"
            }

        } catch (e: Exception) {
            mBindingAlert.textImpressoar1.text = "Deseja selecionar essa impressora?"
            mAlert.setCancelable(false)
            val mShow = mAlert.show()
        }
        /**BUTTON SIM ->*/
        mBindingAlert.buttonSimImpressora1.setOnClickListener {
            SetupNamePrinter.mNamePrinterString = device.toString()
            device?.createBond()
            mShow.dismiss()
            mBinding.linearTitleText.text = "Conectado com: ${device?.address}"
        }
        /**BUTTON NAO ->*/
        mBindingAlert.buttonNaoImpressora1.setOnClickListener {
            CustomMediaSonsMp3().somClick(context)
            mShow.dismiss()
        }
    }


    private fun clickButtons() {
        /**BUTTON ATUALIZA -->**/
        mBinding.btAtualizar.setOnClickListener {
            mBinding.progress.isVisible = true
            mDeviceList.clear()
            mBluetoothAdapter?.cancelDiscovery()
            listView.adapter = ArrayAdapter(
                this,
                R.layout.simple_list_item_1, mDeviceList
            )
            mBluetoothAdapter?.startDiscovery()
        }
        /** BUTTON CONCLUIR -->*/
        mBinding.btConcluido.setOnClickListener {
            onBackTransitionExtension()
        }
        /** CALIBRAR -->*/
        mBinding.btCalibrar.setOnClickListener {
            if (SetupNamePrinter.mNamePrinterString.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.Default) {
                    mPrinter.sendZplOverBluetoothNet(
                        "! U1 SPEED 1\\n! U1 setvar \"print.tone\" \"20\"\\n ! U1 setvar \"media.type\" " +
                                "\"label\"\\n ! " + "U1 setvar \"device.languages\" \"zpl\"\\n ! U1 setvar " +
                                "\"media.sense_mode\" \"gap\"\\n ~jc^xa^jus^xz\\n"
                    )
                }
            } else {
                mErroToastExtension(this, "Nenhuma impressora conectada!")
            }
        }
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


//Intent intent = new Intent();
// intent.setComponent(new ComponentName("com.zebra.printconnect",
//"com.zebra.printconnect.print.TemplatePrintService"));
// intent.putExtra("com.zebra.printconnect.PrintService.TEMPLATE_FILE_NAME", "PriceTagTemplate.zpl");
// intent.putExtra("com.zebra.printconnect.PrintService.VARIABLE_DATA", variableData);
// intent.putExtra("com.zebra.printconnect.PrintService.RESULT_RECEIVER", buildIPCSafeReceiver(new
//ResultReceiver(null) {
// @Override
// protected void onReceiveResult(int resultCode, Bundle resultData) {
// if (resultCode == 0) { // Result code 0 indicates success
// // Handle successful print
// } else {
// // Handle unsuccessful print
//// Error message (null on successful print)
//String errorMessage = resultData.getString("com.zebra.printconnect.PrintService.ERROR_MESSAGE");
//}
// }
// }));
// startService(intent);