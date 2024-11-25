package com.documentos.wms_beirario.model.recebimentoRfid.bluetooh

import android.bluetooth.BluetoothDevice

data class BluetoohRfid(
    val name: String,
    val address: String,
    val device: BluetoothDevice?,
)
