package com.documentos.wms_beirario.ui.rfid_recebimento.bluetoohRfid

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import co.kr.bluebird.sled.BTReader
import co.kr.bluebird.sled.SDConsts
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityBluetoohRfidBinding
import com.documentos.wms_beirario.model.recebimentoRfid.bluetooh.BluetoohRfid
import com.documentos.wms_beirario.ui.rfid_recebimento.RFIDReaderManager
import com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.RfidLeituraEpcActivity
import com.documentos.wms_beirario.utils.extensions.alertConfirmation
import com.documentos.wms_beirario.utils.extensions.alertDefaulSimplesErrorAction
import com.documentos.wms_beirario.utils.extensions.alertInfoTimeDefaultAndroid
import com.documentos.wms_beirario.utils.extensions.alertMessageSucessAction
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.toastDefault
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService.OnBluetoothEventCallback
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus
import java.util.UUID


class BluetoohRfidActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBluetoohRfidBinding
    private var readerRfidBtn: BTReader? = null
    private lateinit var adapterBluetoohSearch: BluetoohRfiBlueBirdSearchdAdapter
    private lateinit var adapterBluetoohPaired: BluetoohRfidPairedAdapter
    private lateinit var rfidReaderManager: RFIDReaderManager
    private var service: BluetoothService? = null
    private lateinit var deviceConnected: String
    private lateinit var deviceBluetoothAdapter: BluetoothDevice

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceAddress = device?.address
                    adapterBluetoohSearch.updateList(
                        BluetoohRfid(
                            name = deviceName ?: "-",
                            address = deviceAddress ?: "-",
                            device = device
                        )
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBluetoohRfidBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initServiceBluetooth()
        callBackServiceBluetooth()
        initBtReader()
        setupAdapters()
        initBluetooth()
        clickSearchBluetooh()
        clickButtonclearBluetoohPaired()


    }

    private fun callBackServiceBluetooth() {
        service?.setOnEventCallback(object : OnBluetoothEventCallback {
            override fun onDataRead(p0: ByteArray?, p1: Int) {}

            override fun onStatusChange(status: BluetoothStatus?) {
                when {
                    status.toString() == "NONE" -> {

                    }

                    status.toString() == "CONNECTED" -> {
                        connectedBluetoothZebra(deviceBluetoothAdapter)
                    }

                    status.toString() == "CONNECTING" -> {
                        toastDefault(message = "Tentando se conectar com dispositivo...")
                    }

                    else -> {

                    }
                }
            }

            override fun onDeviceName(p0: String?) {}

            override fun onToast(p0: String?) {}

            override fun onDataWrite(p0: ByteArray?) {}
        })
    }

    private fun initServiceBluetooth() {
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
    }

    private fun initBluetooth() {
        if (bluetoothAdapter == null) {
            alertDefaulSimplesErrorAction(
                message = "Bluetooth não disponível no dispositivo",
                action = {
                    finish()
                    extensionBackActivityanimation()
                }
            )
        } else {
            // Registrar receptor para descobrir novos dispositivos
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(bluetoothReceiver, filter)
            bluetoothAdapter.startDiscovery()
            initDevicesBluetoothPaired()
        }
    }

    private fun initDevicesBluetoothPaired() {
        // Listar dispositivos emparelhados
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceAddress = device.address
            println("Dispositivo emparelhado: $deviceName ($deviceAddress)")
            adapterBluetoohPaired.updateList(
                BluetoohRfid(
                    name = deviceName,
                    address = deviceAddress,
                    device = device
                )
            )
        }
    }

    private fun clickButtonclearBluetoohPaired() {
        binding.buttonClearPaired.setOnClickListener {
            if (readerRfidBtn != null) {
                val listPairedDevices = readerRfidBtn!!.BT_GetPairedDevices()
                if (!listPairedDevices.isNullOrEmpty()) {
                    listPairedDevices.forEach { devicePaired ->
                        readerRfidBtn!!.BT_UnpairDevice(devicePaired.address)
                    }
                    adapterBluetoohPaired.clear()
                    toastDefault(message = "Todos dispositivos desemparelhados")
                    binding.buttonClearPaired.isEnabled = false
                    binding.linearBluetoohPaired.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun initBtReader() {
        readerRfidBtn = BTReader.getReader(this, Handler())
        rfidReaderManager = RFIDReaderManager.getInstance()
        readerRfidBtn?.SD_Open()
        if (readerRfidBtn != null) {
            if (readerRfidBtn?.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED) {
                alertConfirmation(
                    title = "Conexão",
                    icon = R.drawable.icon_bluetooh_setting,
                    message = "Você esta conectado com leitor: ${readerRfidBtn?.BT_GetConnectedDeviceName()} - ${readerRfidBtn?.BT_GetConnectedDeviceAddr()}\nDeseja alterar o leitor?",
                    actionNo = {
                        startActivity(Intent(this, RfidLeituraEpcActivity::class.java))
                        extensionBackActivityanimation()
                    },
                    actionYes = {
                        startBluetoohScan()
                    })
            } else {
                startBluetoohScan()
            }
        }

    }

    private fun startBluetoohScan() {
        adapterBluetoohSearch.clear()
        readerRfidBtn?.BT_StartScan()
    }

    private fun clickSearchBluetooh() {
        binding.buttonSearchBluetooh.setOnClickListener {
            adapterBluetoohSearch.clear()
            readerRfidBtn?.BT_StartScan()
        }
    }

    private fun setupAdapters() {
        adapterBluetoohSearch = BluetoohRfiBlueBirdSearchdAdapter { bluetooth ->
            deviceBluetoothAdapter = bluetooth
            typeConecttedBluetooh(bluetooth)
        }

        adapterBluetoohPaired = BluetoohRfidPairedAdapter { bluetooth ->
            deviceBluetoothAdapter = bluetooth
            typeConecttedBluetooh(bluetooth)
        }

        binding.rvBluetoohPaired.apply {
            adapter = adapterBluetoohPaired
            layoutManager = LinearLayoutManager(this@BluetoohRfidActivity)
        }
        binding.rvBluetoohSerch.apply {
            adapter = adapterBluetoohSearch
            layoutManager = LinearLayoutManager(this@BluetoohRfidActivity)
        }
    }

    private fun typeConecttedBluetooh(bluetooth: BluetoothDevice) {
        when {
            bluetooth.name.contains("RFR", ignoreCase = true) -> {
                readerRfidBtn?.BT_Connect(bluetooth.address)
            }

            bluetooth.name.contains("RFD", ignoreCase = true) -> {
                service?.connect(bluetooth)
            }

            else -> {
                alertInfoTimeDefaultAndroid(
                    message = "Dispositivo não suporta este tipo de conexão!",
                    time = 5000
                )
            }
        }

    }

    private fun connectedBluetoothZebra(bluetooth: BluetoothDevice) {
        try {
            rfidReaderManager.connectBluetooh(
                context = this@BluetoohRfidActivity,
                address = bluetooth.address,
                onResult = {
                    updateConnectedInfo(bluetooth.name)
                },
                onError = { error ->
                    toastDefault(message = error)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Conexão inválida: $e")
            toastDefault(message = "Erro de uso: ${e.localizedMessage}")

        }
    }

    private fun updateConnectedInfo(bluetooth: String) {
        alertMessageSucessAction(
            message = "Conectado com:\n${bluetooth}",
            action = {
                finish()
                extensionBackActivityanimation()
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothReceiver)
        bluetoothAdapter?.cancelDiscovery()
    }
}
