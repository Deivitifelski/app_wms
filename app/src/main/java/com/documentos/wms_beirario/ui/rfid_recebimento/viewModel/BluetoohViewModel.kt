package com.documentos.wms_beirario.ui.rfid_recebimento.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class BluetoothViewModel(application: Application) : AndroidViewModel(application) {
    private val bluetoothManager = application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
    private val bluetoothLeScanner: BluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

    private val _scanResults = MutableLiveData<List<ScanResult>>()
    val scanResults: LiveData<List<ScanResult>> = _scanResults

    private val resultsList = mutableListOf<ScanResult>()

    @SuppressLint("MissingPermission")
    fun startScanning() {
        if (!bluetoothAdapter.isEnabled) return

        bluetoothLeScanner.startScan(scanCallback)
    }

    @SuppressLint("MissingPermission")
    fun stopScanning() {
        bluetoothLeScanner.stopScan(scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                if (!resultsList.contains(it)) {
                    resultsList.add(it)
                    _scanResults.postValue(resultsList.toList())  // Atualiza a UI com os novos resultados
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            // Tratar erro de escaneamento
        }
    }
}
