package com.documentos.wms_beirario.utils.extensions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.documentos.wms_beirario.data.CustomSharedPreferences

fun Context.registerDataWedgeReceiver(onDataReceived: (barcodeData: String) -> Unit): BroadcastReceiver {
    val dataWedgeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == CustomSharedPreferences.ACTION_DATAWEDGE) {
                val barcodeData = intent.getStringExtra("com.symbol.datawedge.data_string")
                if (barcodeData != null) {
                    onDataReceived(barcodeData)
                }
            }
        }
    }

    val intentFilter = IntentFilter().apply {
        addAction(CustomSharedPreferences.ACTION_DATAWEDGE)
    }
    registerReceiver(dataWedgeReceiver, intentFilter)

    return dataWedgeReceiver
}