package com.documentos.wms_beirario.ui.rfid_recebimento

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class BluetoothHelper(
    private val activity: Activity,
    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>>
) {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val _foundDevices = MutableLiveData<List<BluetoothDevice>>()
    val foundDevices: LiveData<List<BluetoothDevice>> = _foundDevices

    private val _bluetoothStatus = MutableLiveData<Boolean>()
    val bluetoothStatus: LiveData<Boolean> = _bluetoothStatus

    private val devices = mutableListOf<BluetoothDevice>()

    fun startBluetoothDiscovery() {
        if (!isBluetoothEnabled()) {
            bluetoothAdapter?.enable()
            _bluetoothStatus.value = true
        } else {
            _bluetoothStatus.value = true
            checkPermissionsAndStartDiscovery()
        }
    }

    private fun checkPermissionsAndStartDiscovery() {
        val requiredPermissions = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )

        val missingPermissions = requiredPermissions.filter {
            ActivityCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isEmpty()) {
            startDiscovery()
        } else {
            requestPermissionLauncher.launch(requiredPermissions)
        }
    }

    private fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    private fun startDiscovery() {
        bluetoothAdapter?.let { adapter ->
            if (adapter.isDiscovering) {
                adapter.cancelDiscovery()
            }
            devices.clear()
            adapter.startDiscovery()
            setupDiscoveryReceiver()
        }
    }

    private fun setupDiscoveryReceiver() {
        val filter = android.content.IntentFilter(BluetoothDevice.ACTION_FOUND)
        activity.registerReceiver(object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: android.content.Intent?) {
                val action: String? = intent?.action
                if (action == BluetoothDevice.ACTION_FOUND) {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        if (!devices.contains(it)) {
                            devices.add(it)
                            _foundDevices.postValue(devices)
                        }
                    }
                }
            }
        }, filter)
    }

    fun stopDiscovery() {
        bluetoothAdapter?.cancelDiscovery()
    }
}
