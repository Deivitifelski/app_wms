package com.documentos.wms_beirario.ui.rfid_recebimento.bluetoohRfid

import android.bluetooth.BluetoothDevice
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import co.kr.bluebird.sled.BTReader
import co.kr.bluebird.sled.SDConsts
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityBluetoohRfidBinding
import com.documentos.wms_beirario.model.recebimentoRfid.bluetooh.BluetoohRfid
import com.documentos.wms_beirario.utils.extensions.alertConfirmation
import com.documentos.wms_beirario.utils.extensions.alertDefaulSimplesError
import com.documentos.wms_beirario.utils.extensions.alertInfoTimeDefaultAndroid
import com.documentos.wms_beirario.utils.extensions.alertMessageSucessAction
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.toastDefault

class BluetoohRfidActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBluetoohRfidBinding
    private var readerRfidBtn: BTReader? = null
    private lateinit var adapterBluetoohSearch: BluetoohRfiBlueBirdSearchdAdapter
    private lateinit var adapterBluetoohPaired: BluetoohRfidPairedAdapter

    private val mainHandler = Handler(Looper.getMainLooper()) { m ->
        handleMessage(m)
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBluetoohRfidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapters()
        initBtReader()
        clickSearchBluetooh()
        searchDevicesPaired()
        clickButtonclearBluetoohPaired()


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

    private fun searchDevicesPaired() {
        if (readerRfidBtn != null) {
            val listPairedDevices = readerRfidBtn?.BT_GetPairedDevices()
            if (!listPairedDevices.isNullOrEmpty()) {
                binding.linearBluetoohPaired.visibility = View.GONE
                binding.buttonClearPaired.isEnabled = true
                listPairedDevices.forEach {
                    Log.d(TAG, "searchDevicesPaired: ${it.name} - ${it.address}")
                    adapterBluetoohPaired.updateList(
                        BluetoohRfid(
                            name = it.name,
                            addres = it.address
                        )
                    )
                }
            } else {
                binding.linearBluetoohPaired.visibility = View.VISIBLE
                binding.buttonClearPaired.isEnabled = false
            }
        }
    }

    private fun initBtReader() {
        readerRfidBtn = BTReader.getReader(this, mainHandler)
        readerRfidBtn?.SD_Open()
        if (readerRfidBtn != null) {
            if (readerRfidBtn?.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED) {
                alertConfirmation(
                    title = "Conexão",
                    icon = R.drawable.icon_bluetooh_setting,
                    message = "Você esta conectado com leitor: ${readerRfidBtn?.BT_GetConnectedDeviceName()} - ${readerRfidBtn?.BT_GetConnectedDeviceAddr()}\nDeseja alterar o leitor?",
                    actionNo = {
                        finish()
                        extensionBackActivityanimation()
                    },
                    actionYes = {
                        startBluetoohScan()
                    })
            }else{
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
        adapterBluetoohSearch = BluetoohRfiBlueBirdSearchdAdapter { bluetooh ->
            readerRfidBtn?.BT_Connect(bluetooh.addres)
        }

        adapterBluetoohPaired = BluetoohRfidPairedAdapter { bluetooh ->
            readerRfidBtn?.BT_Connect(bluetooh.addres)
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

    private fun updateConnectedInfo(device: String) {
        alertMessageSucessAction(message = "Conectado com:\n$device", action = {
            finish()
            extensionBackActivityanimation()
        })
    }

    private fun handleMessage(m: Message) {
        when (m.what) {
            SDConsts.Msg.BTMsg -> {
                when (m.arg1) {
                    SDConsts.BTCmdMsg.SLED_BT_DEVICE_FOUND -> {
                        val bundle = m.obj as? Bundle
                        bundle?.let { bundle ->
                            val name = bundle.getString(SDConsts.BT_BUNDLE_NAME_KEY)
                            val addr = bundle.getString(SDConsts.BT_BUNDLE_ADDR_KEY)
                            Log.d(TAG, "Encontrados SLED_BT_DEVICE_FOUND: $name $addr")
                            adapterBluetoohSearch.updateList(
                                BluetoohRfid(
                                    name = name ?: "-",
                                    addres = addr ?: "-"
                                )
                            )
                        }
                    }

                    SDConsts.BTCmdMsg.SLED_BT_BOND_STATE_CHAGNED -> {
                        val bundle = m.obj as? Bundle
                        bundle?.let {
                            val name = it.getString(SDConsts.BT_BUNDLE_NAME_KEY)
                            val addr = it.getString(SDConsts.BT_BUNDLE_ADDR_KEY)
                            val newBondState = it.getInt(SDConsts.BT_BUNDLE_BOND_NEW_STATE_KEY)
                            val prevBondState = it.getInt(SDConsts.BT_BUNDLE_BOND_PREV_STATE_KEY)
                            Log.d(
                                TAG,
                                "Encontrados SLED_BT_BOND_STATE_CHAGNED: $name $addr $newBondState $prevBondState"
                            )
                        }
                    }

                    SDConsts.BTCmdMsg.SLED_BT_CONNECTION_STATE_CHANGED -> {
                        verifyConnectedBluetooh()
                    }

                    SDConsts.BTCmdMsg.SLED_BT_DISCOVERY_STARTED -> {
                        binding.progressSearchBluetooh.visibility = View.VISIBLE
                        binding.buttonSearchBluetooh.text = "Buscando..."
                    }

                    SDConsts.BTCmdMsg.SLED_BT_DISCOVERY_FINISHED -> {
                        binding.progressSearchBluetooh.visibility = View.GONE
                        binding.buttonSearchBluetooh.text = "Procurar"
                    }

                    SDConsts.BTCmdMsg.SLED_BT_CONNECTION_ESTABLISHED -> {
                        updateConnectedInfo("${readerRfidBtn?.BT_GetConnectedDeviceName()}\n${readerRfidBtn?.BT_GetConnectedDeviceAddr()}")
                    }
                }
            }

            SDConsts.Msg.SDMsg -> {
                if (m.arg1 == SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED && m.arg2 == SDConsts.SDCommonResult.SMARTBATT_CRITICAL_TEMPERATURE) {
                    runOnUiThread {
                        alertDefaulSimplesError(message = "Dispositivo em temperatura crítica")
                    }
                } else if (m.arg1 == SDConsts.SDCmdMsg.SLED_HOTSWAP_STATE_CHANGED) {
                    val message = if (m.arg2 == SDConsts.SDHotswapState.HOTSWAP_STATE) {
                        "HOTSWAP STATE CHANGED = HOTSWAP_STATE"
                    } else {
                        "HOTSWAP STATE CHANGED = NORMAL_STATE"
                    }
                    Log.d(TAG, message)
                }
            }
        }
    }

    private fun verifyConnectedBluetooh() {
        val connectionState = readerRfidBtn?.BT_GetConnectState()
        when (connectionState) {
            SDConsts.BTConnectState.CONNECTED -> {
                updateConnectedInfo("${readerRfidBtn?.BT_GetConnectedDeviceName()}\n${readerRfidBtn?.BT_GetConnectedDeviceAddr()}")
            }

            SDConsts.BTConnectState.SD_NOT_CONNECTED -> {
                toastDefault(message = "Conexão perdida. Tentando reconectar...")
            }

            SDConsts.BTConnectState.CONNECTING -> {
                toastDefault(message = "Tentando conectar...")
            }

            else -> {
                toastDefault(message = "Estado de conexão desconhecido")
            }
        }
    }

}
