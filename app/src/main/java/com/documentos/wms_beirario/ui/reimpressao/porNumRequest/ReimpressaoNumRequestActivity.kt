package com.documentos.wms_beirario.ui.reimpressao.porNumRequest

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityReimpressaoNumRequestBinding
import com.documentos.wms_beirario.repository.reimpressao.ReimpressaoRepository
import com.documentos.wms_beirario.ui.reimpressao.dialogFragment.DialogReimpressaoDefault
import com.documentos.wms_beirario.ui.reimpressao.dialogFragment.adapterDefault.AdapterReimpressaoDefaultReanding
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarras
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

class ReimpressaoNumRequestActivity : AppCompatActivity(), Observer {

    private val TAG = "ReimpressaoNumRequestActivity"
    private lateinit var mBinding: ActivityReimpressaoNumRequestBinding
    private lateinit var mAdapter: AdapterReimpressaoDefaultReanding
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mViewModel: ReimpressaoNumRequestViewModel
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mDialog: Dialog
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false


    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityReimpressaoNumRequestBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        setupToolbar()
        initConst()
        initViewModel()
        setObservables()
        setupDataWedge()
        setupEdit()
        initRv()
    }

    override fun onResume() {
        super.onResume()
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    override fun onRestart() {
        super.onRestart()
        mDialog.hide()
    }

    private fun setupToolbar() {
        mBinding.toolbar5.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun initRv() {
        mBinding.rvNumRequest.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@ReimpressaoNumRequestActivity)
        }
    }

    private fun initConst() {
        mAdapter = AdapterReimpressaoDefaultReanding { itemClick ->
            mViewModel.getZpls(itemClick.idTarefa, itemClick.sequencialTarefa.toString())
        }
        mDialog = CustomAlertDialogCustom().progress(this)
        mDialog.hide()
        mBinding.editQrcodeNumRequest.requestFocus()
        mToast = CustomSnackBarCustom()
        mAlert = CustomAlertDialogCustom()
    }

    private fun setupEdit() {
        mBinding.editQrcodeNumRequest.extensionSetOnEnterExtensionCodBarras {
            sendData(mBinding.editQrcodeNumRequest.text.toString().trim())
            clearEdit()
        }
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this, ReimpressaoNumRequestViewModel.ReimpressaoNumRequestViewModelFactory(
                ReimpressaoRepository()
            )
        )[ReimpressaoNumRequestViewModel::class.java]
    }

    private fun setObservables() {
        mViewModel.mSucessShow.observe(this) { sucess ->
            mDialog.hide()
            clearEdit()
            if (sucess.isEmpty()) {
                mAlert.alertMessageErrorSimples(
                    this,
                    getString(R.string.denied_information),
                    2000
                )
            } else {
                UIUtil.hideKeyboard(this)
                mAdapter.submitList(sucess)
            }

        }
        mViewModel.mErrorAllShow.observe(this) { error ->
            mDialog.hide()
            mAlert.alertMessageErrorSimples(this, error, 2000)
        }

        mViewModel.mErrorHttpShow.observe(this) { error ->
            mDialog.hide()
            mAlert.alertMessageErrorSimples(this, error, 2000)
        }

        mViewModel.mSucessZplsShows.observe(this) { sucessZpl ->
            try {
                DialogReimpressaoDefault(sucessZpl).show(
                    supportFragmentManager,
                    "DIALOG_REIMPRESSAO"
                )
            } catch (e: Exception) {
                mError("Erro ao criar tela de reimpressoes!")
            }
        }
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
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            Log.e("REIMPRESSAO NUM PEDIDO", "onNewIntent --> $scanData")
            sendData(scanData!!)
            clearEdit()
        }
    }

    private fun sendData(scanData: String) {
        try {
            if (scanData.isEmpty()) {
                mError("Campo vazio!")
            } else {
                mDialog.show()
                mViewModel.getNumRequest(scanData)
                clearEdit()
            }
        } catch (e: Exception) {
            mError("Erro ao receber scan!${e.cause}")
        }
    }

    private fun mError(msg: String) {
        vibrateExtension(500)
        mToast.toastCustomError(this, msg)
    }

    private fun clearEdit() {
        mBinding.editQrcodeNumRequest.setText("")
        mBinding.editQrcodeNumRequest.text!!.clear()
        mBinding.editQrcodeNumRequest.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDialog.dismiss()
        unregisterReceiver(receiver)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}