package com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityRfidLeituraEpcBinding
import com.documentos.wms_beirario.ui.rfid_recebimento.detalhesEpc.DetalheCodigoEpcActivity
import com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.adapter.LeituraRfidAdapter
import com.documentos.wms_beirario.utils.extensions.alertConfirmation
import com.documentos.wms_beirario.utils.extensions.alertInfoTimeDefaultAndroid
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionStartActivity
import com.google.android.material.chip.Chip
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.RFIDReader
import com.zebra.rfid.api3.ReaderDevice
import com.zebra.rfid.api3.Readers
import com.zebra.rfid.api3.RfidEventsListener
import com.zebra.rfid.api3.RfidReadEvents
import com.zebra.rfid.api3.RfidStatusEvents


class RfidLeituraEpcActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRfidLeituraEpcBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var adapterLeituras: LeituraRfidAdapter
    private val zebraDeviceNamePrefix = "RFD"
    private val devicesList = mutableListOf<String>()
    private lateinit var deviceListAdapter: ArrayAdapter<String>
    private lateinit var reader: RFIDReader

    // Usando o ActivityResultLauncher para solicitar ativação do Bluetooth
    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("BLUETOOTH", "Bluetooth ativado. Iniciando descoberta...")
            startBluetoothDiscovery()
        } else {
            Log.d("BLUETOOTH", "Bluetooth não foi ativado.")
        }
    }

    // Usando o ActivityResultLauncher para solicitar permissões
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("BLUETOOTH", "Permissão de localização concedida. Iniciando descoberta...")
            startBluetoothDiscovery()
        } else {
            Log.d(
                "BLUETOOTH",
                "Permissão de localização negada. Não é possível iniciar a descoberta."
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRfidLeituraEpcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBluetooh()
        clickButtonConfig()
        setupAdapter()
        cliqueChips()
        clickButtonLimpar()
        clickButtonFinalizar()
        connectReader()

    }

    private fun connectReader() {
        try {
            // Neste caso, não estamos interessados em leitores Bluetooth
            val readers = Readers(this, ENUM_TRANSPORT.SERVICE_SERIAL)
            val readerDevices: ArrayList<ReaderDevice> = readers.GetAvailableRFIDReaderList()

            if (readerDevices.isNotEmpty()) {
                reader = readerDevices[0].rfidReader
                reader.connect()

                // Configurar onde queremos ser notificados sobre eventos interessantes
                reader.Events.addEventsListener(object : RfidEventsListener {
                    override fun eventReadNotify(rfidReadEvents: RfidReadEvents?) {
                        val detectedTag = rfidReadEvents?.readEventData?.tagData

                        if (detectedTag != null) {
                            reader.Actions.Inventory.stop()

                            val tagID = detectedTag.tagID
                            println("Detected tag: $tagID")
                            alertInfoTimeDefaultAndroid(
                                message = tagID,
                                title = "TAG ENCONTRADA",
                                time = 3000
                            )
                        }
                    }

                    override fun eventStatusNotify(p0: RfidStatusEvents?) {

                    }

                })

                // Queremos saber quando o botão de gatilho é pressionado
                reader.Events.setHandheldEvent(true)
                // Queremos saber quando as tags são detectadas
                reader.Events.setTagReadEvent(true)
                reader.Events.setAttachTagDataWithReadEvent(true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setupBluetooh() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        deviceListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, devicesList)
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
    }

    private fun clickButtonConfig() {
        binding.iconConfig.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.menu_rfid_recebimento, popup.menu)

            // Definir o comportamento ao clicar em cada item
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_option_1 -> {
                        startBluetoothDiscovery()
                        true
                    }

                    R.id.menu_option_2 -> {

                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }
    }


    private fun startBluetoothDiscovery() {
        // Verificar se o dispositivo suporta Bluetooth
        if (bluetoothAdapter == null) {
            Log.d("BLUETOOTH", "Este dispositivo não suporta Bluetooth.")
            return
        }

        // Verificar se o Bluetooth está ativado
        if (!bluetoothAdapter.isEnabled) {
            alertConfirmation(
                title = "Ativar Bluetooth",
                message = "O Bluetooth está desativado. Deseja ativá-lo para iniciar a descoberta de dispositivos?",
                icon = R.drawable.ic_baseline_location_address,
                actionNo = {},
                actionYes = {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    enableBluetoothLauncher.launch(enableBtIntent)
                })
            return
        }

        // Verificar se a permissão de localização foi concedida
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            alertConfirmation(
                title = "Permissão Necessária",
                message = "A permissão de localização é necessária para descobrir dispositivos Bluetooth. Deseja conceder esta permissão?",
                icon = R.drawable.ic_baseline_location_address,
                actionNo = {},
                actionYes = { requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) })
            return
        }

        // Se o Bluetooth estiver ativado e a permissão for concedida, iniciar a descoberta
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        } else {
            bluetoothAdapter.startDiscovery()
            showBluetoothDevicesModal()
        }
        Log.d("BLUETOOTH", "Descoberta de dispositivos Bluetooth iniciada...")
    }


    // BroadcastReceiver para dispositivos Bluetooth descobertos
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action

            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                device?.let {
                    val deviceName = it.name ?: "Dispositivo desconhecido"
                    val deviceAddress = it.address
                    val deviceInfo = "$deviceName - $deviceAddress"

                    // Evitar dispositivos duplicados
                    if (!devicesList.contains(deviceInfo)) {
                        devicesList.add(deviceInfo)
                        deviceListAdapter.notifyDataSetChanged() // Atualizar o modal

                        // Filtrar dispositivos Zebra (RFD40)
                        if (deviceName.startsWith(zebraDeviceNamePrefix)) {
                            Log.d(
                                "BLUETOOTH",
                                "Dispositivo Zebra encontrado: $deviceName - $deviceAddress"
                            )
                        }
                    }
                }
            }
        }
    }

    // Exibir modal de dispositivos Bluetooth encontrados
    private fun showBluetoothDevicesModal() {
        val listView = ListView(this)
        listView.adapter = deviceListAdapter

        val dialog = AlertDialog.Builder(this)
            .setTitle("Dispositivos Encontrados")
            .setView(listView)
            .setPositiveButton("Fechar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()

        // Ação ao selecionar um dispositivo
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedDevice = devicesList[position]
            Toast.makeText(this, "Selecionado: $selectedDevice", Toast.LENGTH_SHORT).show()
            // Aqui você pode iniciar a conexão com o dispositivo selecionado, se necessário
        }
    }


    private fun clickButtonFinalizar() {
        binding.buttonFinalizar.setOnClickListener {
            alertConfirmation(
                message = "Deseja realizar o recebimento?",
                actionNo = {},
                actionYes = {
                    finish()
                    extensionBackActivityanimation()
                }
            )
        }
    }

    private fun clickButtonLimpar() {
        binding.buttonClear.setOnClickListener {
            alertConfirmation(
                message = "Deseja limpar as leituras e iniciar novamente?",
                actionNo = {},
                actionYes = {}
            )
        }
    }

    private fun setupAdapter() {
        cliqueItemDaLista()
        binding.rvItemEpcRecebimento.apply {
            adapter = adapterLeituras
            layoutManager = LinearLayoutManager(this@RfidLeituraEpcActivity)
        }
    }

    private fun cliqueItemDaLista() {
        adapterLeituras = LeituraRfidAdapter {
            extensionStartActivity(DetalheCodigoEpcActivity())
        }
    }

    private fun cliqueChips() {
        binding.chipRelacionados.isChecked = true
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chipId = checkedIds[0]
                val chip = group.findViewById<Chip>(chipId)
                when (chip.id) {
                    R.id.chip_faltando -> {}
                    R.id.chip_encontrados -> {}
                    R.id.chip_nao_relacionado -> {}
                    R.id.chip_relacionados -> {}
                }
            } else {
                Toast.makeText(this, "Nenhum Chip selecionado", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }
        unregisterReceiver(receiver)
    }
}
