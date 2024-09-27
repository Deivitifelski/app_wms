package com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import app.akexorcist.bluetotohspp.library.BluetoothState.REQUEST_ENABLE_BT
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityRfidLeituraEpcBinding
import com.documentos.wms_beirario.model.recebimentoRfid.LeituraRfidEpc
import com.documentos.wms_beirario.ui.rfid_recebimento.detalhesEpc.DetalheCodigoEpcActivity
import com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.adapter.LeituraRfidAdapter
import com.documentos.wms_beirario.utils.extensions.alertConfirmation
import com.documentos.wms_beirario.utils.extensions.alertInfoTimeDefaultAndroid
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionStartActivity
import com.google.android.material.chip.Chip
import kotlin.random.Random

class RfidLeituraEpcActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var binding: ActivityRfidLeituraEpcBinding
    private lateinit var adapterLeituras: LeituraRfidAdapter

    private val zebraDeviceNamePrefix = "RFD" // Prefixo para dispositivos Zebra RFD40
    private val devicesList = mutableListOf<String>() // Lista de dispositivos encontrados
    private lateinit var deviceListAdapter: ArrayAdapter<String> // Adapter para exibir dispositivos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRfidLeituraEpcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Solicitar permissões Bluetooth
        requestBluetoothPermissions()

        setupAdapter()
        cliqueChips()
        clickButtonLimpar()
        clickButtonFinalizar()

        // Inicializar BluetoothAdapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // Configurar o ArrayAdapter para a lista de dispositivos
        deviceListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, devicesList)

        // Registrar o BroadcastReceiver para capturar dispositivos encontrados
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        // Iniciar a descoberta de dispositivos Bluetooth
        startBluetoothDiscovery()

        // Mostrar modal de dispositivos Bluetooth encontrados
        showBluetoothDevicesModal()
    }

    private fun startBluetoothDiscovery() {
        if (bluetoothAdapter.isEnabled) {
            if (bluetoothAdapter.isDiscovering) {
                bluetoothAdapter.cancelDiscovery()
            }
            bluetoothAdapter.startDiscovery()
            Log.d("BLUETOOTH", "Descoberta de dispositivos iniciada...")
        } else {
            Log.d("BLUETOOTH", "Bluetooth está desativado. Solicitando ativação...")
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    // BroadcastReceiver para dispositivos Bluetooth descobertos
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action

            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
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
                            Log.d("BLUETOOTH", "Dispositivo Zebra encontrado: $deviceName - $deviceAddress")
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

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE), 1)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION), 1)
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
