package com.documentos.wms_beirario.ui.separacao.activity

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivitySeparaco3Binding
import com.documentos.wms_beirario.model.separation.ResponseListCheckBoxItem
import com.documentos.wms_beirario.model.separation.bodySeparation3
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.ui.separacao.SeparationViewModel3
import com.documentos.wms_beirario.ui.separacao.adapter.AdapterSeparation3
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarras
import com.documentos.wms_beirario.utils.extensions.mErroToastExtension
import io.reactivex.Observer
import java.util.*

class SeparacaoActivity3 : AppCompatActivity(), java.util.Observer {

    private val TAG = "SEPARATION 3"
    private lateinit var mBinding: ActivitySeparaco3Binding
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mProgress: Dialog
    private lateinit var mIntent: ResponseListCheckBoxItem
    private lateinit var mViewModel: SeparationViewModel3
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
    }

    override fun onResume() {
        super.onResume()
        setupDataWedge()
        clearText()
    }

    private fun setToolbar() {
        mBinding.toolbarSeparacao3.apply {
            title = mIntent.enderecoVisualOrigem
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun initConst() {
        mViewModel = ViewModelProvider(
            this, SeparationViewModel3.ViewModelSeparationFactory3(
                SeparacaoRepository()
            )
        )[SeparationViewModel3::class.java]
        mADapterSeparation3 = AdapterSeparation3()
        mBinding.rvSeparationProdAndress.apply {
            layoutManager = LinearLayoutManager(this@SeparacaoActivity3)
            adapter = mADapterSeparation3
        }
        mProgress = CustomAlertDialogCustom().progress(this)
        mProgress.hide()
        mAlert = CustomAlertDialogCustom()
    }

    private fun initIntent() {
        try {
            if (intent.extras != null) {
                mIntent = intent.getSerializableExtra("DADOS_BIPAGEM") as ResponseListCheckBoxItem
                Log.e(TAG, "Dados recebidos intent de SEPARATION 2: $mIntent")
                mViewModel.getProdAndress(
                    mIntent.estanteEnderecoOrigem,
                    mIntent.idEnderecoOrigem.toString()
                )
            }
        } catch (e: Exception) {
            mErroToastExtension(this, "Erro ao receber dados!")
        }
    }

    private fun setupEdit() {
        mBinding.editSeparation3.extensionSetOnEnterExtensionCodBarras {
            sendData(mBinding.editSeparation3.text.toString())
            clearText()
        }
    }

    private fun clearText() {
        mProgress.hide()
        mBinding.editSeparation3.text?.clear()
        mBinding.editSeparation3.setText("")
        mBinding.editSeparation3.requestFocus()
    }

    private fun setupObservables() {
        /**SUCESSO NO GET AO ENTRAR N TELA -->*/
        mViewModel.mSucessGetShow.observe(this) { sucess ->
            mADapterSeparation3.update(sucess)
        }
        /**SUCESSO DO POST -->*/
        mViewModel.mSucessPostShow.observe(this) { layoutEtiqueta ->
            try {
                val listLayout = mutableListOf<String>()
                layoutEtiqueta.forEach {
                    listLayout.add(it.toString())
                }
                mPrinter = PrinterConnection(SetupNamePrinter.mNamePrinterString)
                mPrinter.sendZplOverBluetooth(
                    null,
                    listLayout,
                )
            } catch (e: Exception) {
                mErroToastExtension(this, "Erro ao tentar imprimir!")
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
        if (scanData.isNullOrEmpty()) {
            mErroToastExtension(this, getString(R.string.edit_emply))
        } else {
            val body = bodySeparation3(scanData, mIntent.idEnderecoOrigem)
            mViewModel.postAndress(bodySeparation3 = body)
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
            Log.e("SEPARAÇAO 3", "Dados recebbidos via intent --> $scanData")
            sendData(scanData = scanData!!)
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