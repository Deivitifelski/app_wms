package com.documentos.wms_beirario.ui.separacao.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.ActivitySeparaco3Binding
import com.documentos.wms_beirario.model.separation.BodySepararEtiquetar
import com.documentos.wms_beirario.model.separation.ResponseEstantesAndaresSeparation3Item
import com.documentos.wms_beirario.model.separation.ResponseEtiquetarSeparar
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.ui.bluetooh.BluetoohPrinterActivity
import com.documentos.wms_beirario.ui.separacao.adapter.AdapterSeparation3
import com.documentos.wms_beirario.ui.separacao.viewModel.SeparationViewModel4
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.*
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SeparacaoActivityBeta4 : AppCompatActivity(), Observer {

    private val TAG = "SEPARATION 4"
    private lateinit var mBinding: ActivitySeparaco3Binding
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mProgress: Dialog
    private lateinit var mIntent: ResponseEstantesAndaresSeparation3Item
    private lateinit var mViewModel: SeparationViewModel4
    private lateinit var mADapterSeparation3: AdapterSeparation3
    private var service: BluetoothService? = null
    private lateinit var writer: BluetoothWriter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySeparaco3Binding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setupEdit()
        initConst()
        initIntent()
        setupRv()
        getInitScreen()
        setToolbar()
        setupObservables()
        mBinding.editSeparation3.requestFocus()
        setupDataWedge()
        verificationsBluetooh()
    }

    override fun onResume() {
        super.onResume()
        if (BluetoohPrinterActivity.STATUS == "CONNECTED") {
            initConfigPrinter()
        }
        clearText()
        hideKeyExtensionActivity(mBinding.editSeparation3)
        mProgress.hide()
    }

    private fun setToolbar() {
        mBinding.toolbarSeparacao3.apply {
            title = "${ServiceApi.IDARMAZEM} | ${mIntent.enderecoVisual}"
            subtitle = getVersionNameToolbar()
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun initConfigPrinter() {
        service = BluetoothClassicService.getDefaultInstance()
        writer = BluetoothWriter(service)
        Log.e(TAG, "INICIANDO PRINTER -> WRITE")
    }

    /**VERIFICA SE JA TEM IMPRESSORA CONECTADA!!--->*/
    private fun verificationsBluetooh() {
        if (BluetoohPrinterActivity.STATUS != "CONNECTED") {
            mAlert.alertSelectPrinter(this, activity = this)
        } else {
            initConfigPrinter()
        }
    }

    private fun getInitScreen() {
        mViewModel.postBuscaProdutos(
            mIntent.enderecoVisual
        )
    }

    private fun initConst() {
        mViewModel = ViewModelProvider(
            this, SeparationViewModel4.ViewModelSeparationFactory3(
                SeparacaoRepository()
            )
        )[SeparationViewModel4::class.java]
        mProgress = CustomAlertDialogCustom().progress(this)
        mProgress.hide()
        mAlert = CustomAlertDialogCustom()
    }

    private fun setupRv() {
        mADapterSeparation3 = AdapterSeparation3()
        mBinding.rvSeparationProdAndress.apply {
            layoutManager = LinearLayoutManager(this@SeparacaoActivityBeta4)
            adapter = mADapterSeparation3
        }
    }

    private fun initIntent() {
        try {
            if (intent.extras != null) {
                mIntent =
                    intent.getSerializableExtra("DADOS_BIPAGEM") as ResponseEstantesAndaresSeparation3Item
                Log.e(TAG, "Dados recebidos intent de SEPARATION 2: $mIntent")
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
        /**SUCESSO NO GET AO ENTRAR N TELA (PRODUTOS)-->*/
        mViewModel.mSucessoGetProdutosShow.observe(this) { sucess ->
            if (sucess.isEmpty()) {
                mAlert.alertMessageSucessAction(
                    this,
                    message = "Itens Separados com sucesso",
                    action = { onBackPressed() })
            } else {
                mADapterSeparation3.update(sucess)
            }
        }
        /**SUCESSO DO POST ETIQUETAGEM E SEPARAÇÃO -->*/
        mViewModel.mSucessPostSepEtiShow.observe(this) { resEtiquetarSeparar ->
            try {
                mProgress.hide()
                getInitScreen()
                setupRv()
                sendPrinter(resEtiquetarSeparar)
            } catch (e: Exception) {
                mErroToastExtension(this, "Erro ao tentar finalizar!")
            } finally {
                clearText()
            }
        }
        /**ERRO IMPRIMIR E ETIQUETAR -->*/
        mViewModel.mErrorSepEtiShow.observe(this) { errorAll ->
            mAlert.alertMessageErrorSimplesAction(this, errorAll, action = { clearText() })
        }

        mViewModel.mErrorShow.observe(this) { error ->
            mAlert.alertMessageErrorSimples(this, error)
        }
        mViewModel.mValidationProgressShow.observe(this) { progress ->
            if (progress) mProgress.show() else mProgress.hide()
        }
    }

    private fun sendPrinter(resEtiquetarSeparar: ResponseEtiquetarSeparar?) {
        try {
            if (service != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    resEtiquetarSeparar.let { data ->
                        data?.forEach { item ->
                            writer.write(item.codigoZpl)
                        }
                    }
                }
                Toast.makeText(this, "imprimindo...", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            mErroToastExtension(this, "Erro ao tentar imprimir\n$e")
        }
    }

    private fun sendData(scanData: String) {
        try {
            if (BluetoohPrinterActivity.STATUS != "CONNECTED") {
                vibrateExtension(500)
                mAlert.alertSelectPrinter(this, getString(R.string.printer_of_etiquetagem_modal))
                clearText()
            } else if (scanData.isEmpty()) {
                mBinding.editLayoutSeparation3.shake {
                    mErroToastExtension(this, "Preencha o campo!")
                }
            } else {
                val body = BodySepararEtiquetar(numeroSerie = scanData)
                mViewModel.postAndressEtiquetarSeparar(
                    body = body,
                    idEnderecoOrigem = mIntent.idEndereco.toString()
                )
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