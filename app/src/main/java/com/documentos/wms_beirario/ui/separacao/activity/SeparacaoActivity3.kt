package com.documentos.wms_beirario.ui.separacao.activity

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityEndSeparationBinding
import com.documentos.wms_beirario.model.separation.RequestSeparationArraysAndaresEstante3
import com.documentos.wms_beirario.model.separation.SeparationEnd
import com.documentos.wms_beirario.model.separation.filtros.BodyEnderecosFiltro
import com.documentos.wms_beirario.model.separation.filtros.ItemDocTrans
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.ui.separacao.adapter.AdapterSeparationEnd2
import com.documentos.wms_beirario.ui.separacao.viewModel.SeparationViewModel3
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarras
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.toastError
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.Observable
import java.util.Observer

class SeparacaoActivity3 : AppCompatActivity(), Observer {

    private lateinit var mAdapter: AdapterSeparationEnd2
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mViewModel: SeparationViewModel3
    private lateinit var mSons: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mBinding: ActivityEndSeparationBinding
    private lateinit var mIntentData: RequestSeparationArraysAndaresEstante3
    private var idArmazem: Int? = null
    private lateinit var token: String
    private var listDoc: ItemDocTrans? = null
    private var listTrans: ItemDocTrans? = null
    private lateinit var sharedPreferences: CustomSharedPreferences
    private var mQntSeparada: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityEndSeparationBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initIntent()
        setToolbar()
        initViewModel()
        showresultEnd()
        showresultListCheck()
        setupDataWedge()
        initScanEditText()
    }

    override fun onStart() {
        super.onStart()
        mBinding.progressEdit.isVisible = false
        callApi()
        initRecyclerView()
        UIUtil.hideKeyboard(this)
    }


    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this, SeparationViewModel3.ViewModelEndSeparationFactory(
                SeparacaoRepository()
            )
        )[SeparationViewModel3::class.java]
    }


    private fun initIntent() {
        try {
            sharedPreferences = CustomSharedPreferences(this)
            token = sharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
            this.idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
            mBinding.editSeparacao2.requestFocus()
            val extras = intent
            if (extras != null) {
                val data =
                    extras.getSerializableExtra("ARRAYS_AND_EST") as RequestSeparationArraysAndaresEstante3
                listDoc = extras.getSerializableExtra("DOC") as ItemDocTrans
                listTrans = extras.getSerializableExtra("TRANS") as ItemDocTrans
                mIntentData = data
                Log.e(
                    "Recebendo tela 2",
                    "DOCUMENTOS:${listDoc!!.items}\nTRANSPORTADORA:${listTrans!!.items}"
                )
            }
        } catch (e: Exception) {
            vibrateExtension(500)
            mToast.snackBarErrorSimples(mBinding.root, e.toString())
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


    private fun setToolbar() {
        mSons = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
        mBinding.toolbarSeparacao2.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = getVersionNameToolbar()
        }
    }

    private fun initRecyclerView() {
        mAdapter = AdapterSeparationEnd2()
        mBinding.rvSeparacaoEnd.layoutManager = LinearLayoutManager(this)
        mBinding.rvSeparacaoEnd.adapter = mAdapter
    }

    private fun callApi() {
        val doc = listDoc?.items ?: listOf(null)
        val trans = listTrans?.items ?: listOf(null)
        val body = BodyEnderecosFiltro(
            listatiposdocumentos = doc.ifEmpty { listOf(null) },
            listatransportadoras = trans.ifEmpty { listOf(null) },
            listaandares = mIntentData.andares,
            listaestantes = mIntentData.estantes
        )
        mViewModel.postArrayAndaresEstantes(body, idArmazem, token)
    }

    private fun initScanEditText() {
        mBinding.editSeparacao2.extensionSetOnEnterExtensionCodBarras {
            sendReading(mBinding.editSeparacao2.text.toString())
            clearEdit()
        }
    }

    /*
     * VERIFICA COMO DEVE SER TRATADA A SEPARAÇÃO:
     * CASO 100 -> SEGUE O FLUXO NA MESMA TELA
     * CASO CONTRÁRIO -> ABRE NOVA TELA QUE AO SEPARAR GERAR UM ETIQUETA
     */
    private fun sendReading(mQrcode: String) {
        try {
            mBinding.progressEdit.isVisible = true
            if (mQrcode != "") {
                val qrcodeRead = mAdapter.searchSeparation(mQrcode)
                if (qrcodeRead == null) {
                    mBinding.progressEdit.isVisible = false
                    mAlert.alertMessageErrorSimples(
                        this,
                        "Endereço inválido"
                    )
                } else {
                    when (this.idArmazem) {
                        100 -> {
                            mQntSeparada = qrcodeRead.quantidadeProdutos
                            mViewModel.postSeparationEnd(
                                SeparationEnd(
                                    quantidade = qrcodeRead.quantidadeProdutos,
                                    codBarrasEndOrigem = qrcodeRead.codBarrasEndOrigem,
                                ),
                                idArmazem!!,
                                token
                            )
                        }
                        else -> {
                            val doc = listDoc?.items ?: listOf(null)
                            val trans = listTrans?.items ?: listOf(null)
                            val intent = Intent(this, SeparacaoActivityBeta4::class.java)
                            intent.putExtra("DADOS_BIPAGEM", qrcodeRead)
                            intent.putExtra("DOC", ItemDocTrans(doc))
                            intent.putExtra("TRANS", ItemDocTrans(trans))
                            startActivity(intent)
                            extensionSendActivityanimation()

                        }
                    }
                }
            }
        } catch (e: Exception) {
            toastError(this, "Erro inesperado!\n$e")
        } finally {
            clearEdit()
        }
    }

    private fun clearEdit() {
        mBinding.editSeparacao2.setText("")
        mBinding.editSeparacao2.text!!.clear()
        mBinding.editSeparacao2.requestFocus()
    }


    private fun showresultListCheck() {
        /**MOSTRANDO TAREFAS DE ANDARES E ESTANTES SELECIONADO ANTERIORMENTE -->*/
        mViewModel.mShowShow2.observe(this) { responseList ->
            clearEdit()
            if (responseList.isEmpty()) {
                /**ALERTA DE TODOS OS VOLUMES SEPARADOS COM SUCESSO! -->*/
                mAlert.alertMessageSucessAction(
                    this,
                    "Todos volumes lidos\nvoltar a tela anterior!",
                    action = {
                        val list = mutableListOf<String>()
                        responseList?.forEach {
                            list.add(it.enderecoVisual)
                        }
                        //ESTANTE_ENDERECO_ORIGEM
                        list.clear()
                        mIntentData =
                            RequestSeparationArraysAndaresEstante3(mIntentData.andares, list)
                        onBackPressed()
                    })
            } else {
                responseList.forEach { arm ->
                }
                mAdapter.update(responseList)
            }
        }

        mViewModel.mErrorShow2.observe(this) { responseError ->
            clearEdit()
            mAlert.alertMessageErrorSimples(this, responseError)
        }

        mViewModel.mProgressShow.observe(this) { showProgress ->
            mBinding.progressEdit.isVisible = showProgress
        }

        mViewModel.mProgressInitShow.observe(this) { progressInit ->
            mBinding.progressSeparationInit.isVisible = progressInit
        }
    }

    /**LENDO EDIT TEXT PARA SEPARAR FINISH COM ARMAZEM 100 ------------------------------------------------------------->*/
    private fun showresultEnd() {
        mViewModel.mSeparationEndShow.observe(this) {
            mAlert.alertMessageSucessAction(
                this,
                "${mQntSeparada.toString()} volumes separados com sucesso!",
                action = {
                    callApi()
                    initRecyclerView()
                    clearEdit()
                })
        }

        mViewModel.mErrorSeparationEndShow.observe(this) { responseErrorEnd ->
            mAlert.alertMessageErrorSimples(this, responseErrorEnd)
        }

        mViewModel.mProgressShow.observe(this) { progress ->
            mBinding.progressEdit.isVisible = progress
        }
    }

    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            Log.e("SEPARAÇAO FINAL", "DADOS RECEBIDOS LEITURA DA SEPARAÇAO FINAL --> $scanData")
            sendReading(scanData.toString())
            clearEdit()
        }
    }

    /**RETORNA A TELA ANTERIOR AS ESTANTES SELECIONADAS -->*/
    private fun returSeparation2() {
        val intent = Intent()
        val doc = listDoc?.items ?: listOf(null)
        val trans = listTrans?.items ?: listOf(null)
        intent.putExtra("ARRAY_BACK", mIntentData)
        intent.putExtra("DOC", ItemDocTrans(doc))
        intent.putExtra("TRANS", ItemDocTrans(trans))
        Log.e("Voltando tela 2", "DOCUMENTOS:${listDoc?.items}\nTRANSPORTADORA:${listTrans?.items}")
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        returSeparation2()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}

