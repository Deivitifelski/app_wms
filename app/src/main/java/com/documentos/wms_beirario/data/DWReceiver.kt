package com.documentos.wms_beirario.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.br_coletores.viewModels.scanner.ObservableObject

class DWReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //  This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        //  Notify registered observers
        ObservableObject.instance.updateValue(intent)
    }
}