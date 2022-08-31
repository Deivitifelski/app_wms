package com.documentos.wms_beirario.ui.bluetooh

import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.Color
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
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.mErroToastExtension
import com.documentos.wms_beirario.utils.extensions.mSucessToastExtension
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import com.github.douglasjunior.bluetoothclassiclibrary.*
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService.OnBluetoothEventCallback
import java.util.*


class BluettohLIbrary : AppCompatActivity() {

    private lateinit var mBinding: ActivityBluetoohPrinterBinding
    private val TAG = "BLUETOOH"
    private lateinit var listView: ListView
    private val mDeviceList = ArrayList<String>()
    private val mListBluetoohSelect: ArrayList<BluetoothDevice> = ArrayList()
    private var service: BluetoothService? = null
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mDeviceShared: String
    lateinit var mDeviceApp: BluetoothDevice


    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityBluetoohPrinterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mSharedPreferences = CustomSharedPreferences(this)
        listView = mBinding.listView
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
        service?.startScan()
        val writer = BluetoothWriter(service)
        clikBUtton(writer)
        mDeviceShared =
            mSharedPreferences.getString(CustomSharedPreferences.DEVICE_PRINTER).toString() ?: ""
        verificaConnect()
        //CLICK NA LISTA -->
        listView.setOnItemClickListener { _, _, position, _ ->
            mDeviceApp = mListBluetoohSelect[position]
            service?.connect(mListBluetoohSelect[position])
        }




        service?.setOnScanCallback(object : BluetoothService.OnBluetoothScanCallback {
            override fun onDeviceDiscovered(device: BluetoothDevice?, rssi: Int) {
                if (device?.name.toString() == mDeviceShared) {
                    mDeviceApp = device!!
                    service?.connect(device)
                    mBinding.linearTitleText.apply {
                        setTextColor(Color.RED)
                        text = "Se conectando com $mDeviceShared"
                        Handler(Looper.myLooper()!!).postDelayed({
                            service?.stopScan()
                        }, 2000)
                    }
                    mBinding.progress.isVisible = true
                    Log.e("TAG", "${device?.address}")
                    if (device != null) {
                        mListBluetoohSelect.add(device)
                    }
                    mDeviceList.add(device!!.name + "\n" + device.address)
                    listView.adapter = ArrayAdapter(
                        this@BluettohLIbrary,
                        R.layout.simple_list_item_1, mDeviceList.distinct()
                    )
                }
            }

            override fun onStartScan() {
                mBinding.progress.isVisible = true
                Toast.makeText(this@BluettohLIbrary, "Buscando dispositivos", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onStopScan() {
                mBinding.progress.isVisible = false
            }
        })


        // -->
        service!!.setOnEventCallback(object : OnBluetoothEventCallback {
            override fun onDataRead(buffer: ByteArray, length: Int) {

            }

            override fun onStatusChange(status: BluetoothStatus) {
                when {
                    status.toString() == "NONE" -> {
                        mBinding.btCalibrar.isEnabled = false
                        mBinding.linearTitleText.text = "Selecione um Dispositivo:"
                    }
                    status.toString() == "CONNECTED" -> {

                        mBinding.btCalibrar.isEnabled = true
                        mBinding.linearTitleText.apply {
                            setTextColor(getColor(R.color.holo_green_dark))
                            text = "Conectado com: $mDeviceShared"
                        }
                    }
                    status.toString() == "CONNECTING" -> {
                        mBinding.btCalibrar.isEnabled = false
                        mBinding.linearTitleText.apply {
                            setTextColor(Color.RED)
                            text =
                                "Tentando se conectar"
                        }
                    }
                    else -> {
                        mBinding.btCalibrar.isEnabled = false
                        Toast.makeText(this@BluettohLIbrary, "Desconectado", Toast.LENGTH_SHORT)
                            .show()
                        mBinding.linearTitleText.text = "Selecione um Dispositivo:"
                    }
                }
            }

            override fun onDeviceName(deviceName: String) {

                SetupNamePrinter.mNamePrinterString = deviceName
                mSharedPreferences.saveString(CustomSharedPreferences.DEVICE_PRINTER, deviceName)
                Toast.makeText(
                    this@BluettohLIbrary,
                    "Conectado com: $deviceName",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onToast(message: String) {
                Log.e(TAG, "onToast -> $message ")
            }

            override fun onDataWrite(buffer: ByteArray) {

            }
        })
    }

    private fun verificaConnect() {
        if (service?.status == BluetoothStatus.CONNECTED) {
            Log.e(TAG, "verificaConnect: CONECTADO")
        } else {
            Log.e(TAG, "verificaConnect:NAO CONECTADO")
        }
    }


    private fun clikBUtton(writer: BluetoothWriter) {
        mBinding.btCalibrar.setOnClickListener {
            val zpl =
                "! U1 SPEED 1\\n! U1 setvar \"print.tone\" \"20\"\\n ! U1 setvar \"media.type\" \"label\"\\n ! U1 setvar \"device.languages\" \"zpl\"\\n ! U1 setvar \"media.sense_mode\" \"gap\"\\n ~jc^xa^jus^xz\\n"
            writer.write(zpl)
        }

        /**ATUALIZAR LISTA -->*/
        mBinding.btAtualizar.setOnClickListener {
            mDeviceList.clear()
            mListBluetoohSelect.clear()
            listView.adapter = ArrayAdapter(
                this,
                R.layout.simple_list_item_1, mDeviceList
            )
            service?.startScan()
        }
        /** BUTTON CONCLUIR -->*/
        mBinding.btConcluido.setOnClickListener {
            onBackTransitionExtension()
        }
    }


    /**ALERTA PARA APARELHOS ANTERIORMENTE CONECTADOS --> **/
    fun alertDialogBluetoohSelecionado(
        device: BluetoothDevice
    ) {
        vibrateExtension(500)
        val mAlert = AlertDialog.Builder(this)
        CustomMediaSonsMp3().somAtencao(this)
        val mBindingAlert = LayoutCustomImpressoraBinding.inflate(LayoutInflater.from(this))
        mAlert.setView(mBindingAlert.root)
        mBindingAlert.textImpressoar1.textSize = 16F
        mAlert.setCancelable(false)
        val mShow = mAlert.show()
        try {
            if (device.name!!.isEmpty() || device.name == "" || device.name.isNullOrEmpty()) {
                mBindingAlert.textImpressoar1.text =
                    "Deseja se conectar com:" + "${device.address}?"
            } else {
                mBindingAlert.textImpressoar1.text = "Deseja se conectar com:\n ${device.name} ?"
            }

        } catch (e: Exception) {
            mBindingAlert.textImpressoar1.text = "Deseja selecionar essa impressora?"
            mAlert.setCancelable(false)
            val mShow = mAlert.show()
        }
        /**BUTTON SIM ->*/
        mBindingAlert.buttonSimImpressora1.setOnClickListener {
            service?.connect(device)
            mShow.dismiss()
        }
        /**BUTTON NAO ->*/
        mBindingAlert.buttonNaoImpressora1.setOnClickListener {
            mShow.dismiss()
        }
    }
}