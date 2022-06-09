package com.documentos.wms_beirario.ui.consultaAuditoria

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityAuditoria2Binding
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoria1
import com.documentos.wms_beirario.ui.consultaAuditoria.adapter.AuditoriaAdapter2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

class AuditoriaActivity2 : AppCompatActivity(), Observer {

    private val TAG = "AUDITORIA 2"
    private lateinit var mBinding: ActivityAuditoria2Binding
    private lateinit var mAdapter: AuditoriaAdapter2
    private lateinit var mList: MutableList<ResponseAuditoria1>
    private lateinit var mDialog: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityAuditoria2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        setCost()
        setToolbar()
        setupRV()
        setupEdit()
    }

    override fun onResume() {
        super.onResume()
        clearEdit()
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun setCost() {
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
        mDialog = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()

    }

    private fun setToolbar() {
        mBinding.toolbarAuditoria2.apply {
            subtitle = getVersion()
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setupEdit() {
        mBinding.editAuditoria02.extensionSetOnEnterExtensionCodBarras {
            if (mBinding.editAuditoria02.text.toString().isNullOrEmpty()) {
                mBinding.editLayoutNumAuditoria2.shake { mErroToastExtension(this, "Campo Vazio!") }
            } else {
                sendData(mBinding.editAuditoria02.text.toString())
                clearEdit()
            }
        }
    }

    private fun setupRV() {
        mAdapter = AuditoriaAdapter2()
        mBinding.rvAuditoria2.apply {
            layoutManager = LinearLayoutManager(this@AuditoriaActivity2)
            adapter = mAdapter
        }
        mBinding.progressAuditoria2.isVisible = true
        Handler(Looper.getMainLooper()).postDelayed({
            mBinding.progressAuditoria2.isVisible = false
            mAdapter.update(mList)
        }, 2000)
    }

    private fun sendData(codigo: String) {
//        val response = mAdapter.returnItem(codigo)
//        if (mList.contains(response)) {
//            mList.remove(response)
//            mAdapter.update(mList)
//        } else {
//            mErroToastExtension(this, "Código não existe!")
//        }
        UIUtil.hideKeyboard(this)
    }

    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            Log.e(TAG, "onNewIntent -> $scanData")
            sendData(scanData.toString())
            clearEdit()
        }
    }

    private fun clearEdit() {
        mBinding.editAuditoria02.text?.clear()
        mBinding.editAuditoria02.setText("")
        mBinding.editAuditoria02.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}