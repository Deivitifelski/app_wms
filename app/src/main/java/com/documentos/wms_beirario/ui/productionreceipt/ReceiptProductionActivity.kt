package com.documentos.wms_beirario.ui.productionreceipt

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.ViewModelSharedDataWedgeScan
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.example.br_coletores.viewModels.scanner.ObservableObject
import java.util.*

class ReceiptProductionActivity : AppCompatActivity(), Observer {

    private lateinit var mViewModelDataWedge: ViewModelSharedDataWedgeScan
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_production)

        mViewModelDataWedge = ViewModelProvider(this)[ViewModelSharedDataWedgeScan::class.java]
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
    }


    override fun onResume() {
        super.onResume()
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            if (intent.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
                val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
                if (scanData != null) {
                    mViewModelDataWedge.returnScan(scanData)
                }
            }
        }
    }

    override fun update(o: Observable?, arg: Any?) {}
}