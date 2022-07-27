package com.documentos.wms_beirario.ui.separacao.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.ActivitySeparaco3Binding
import com.documentos.wms_beirario.databinding.LayoutAlertSucessCustomBinding
import com.documentos.wms_beirario.model.separation.ResponseEstantesAndaresSeparation3Item
import com.documentos.wms_beirario.model.separation.bodySeparation3
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.separacao.adapter.AdapterSeparation3
import com.documentos.wms_beirario.ui.separacao.viewModel.SeparationViewModel4
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.*
import java.util.*

class SeparacaoActivity4 : AppCompatActivity(), Observer {

    private val TAG = "SEPARATION 3"
    private lateinit var mBinding: ActivitySeparaco3Binding
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mProgress: Dialog
    private lateinit var mIntent: ResponseEstantesAndaresSeparation3Item
    private lateinit var mViewModel: SeparationViewModel4
    private lateinit var mADapterSeparation3: AdapterSeparation3
    private lateinit var mPrinter: PrinterConnection


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySeparaco3Binding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setupEdit()
        initConst()
        initIntent()
        setToolbar()
        setupObservables()
        mBinding.editSeparation3.requestFocus()
        setupDataWedge()
    }

    override fun onResume() {
        super.onResume()
        clearText()
        hideKeyExtensionActivity(mBinding.editSeparation3)

    }

    private fun setToolbar() {
        mBinding.toolbarSeparacao3.apply {
            title = "${ServiceApi.IDARMAZEM} |  ${mIntent.ENDERECO_VISUAL_ORIGEM}"
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun initConst() {
        mViewModel = ViewModelProvider(
            this, SeparationViewModel4.ViewModelSeparationFactory3(
                SeparacaoRepository()
            )
        )[SeparationViewModel4::class.java]
        mADapterSeparation3 = AdapterSeparation3()
        mBinding.rvSeparationProdAndress.apply {
            layoutManager = LinearLayoutManager(this@SeparacaoActivity4)
            adapter = mADapterSeparation3
        }
        mProgress = CustomAlertDialogCustom().progress(this)
        mProgress.hide()
        mAlert = CustomAlertDialogCustom()
    }

    private fun initIntent() {
        try {
            if (intent.extras != null) {
                mIntent =
                    intent.getSerializableExtra("DADOS_BIPAGEM") as ResponseEstantesAndaresSeparation3Item
                Log.e(TAG, "Dados recebidos intent de SEPARATION 2: $mIntent")
                mViewModel.getProdAndress(
                    mIntent.ID_ENDERECO_ORIGEM.toString()
                )
            }
        } catch (e: Exception) {
            mErroToastExtension(this, "Erro ao receber dados!")
        }
    }

    private fun setupEdit() {
        mBinding.editSeparation3.extensionSetOnEnterExtensionCodBarras {
            sendData(mBinding.editSeparation3.text.toString())
        }
    }

    private fun clearText() {
        mProgress.hide()
        mBinding.editSeparation3.text?.clear()
        mBinding.editSeparation3.clickHideShowKey()
        mBinding.editSeparation3.setText("")
        hideKey()
    }

    private fun hideKey() {
        val view = currentFocus
        view?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun setupObservables() {
        /**SUCESSO NO GET AO ENTRAR N TELA -->*/
        mViewModel.mSucessGetShow.observe(this) { sucess ->
            if (sucess.isEmpty()) {
                alertMessageSucess("Itens Separados com sucesso")
            } else {
                mADapterSeparation3.update(sucess)
            }
        }
        /**SUCESSO DO POST -->*/
        mViewModel.mSucessPostShow.observe(this) { sucessUnit ->
            try {
                initConst()
                initIntent()
            } catch (e: Exception) {
                mErroToastExtension(this, "Erro ao tentar finalizar!")
            } finally {
                clearText()
            }
        }
        mViewModel.mErrorSeparationSShowAll.observe(this) { errorAll ->
            mAlert.alertMessageErrorSimples(this, errorAll)
        }

        mViewModel.mErrorShow.observe(this) { error ->
            mAlert.alertMessageErrorSimples(this, error)
        }
        mViewModel.mValidationProgressShow.observe(this) { progress ->
            if (progress) mProgress.show() else mProgress.hide()
        }
    }

    private fun sendData(scanData: String) {
        try {
            if (scanData.isNullOrEmpty()) {
                mBinding.editLayoutSeparation3.shake {
                    mErroToastExtension(this, "Preencha o campo!")
                }
            } else {
                val body = bodySeparation3(scanData, mIntent.ID_ENDERECO_ORIGEM)
                mViewModel.postAndress(bodySeparation3 = body)
                clearText()
            }
        } catch (e: Exception) {
            mErroToastExtension(this, "${e.message}")
        } finally {
            clearText()
        }
    }


    private fun setupDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
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
            Log.e("SEPARAÇAO 4", "Dados recebbidos via intent --> $scanData")
            sendData(scanData = scanData!!)
            clearText()
        }
    }

    /**
     * MODAL QUANDO FINALIZOU TODOS OS ITENS -->
     */
    private fun alertMessageSucess(message: String) {
        val mAlert = AlertDialog.Builder(this)
        mAlert.setCancelable(false)
        val binding = LayoutAlertSucessCustomBinding.inflate(layoutInflater)
        mAlert.setView(binding.root)
        val mShow = mAlert.show()
        mAlert.create()
        binding.editCustomAlertSucess.addTextChangedListener {
            if (it.toString() != "") {
                mShow.dismiss()
            }
        }
        binding.txtMessageSucess.text = message
        binding.buttonSucessLayoutCustom.setOnClickListener {
            CustomMediaSonsMp3().somClick(this)
            mShow.dismiss()
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mProgress.dismiss()
        unregisterReceiver(receiver)
    }
}