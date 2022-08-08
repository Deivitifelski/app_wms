package com.documentos.wms_beirario.ui.armazenagem

import ArmazenagemResponse
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityArmazenagem2Binding
import com.documentos.wms_beirario.model.armazenagem.ArmazemRequestFinish
import com.documentos.wms_beirario.repository.armazenagem.ArmazenagemRepository
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarras
import com.documentos.wms_beirario.utils.extensions.getVersion
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import java.util.*

class ArmazenagemActivity2 : AppCompatActivity(), Observer {

    private lateinit var mBinding: ActivityArmazenagem2Binding
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mViewModel: ArmazenagemViewModel
    private lateinit var mDataIntent: ArmazenagemResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArmazenagem2Binding.inflate(layoutInflater)
        setContentView(mBinding.root)
        getDataIntent()
        initViewModel()
        setToolbar()
        initConst()
        clearEdit()
        setupDataWedge()
        setupEdit()
        setObservables()
    }

    override fun onResume() {
        super.onResume()
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun setToolbar() {
        mBinding.toolbarArmazenagem2.apply {
            CustomMediaSonsMp3().somClick(this@ArmazenagemActivity2)
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = "Armazenagem [${getVersion()}]"
        }
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            ArmazenagemViewModel.ArmazenagemViewModelFactory(ArmazenagemRepository())
        )[ArmazenagemViewModel::class.java]
    }

    private fun getDataIntent() {
        try {
            if (intent != null) {
                mDataIntent = intent.getParcelableExtra("ARMAZEM_SEND")!!
                mBinding.txtDestinoApi.text = mDataIntent.visualEnderecoDestino
                mBinding.txtOrigemApi.text = mDataIntent.visualEnderecoOrigem
            } else {
                mAlert.alertErroInitBack(this, this, "Ops,erro inesperado!")
            }
        } catch (e: Exception) {
            mAlert.alertErroInitBack(this, this, e.toString())
        }
    }

    private fun setupEdit() {
        mBinding.editTxtArmazenagem02.extensionSetOnEnterExtensionCodBarras {
            sendData(mBinding.editTxtArmazenagem02.text.toString())
            clearEdit()
        }
    }


    private fun sendData(mQrcode: String) {
        mViewModel.postFinish(ArmazemRequestFinish(mDataIntent.id, mQrcode))
        Log.e(
            "ARMAZENAGEM FINISH ->",
            "Itens enviados para fnalizar tarefa --> ${mDataIntent.id} || $mQrcode"
        )
        clearEdit()
    }

    private fun setObservables() {
        mViewModel.mSucessShow2.observe(this) {
            clearEdit()
            vibrateExtension(500)
            mAlert.alertSucessFinishBack(this, "Armazenado com sucesso!")
        }

        mViewModel.mProgressInitShow.observe(this) { progress ->
            mBinding.progressArmazenagemFinalizar.isVisible = progress
        }

        mViewModel.mErrorHttpShow.observe(this) { error ->
            mAlert.alertMessageErrorSimples(this, error)
            clearEdit()
        }

        mViewModel.mErrorAllShow.observe(this) { error ->
            mAlert.alertMessageErrorSimples(this, error)
            clearEdit()
        }
    }

    private fun initConst() {
        mBinding.editTxtArmazenagem02.requestFocus()
        mBinding.progressArmazenagemFinalizar.isVisible = false
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    private fun setupDataWedge() {
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
    }

    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData =
                intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING).toString()
            sendData(scanData)
            clearEdit()
        }
    }

    private fun clearEdit() {
        mBinding.editTxtArmazenagem02.setText("")
        mBinding.editTxtArmazenagem02.text!!.clear()
        mBinding.editTxtArmazenagem02.requestFocus()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

}