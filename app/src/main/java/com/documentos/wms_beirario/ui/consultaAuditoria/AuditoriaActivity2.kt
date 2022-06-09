package com.documentos.wms_beirario.ui.consultaAuditoria

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityAuditoria2Binding
import com.documentos.wms_beirario.model.auditoria.BodyAuditoriaFinish
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoria1
import com.documentos.wms_beirario.repository.consultaAuditoria.AuditoriaRepository
import com.documentos.wms_beirario.ui.consultaAuditoria.adapter.AuditoriaAdapter3
import com.documentos.wms_beirario.ui.consultaAuditoria.viewModel.AuditoriaViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

class AuditoriaActivity2 : AppCompatActivity(), Observer {

    private val TAG = "AUDITORIA 2"
    private lateinit var mBinding: ActivityAuditoria2Binding
    private lateinit var mAdapter: AuditoriaAdapter3
    private lateinit var mList: MutableList<ResponseAuditoria1>
    private lateinit var mDialog: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mIntentId: String
    private lateinit var mIntentEstante: String
    private lateinit var mViewModel: AuditoriaViewModel2
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mNameUser: String


    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityAuditoria2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        setToolbar()
        initIntent()
        setCost()
        getData()
        setupRV()
        setupEdit()
        observer()
    }


    private fun setToolbar() {
        mBinding.toolbarAuditoria2.apply {
            subtitle = getVersion()
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun initIntent() {
        try {
            if (intent.extras != null) {
                val id = intent.getStringExtra("ID")
                val estante = intent.getStringExtra("ESTANTE")
                mIntentId = id.toString()
                mIntentEstante = estante.toString()
                Log.e(TAG, "initIntent -> $mIntentId - $mIntentEstante")
            }
        } catch (e: Exception) {
            mDialog.alertErroInitBack(this, this, "Erro ao receber dados!")
        }

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
        mSharedPreferences = CustomSharedPreferences(this)
        mNameUser = mSharedPreferences.getString(CustomSharedPreferences.NAME_USER).toString()
        mViewModel = ViewModelProvider(
            this, AuditoriaViewModel2.Auditoria2ViewModelFactory(
                AuditoriaRepository()
            )
        )[AuditoriaViewModel2::class.java]

        mAdapter = AuditoriaAdapter3()
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
        mDialog = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()

    }


    private fun getData() {
        Log.e(TAG, "TENTANDO ENVIAR -> id:$mIntentId estante:$mIntentEstante")
        mViewModel.getReceipt3(mIntentId, mIntentEstante)
    }

    private fun observer() {
        /**BUSCA ITENS DA ESTANTE -->*/
        mViewModel.mSucessAuditoria3Show.observe(this) { sucess ->
            if (sucess.isEmpty()) {
                mErroToastExtension(this, "Estante Vazia!")
            } else {
                mAdapter.update(sucess)
            }
        }
        mViewModel.mErrorAuditoriaShow.observe(this) { error ->
            mDialog.alertMessageErrorSimples(this, error)
        }
        mViewModel.mErrorAllShow.observe(this) { error ->
            mDialog.alertMessageErrorSimples(this, error)
        }
        mViewModel.mValidProgressEditShow.observe(this) { progress ->
            mBinding.progressAuditoria2.isVisible = progress
        }
        /**RESPOSTA DA BIPAGEM -->*/
        mViewModel.mSucessPostShow.observe(this) { sucessPost ->
            if (sucessPost.isEmpty()) {
                mErroToastExtension(this, "Estante Vazia!")
            } else {
                mAdapter.update(sucessPost)
            }
        }
        mViewModel.mErrorPostShow.observe(this) { errorPost ->
            mDialog.alertMessageErrorSimples(this, errorPost)
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
        mBinding.rvAuditoria2.apply {
            layoutManager = LinearLayoutManager(this@AuditoriaActivity2)
            adapter = mAdapter
        }
    }

    private fun sendData(codigo: String) {
        val body = BodyAuditoriaFinish(mIntentEstante, mIntentId.toInt(), codigo, mNameUser)
        mViewModel.postItens(body = body)
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