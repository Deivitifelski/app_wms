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
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import co.kr.bluebird.sled.BTReader
import co.kr.bluebird.sled.SDConsts
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityBluetoohRfidBinding
import com.documentos.wms_beirario.model.recebimentoRfid.bluetooh.BluetoohRfid
import com.documentos.wms_beirario.ui.rfid_recebimento.RFIDReaderManager
import com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.RfidLeituraEpcActivity
import com.documentos.wms_beirario.utils.extensions.alertConfirmation
import com.documentos.wms_beirario.utils.extensions.alertDefaulSimplesError
import com.documentos.wms_beirario.utils.extensions.alertDefaulSimplesErrorAction
import com.documentos.wms_beirario.utils.extensions.alertInfoTimeDefaultAndroid
import com.documentos.wms_beirario.utils.extensions.alertMessageSucessAction
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.hideAnimationExtension
import com.documentos.wms_beirario.utils.extensions.showAnimationExtension
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
    private lateinit var deviceBluetoothAdapter: BluetoothDevice
    private val mainHandler = Handler(Looper.getMainLooper()) { m ->
        handleMessage(m)
        true
    }

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name ?: "-"
                    val deviceAddress = device?.address ?: "-"

                    val formattedName = when {
                        deviceName.contains("RFR", ignoreCase = true) -> "$deviceName (BlueBird)"
                        deviceName.contains("RFD", ignoreCase = true) -> "$deviceName (Zebra)"
                        else -> deviceName
                    }

                    adapterBluetoohSearch.updateList(
                        BluetoohRfid(
                            name = formattedName,
                            address = deviceAddress,
                            device = device
                        )
                    )
                }

                BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE -> {
                    startBluetooth(search = false)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBluetoohRfidBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupAdapters()
        initServiceBluetooth()
        callBackServiceBluetooth()
        initBtReader()
        initBluetooth()
        clickSearchBluetooh()
        clickButtonclearBluetoohPaired()


    }

    private fun callBackServiceBluetooth() {
        service?.setOnEventCallback(object : OnBluetoothEventCallback {
            override fun onDataRead(p0: ByteArray?, p1: Int) {}

            override fun onStatusChange(status: BluetoothStatus?) {
                STATUS_BLUETOOTH_RFID = status.toString()
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
        // Obtém a lista de dispositivos emparelhados
        val pairedDevices = bluetoothAdapter?.bondedDevices ?: return

        pairedDevices.forEach { device ->
            val deviceName = device.name ?: "-"
            val deviceAddress = device.address ?: "-"

            // Ajusta o nome do dispositivo com base no padrão identificado
            val deviceNameFormatted = when {
                deviceName.contains("RFR", ignoreCase = true) -> "$deviceName (BlueBird)"
                deviceName.contains("RFD", ignoreCase = true) -> "$deviceName (Zebra)"
                else -> deviceName
            }

            // Atualiza a lista de dispositivos emparelhados
            adapterBluetoohPaired.updateList(
                BluetoohRfid(
                    name = deviceNameFormatted,
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
        readerRfidBtn = BTReader.getReader(this, mainHandler)
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
                        startBluetooth(search = true)
                    }
                )
            } else {
                startBluetooth(search = true)
            }
        }
    }

    private fun startBluetooth(search: Boolean) {
        if (search) {
            adapterBluetoohSearch.clear()
            bluetoothAdapter?.startDiscovery()
            binding.buttonSearchBluetooh.text = "Buscando..."
            binding.progressSearchBluetooh.isVisible = true
        } else {
            binding.buttonSearchBluetooh.text = "Buscar"
            binding.progressSearchBluetooh.isVisible = false
        }
    }


    private fun clickSearchBluetooh() {
        binding.buttonSearchBluetooh.setOnClickListener {
            startBluetooth(search = true)
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
            bluetooth.name.contains("RFR") -> {
                readerRfidBtn?.BT_Connect(bluetooth.address)
            }

            bluetooth.name.contains("RFD") -> {
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

    private fun handleMessage(m: Message) {
        when (m.what) {
            SDConsts.Msg.BTMsg -> {
                when (m.arg1) {
                    SDConsts.BTCmdMsg.SLED_BT_CONNECTION_STATE_CHANGED -> {
                        Handler(Looper.getMainLooper()).postDelayed({
                            val isPaired = checkIfDeviceIsPaired()
                            if (isPaired) {
                                verifyConnectedBluetooh()
                            } else {
                                alertInfoTimeDefaultAndroid(
                                    message = "Para se conectar é necessário parear o dispositivo, aperte o gatilho para efetuar o pareamento",
                                    time = 10000
                                )
                            }
                        }, 3000)
                    }
                }
            }
        }
    }


    private fun checkIfDeviceIsPaired(): Boolean {
        val pairedDevices = readerRfidBtn?.BT_GetPairedDevices() ?: return false
        val deviceAddress =
            readerRfidBtn?.BT_GetConnectedDeviceAddr() // Use o método apropriado para obter o endereço do dispositivo
        // Verifica se o dispositivo conectado está na lista de dispositivos pareados
        return pairedDevices.any { it.address == deviceAddress }
    }


    private fun verifyConnectedBluetooh() {
        val nameDevice = readerRfidBtn?.BT_GetConnectedDeviceName()
        val addressDevice = readerRfidBtn?.BT_GetConnectedDeviceAddr()
        updateConnectedInfo(bluetooth = "$nameDevice - $addressDevice")
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

    companion object {
        var STATUS_BLUETOOTH_RFID = ""
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothReceiver)
        bluetoothAdapter?.cancelDiscovery()
    }
}
