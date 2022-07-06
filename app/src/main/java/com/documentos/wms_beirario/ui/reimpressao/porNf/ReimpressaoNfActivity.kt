package com.documentos.wms_beirario.ui.reimpressao.porNf

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
import com.documentos.wms_beirario.databinding.ActivityReimpressaoNfBinding
import com.documentos.wms_beirario.repository.reimpressao.ReimpressaoRepository
import com.documentos.wms_beirario.ui.reimpressao.dialogFragment.DialogReimpressaoDefault
import com.documentos.wms_beirario.ui.reimpressao.dialogFragment.adapterDefault.AdapterReimpressaoDefaultReanding
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.changedEditText
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarras
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

class ReimpressaoNfActivity : AppCompatActivity(), Observer {

    private val TAG = "ReimpressaoNfActivity"
    private lateinit var mBinding: ActivityReimpressaoNfBinding
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mViewModel: ReimpressaoNfViewModel
    private lateinit var mAdapter: AdapterReimpressaoDefaultReanding
    private lateinit var mDialog: Dialog
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityReimpressaoNfBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        setupToolbar()
        initConst()
        initVewModel()
        setObservables()
        setupDataWedge()
        setupEdit()
        initRv()
        clickButtonSend()
        validButton()
        mBinding.buttonEnviar.isEnabled = false
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

    private fun initVewModel() {
        mViewModel = ViewModelProvider(
            this, ReimpressaoNfViewModel.ReimpressaoNfViewModelFactory(
                ReimpressaoRepository()
            )
        )[ReimpressaoNfViewModel::class.java]
    }

    private fun initRv() {
        mBinding.rvReimpressaoNf.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@ReimpressaoNfActivity)
        }
    }

    private fun setupEdit() {
        mBinding.editNfNumNf.extensionSetOnEnterExtensionCodBarras {
            try {
                if (mBinding.editNfNumNf.text.toString().isNotEmpty()) {
                    mBinding.editSerieNf.requestFocus()
                } else {
                    mError("Campo Vazio")
                }
            } catch (e: Exception) {
                clearEdit()
                mError("$e")
            }
        }

        mBinding.editSerieNf.extensionSetOnEnterExtensionCodBarras {
            try {
                if (mBinding.editSerieNf.text.toString().isNotEmpty()) {
                    UIUtil.hideKeyboard(this)
                } else {
                    mError("Campo Vazio")
                }
            } catch (e: Exception) {
                clearEdit()
                mError("$e")
            }
        }
    }

    private fun validButton() {
        mBinding.editNfNumNf.changedEditText { editNfNumNf(mBinding.editNfNumNf.text.toString()) }
        mBinding.editSerieNf.changedEditText { editSerieNf(mBinding.editSerieNf.text.toString()) }
    }

    private fun editSerieNf(s: String) {
        mBinding.buttonEnviar.isEnabled =
            mBinding.editNfNumNf.text!!.isNotEmpty() && s.isNotEmpty()
    }

    private fun editNfNumNf(s: String) {
        mBinding.buttonEnviar.isEnabled =
            mBinding.editSerieNf.text!!.isNotEmpty() && s.isNotEmpty()
    }

    private fun clickButtonSend() {
        mBinding.buttonEnviar.setOnClickListener {
            mDialog.show()
            mViewModel.getNumNf(
                mBinding.editNfNumNf.text.toString(),
                mBinding.editSerieNf.text.toString()
            )
            clearEdit()
        }
    }

    private fun initConst() {
        mAdapter = AdapterReimpressaoDefaultReanding { itemClick ->
            mViewModel.getZpls(itemClick.idTarefa, itemClick.sequencialTarefa.toString())
        }
        mDialog = CustomAlertDialogCustom().progress(this)
        mDialog.hide()
        mBinding.editNfNumNf.requestFocus()
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
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            Log.e("REIMPRESSAO NF", "onNewIntent --> $scanData")
            clearEdit()
        }
    }

    private fun clearEdit() {
        mBinding.editNfNumNf.setText("")
        mBinding.editNfNumNf.text!!.clear()
        mBinding.editSerieNf.setText("")
        mBinding.editSerieNf.text!!.clear()
        mBinding.editNfNumNf.requestFocus()
    }

    private fun setObservables() {
        mViewModel.mSucessShow.observe(this) { sucess ->
            mDialog.hide()
            if (sucess.isEmpty()) {
                mAlert.alertMessageErrorSimples(
                    this,
                    getString(R.string.denied_information),
                    2000
                )
            } else {
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

    private fun mError(msg: String) {
        vibrateExtension(500)
        mToast.toastCustomError(this, msg)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mDialog.dismiss()
        unregisterReceiver(receiver)
    }
}