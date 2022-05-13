package com.documentos.wms_beirario.ui.etiquetagem.activitys

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityEtiquetagem1Binding
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequest1
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.ui.etiquetagem.viewmodel.EtiquetagemFragment1ViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import java.util.*


class EtiquetagemActivity1 : AppCompatActivity(), Observer {
    private lateinit var mBinding: ActivityEtiquetagem1Binding
    private lateinit var mViewModel: EtiquetagemFragment1ViewModel
    private lateinit var mAlert: CustomAlertDialogCustom
    private val TAG = "EtiquetagemActivity1"
    private lateinit var mPrintConnect: PrinterConnection
    private lateinit var mToast: CustomSnackBarCustom
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false


    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityEtiquetagem1Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        initViewModel()
        setObservable()
        verificationsBluetooh()
        setToolbar()
        setupEdit()
        clickButton()
        setupDataWedge()
    }

    override fun onResume() {
        super.onResume()
        AppExtensions.visibilityProgressBar(mBinding.progressBarEditEtiquetagem1, false)
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }

    }


    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            EtiquetagemFragment1ViewModel.Etiquetagem1ViewModelFactory(EtiquetagemRepository())
        )[EtiquetagemFragment1ViewModel::class.java]
    }

    /**VERIFICA SE JA TEM IMPRESSORA CONECTADA!!--->*/
    private fun verificationsBluetooh() {
        mAlert = CustomAlertDialogCustom()
        if (SetupNamePrinter.applicationPrinterAddress.isEmpty()) {
            mAlert.alertSelectPrinter(this)
        }
    }

    private fun setToolbar() {
        mToast = CustomSnackBarCustom()
        mPrintConnect = PrinterConnection(SetupNamePrinter.applicationPrinterAddress)
        mBinding.toolbar.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    /**ENVIANDO PARA OUTRO FRAGMENT -->*/
    private fun clickButton() {
        mBinding.buttonPendencePorNf.setOnClickListener {
            startActivity(Intent(this, EtiquetagemPendenciaNFActivity::class.java))
            extensionSendActivityanimation()
        }
        mBinding.buttonPendencePorPendency.setOnClickListener {
            startActivity(Intent(this, EtiquetagemPedidoActivity::class.java))
            extensionSendActivityanimation()
        }
    }

    private fun setObservable() {
        mViewModel.mSucessShow.observe(this, { zpl ->
            try {
                clearEdit()
                if (SetupNamePrinter.applicationPrinterAddress.isNullOrEmpty()) {
                    mAlert.alertSelectPrinter(this)
                } else {
                    var mListZpl: MutableList<String> = mutableListOf()
                    zpl.forEach {
                        mListZpl.add(it.codigoZpl)
                    }
                    mPrintConnect.printZebraLoop(mListZpl)
                    Toast.makeText(this, getString(R.string.printing), Toast.LENGTH_SHORT).show()
                }
            }catch (e:Exception){
                mErrorToast("Erro ao tentar imprimir:\n$e")
            }
        })

        mViewModel.mErrorShow.observe(this) { messageError ->
            clearEdit()
            mAlert.alertMessageAtencao(this, messageError,2000)
        }
        mViewModel.mErrorAllShow.observe(this, { errorAll ->
            clearEdit()
            mAlert.alertMessageErrorSimples(this, errorAll, 2000)
        })

        mViewModel.mProgressShow.observe(this, { progress ->
            mBinding.progressBarEditEtiquetagem1.isVisible = progress
        })
    }

    private fun setupEdit() {
        mBinding.editEtiquetagem.requestFocus()
        mBinding.editEtiquetagem.extensionSetOnEnterExtensionCodBarras {
            sendData(mBinding.editEtiquetagem.text.toString())
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
            Log.e(TAG, "onNewIntent -> $scanData")
            sendData(scanData.toString())
            clearEdit()
        }
    }

    private fun sendData(scan: String) {
        try {
            if (scan.isNotEmpty()) {
                mViewModel.etiquetagemPost(etiquetagemRequest1 = EtiquetagemRequest1(scan))
                clearEdit()
            }
        } catch (e: Exception) {
            mErrorToast(e.toString())
        }
    }

    private fun mErrorToast(toString: String) {
        vibrateExtension(500)
        mToast.toastCustomError(this, toString)
    }

    private fun clearEdit() {
        mBinding.editEtiquetagem.setText("")
        mBinding.editEtiquetagem.text?.clear()
        mBinding.editEtiquetagem.requestFocus()
    }

    override fun onBackPressed() {
        finish()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

}