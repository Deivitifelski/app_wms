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
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.ActivitySeparaco3Binding
import com.documentos.wms_beirario.model.separation.BodySepararEtiquetar
import com.documentos.wms_beirario.model.separation.ResponseEstantesAndaresSeparation3Item
import com.documentos.wms_beirario.model.separation.ResponseEtiquetarSeparar
import com.documentos.wms_beirario.model.separation.filtros.BodyProdutoSeparacao
import com.documentos.wms_beirario.model.separation.filtros.ItemDocTrans
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.ui.bluetooh.BluetoohPrinterActivity
import com.documentos.wms_beirario.ui.separacao.adapter.AdapterSeparation3
import com.documentos.wms_beirario.ui.separacao.viewModel.SeparationViewModel4
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.clickHideShowKey
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarras
import com.documentos.wms_beirario.utils.extensions.hideKeyExtensionActivity
import com.documentos.wms_beirario.utils.extensions.returnNameVersionDb
import com.documentos.wms_beirario.utils.extensions.shake
import com.documentos.wms_beirario.utils.extensions.toastError
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Observable
import java.util.Observer

class SeparacaoActivityBeta4 : AppCompatActivity(), Observer {

    private val TAG = "SEPARATION 4"
    private lateinit var mBinding: ActivitySeparaco3Binding
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var toast: CustomSnackBarCustom
    private lateinit var sonsMp3: CustomMediaSonsMp3
    private lateinit var progress: Dialog
    private var validaLeitura: Boolean = true
    private lateinit var mIntent: ResponseEstantesAndaresSeparation3Item
    private lateinit var mViewModel: SeparationViewModel4
    private lateinit var mADapterSeparation3: AdapterSeparation3
    private var service: BluetoothService? = null
    private lateinit var writer: BluetoothWriter
    private lateinit var token: String
    private var idArmazem: Int = 0
    private lateinit var sharedPreferences: CustomSharedPreferences
    private var listDoc: ItemDocTrans? = null
    private var listTrans: ItemDocTrans? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySeparaco3Binding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setupEdit()
        initConst()
        initIntent()
        setupRv()
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
        progress.hide()
    }

    private fun setToolbar() {
        mBinding.toolbarSeparacao3.apply {
            title = "${ServiceApi.IDARMAZEM} | ${mIntent.enderecoVisual}"
            subtitle = returnNameVersionDb()
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

    private fun callApi() {
        val doc = listDoc?.items ?: listOf(null)
        val trans = listTrans?.items ?: listOf(null)
        val body = BodyProdutoSeparacao(
            codbarrasendereco = mIntent.codBarrasEndOrigem,
            listatiposdocumentos = doc.ifEmpty { listOf(null) },
            listatransportadoras = trans.ifEmpty { listOf(null) },
        )
        mViewModel.postBuscaProdutos(
            body,
            idArmazem,
            token
        )
    }

    private fun initConst() {
        mViewModel = ViewModelProvider(
            this, SeparationViewModel4.ViewModelSeparationFactory3(
                SeparacaoRepository()
            )
        )[SeparationViewModel4::class.java]
        sharedPreferences = CustomSharedPreferences(this)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        progress = CustomAlertDialogCustom().progress(this)
        progress.hide()
        mAlert = CustomAlertDialogCustom()
        toast = CustomSnackBarCustom()
        sonsMp3 = CustomMediaSonsMp3()
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
                listDoc = intent.getSerializableExtra("DOC") as ItemDocTrans
                listTrans = intent.getSerializableExtra("TRANS") as ItemDocTrans
                callApi()
                Log.e(TAG, "Dados recebidos intent de SEPARATION 2: $mIntent")
            }
        } catch (e: Exception) {
            toastError(this, "Erro ao receber dados!")
        }
    }

    private fun setupEdit() {
        mBinding.editSeparation3.extensionSetOnEnterExtensionCodBarras {
            sendData(mBinding.editSeparation3.text.toString())
        }
    }

    private fun clearText() {
        progress.hide()
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
            progress.hide()
            validaLeitura = true
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
                validaLeitura = true
                progress.hide()
                callApi()
                setupRv()
                sendPrinter(resEtiquetarSeparar)
            } catch (e: Exception) {
                toastError(this, "Erro ao tentar finalizar!")
            } finally {
                clearText()
            }
        }
        /**ERRO IMPRIMIR E ETIQUETAR -->*/
        mViewModel.mErrorSepEtiShow.observe(this) { errorAll ->
            validaLeitura = true
            progress.hide()
            mAlert.alertMessageErrorSimplesAction(this, errorAll, action = { clearText() })
        }

        mViewModel.mErrorShow.observe(this) { error ->
            validaLeitura = true
            progress.hide()
            mAlert.alertMessageErrorSimples(this, error)
        }

        mViewModel.mErrorSeparationSShowAll.observe(this) { error ->
            validaLeitura = true
            progress.hide()
            mAlert.alertMessageErrorSimplesAction(this, error, action = { clearText() })
        }

        mViewModel.mValidationProgressShow.observe(this) { progress ->
            if (progress) this.progress.show() else this.progress.hide()
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
            toastError(this, "Erro ao tentar imprimir\n$e")
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
                    toastError(this, "Preencha o campo!")
                }
            } else {
                validaLeitura = false
                val body = BodySepararEtiquetar(numeroSerie = scanData)
                mViewModel.postAndressEtiquetarSeparar(
                    body = body,
                    idEnderecoOrigem = mIntent.idEndereco,
                    idArmazem,
                    token
                )
                clearText()
            }
        } catch (e: Exception) {
            toastError(this, "${e.message}")
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
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            //(validaLeitura == false) impede o usuário de fazer uma nova requisição.
            if (!validaLeitura) {
                Log.e(TAG, "ERRO")
                setErrorScan()
            } else {
                Log.e(TAG, "$scanData")
                sendData(scanData = scanData!!)
                clearText()
            }

        }
    }

    private fun setErrorScan() {
        vibrateExtension(500)
        sonsMp3.somAlerta(this)
        toast.toastCustomError(this, "Aguarde resposta do servidor.")
        clearText()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        progress.dismiss()
        unregisterReceiver(receiver)
    }
}