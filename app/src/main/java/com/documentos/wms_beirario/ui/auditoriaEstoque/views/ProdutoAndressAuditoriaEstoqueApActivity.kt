package com.documentos.wms_beirario.ui.auditoriaEstoque.views

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityProdutoAndressAuditoriaEstoqueApBinding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.request.BodyApontEndProdutoAuditoriaEstoque
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListEnderecosAuditoriaEstoque3Item
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasItem
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseAuditoriaEstoqueAP
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.ui.auditoriaEstoque.adapters.AdapterAuditoriaEstoqueAP
import com.documentos.wms_beirario.ui.auditoriaEstoque.fragment.AuditoriaEstoqueDetalhesFragment
import com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels.AuditoriaEstoqueApontmentoViewModelAp
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersion
import com.documentos.wms_beirario.utils.extensions.hideKeyBoardFocus
import com.documentos.wms_beirario.utils.extensions.hideKeyExtensionActivity
import com.documentos.wms_beirario.utils.extensions.toastError
import com.documentos.wms_beirario.utils.extensions.toastSucess
import java.util.Observable
import java.util.Observer

class ProdutoAndressAuditoriaEstoqueApActivity : AppCompatActivity(), Observer {
    private lateinit var binding: ActivityProdutoAndressAuditoriaEstoqueApBinding
    private lateinit var adapterAP: AdapterAuditoriaEstoqueAP
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var alertDialog: CustomAlertDialogCustom
    private lateinit var sonsMp3: CustomMediaSonsMp3
    private lateinit var viewModel: AuditoriaEstoqueApontmentoViewModelAp
    private lateinit var dialogProgress: Dialog
    private var idArmazem: Int? = null
    private var token: String? = null
    private lateinit var codigoApontamento: String
    private var contagem: Int = 1
    private val TAG = "PRODUTO ESTOQUE"
    private var auditoria: ListaAuditoriasItem? = null
    private var estante: String? = null
    private var forcaApontamento: String = "N"
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private var loanding = false
    private var andress: ListEnderecosAuditoriaEstoque3Item? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProdutoAndressAuditoriaEstoqueApBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initConst()
        setRv()
        clickKey()
        getIntentActivity()
        setToolbar()
        observer()
        initDataWedge()
        setupDataWedge()
        clickButtonFinish()
        clickDeleteAuditoria()

    }


    private fun getIntentActivity() {
        if (intent != null) {
            auditoria = intent.getSerializableExtra("AUDITORIA_SELECT") as ListaAuditoriasItem?
            estante = intent.getStringExtra("ESTANTE")
            andress =
                intent.getSerializableExtra("ANDRESS_SELECT") as ListEnderecosAuditoriaEstoque3Item
            getData()
        } else {
            errorInitScreen()
        }
    }

    private fun errorInitScreen() {
        alertDialog.alertMessageErrorSimplesAction(this,
            "Ocorreu um erro ao receber dados, volte e tente novamente!",
            action = {
                finishAndRemoveTask()
            })
    }

    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun setupDataWedge() {
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
    }

    private fun clickDeleteAuditoria() {
        binding.buttonDeleteContagem.setOnClickListener {
            alertDialog.alertMessageAtencaoOptionAction(
                context = this,
                message = "Deseja limpar contagem ${contagem}?",
                actionNo = {},
                actionYes = {
                    viewModel.limparAuditoria(
                        idArmazem = idArmazem!!,
                        token = token!!,
                        idAuditoria = auditoria!!.id,
                        contagem = contagem,
                        idEndereco = andress!!.idEndereco
                    )
                }
            )
        }
    }

    private fun initConst() {
        binding.editEndereco.hideKeyBoardFocus()
        hideKeyExtensionActivity(binding.editEndereco)
        adapterAP = AdapterAuditoriaEstoqueAP { detalhes ->
            AuditoriaEstoqueDetalhesFragment(
                detalhes,
                token!!,
                auditoria!!,
                idArmazem!!,
                andress!!.idEndereco
            ).show(supportFragmentManager, "DETALHES")
        }
        alertDialog = CustomAlertDialogCustom()
        sonsMp3 = CustomMediaSonsMp3()
        dialogProgress = CustomAlertDialogCustom().progress(this, "Buscando auditorias...")
        dialogProgress.hide()
        sharedPreferences = CustomSharedPreferences(this)
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN)
        viewModel = ViewModelProvider(
            this, AuditoriaEstoqueApontmentoViewModelAp.AuditoriaEstoqueApontmentoViewModelFactory3(
                AuditoriaEstoqueRepository()
            )
        )[AuditoriaEstoqueApontmentoViewModelAp::class.java]
    }

    private fun setToolbar() {
        binding.toolbarAp.apply {
            setNavigationOnClickListener {
                finishAndRemoveTask()
                extensionBackActivityanimation(this@ProdutoAndressAuditoriaEstoqueApActivity)
            }
            title = "Auditoria de estoque"
            subtitle = "Contagem: $contagem | ${andress?.enderecoVisual} | " + getVersion()
        }
    }

    private fun setRv() {
        binding.rvApontamentoAp.apply {
            layoutManager = LinearLayoutManager(this@ProdutoAndressAuditoriaEstoqueApActivity)
            adapter = adapterAP
        }
    }

    private fun getData() {
        viewModel.getProdutoAndressAP(
            endereco = andress!!,
            idAuditoria = auditoria!!.id,
            token = token!!,
            idArmazem = idArmazem!!
        )
    }

    private fun clickButtonFinish() {
        binding.buttonFinishAuditoria.setOnClickListener {
            alertDialog.alertMessageAtencaoOptionAction(
                context = this,
                message = "Deseja validar contagem: ${contagem} ?",
                actionNo = {},
                actionYes = {
                    viewModel.validaContagem(
                        idAuditoria = auditoria!!.id,
                        token = token!!,
                        idArmazem = idArmazem!!,
                        idEndereco = andress!!.idEndereco,
                        contagem = contagem
                    )
                }
            )
        }
    }

    private fun observer() {
        viewModel.apply {
            emplyAuditoriasDb()
            notEmplyAuditoriasDb()
            errorDb()
            errorAll()
            validaProgress()
            responseApontAp()
            errorApontAp()
            validaContagemDb()
            sucessDeleteAuditoria()
            sucessLimpaContagem()
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelAp.validaContagemDb() {
        sucessValidaContagemShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { res ->
            if (res.erro == "true") {
                alertDialog.alertMessageAtencaoOptionAction(
                    context = this@ProdutoAndressAuditoriaEstoqueApActivity,
                    res.mensagemErro,
                    actionNo = {},
                    actionYes = {
                        adapterAP.clear()
                        contagem += 1
                        binding.txtInfo.visibility = View.VISIBLE
                        setToolbar()
                        clearInputAllCount()
                    }
                )
            } else {
                alertDialog.alertMessageSucessAction(
                    context = this@ProdutoAndressAuditoriaEstoqueApActivity,
                    message = res.mensagemErro,
                    action = {
                        finishAndRemoveTask()
                        extensionBackActivityanimation(this@ProdutoAndressAuditoriaEstoqueApActivity)
                    }
                )
            }
        }
    }

    private fun clearInputAllCount() {
        binding.apply {
            txtQtdPares.text = "-"
            txtQtdVol.text = "-"
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelAp.emplyAuditoriasDb() {
        sucessGetProdutosEmplyShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { emply ->
            binding.txtInfo.text = "Sem produtos para auditoria"
            binding.txtInfo.visibility = View.VISIBLE
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelAp.sucessLimpaContagem() {
        sucessDeleteAuditoriaShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { response ->
            sonsMp3.somSucess(this@ProdutoAndressAuditoriaEstoqueApActivity)
            toastSucess(
                this@ProdutoAndressAuditoriaEstoqueApActivity,
                "Contagem $contagem limpa com sucesso!"
            )
            getData()
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelAp.sucessDeleteAuditoria() {
        sucessGetProdutosEmplyShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { resp ->
            sonsMp3.somSucess(this@ProdutoAndressAuditoriaEstoqueApActivity)
            toastSucess(
                this@ProdutoAndressAuditoriaEstoqueApActivity,
                "Contagem: $contagem auditoria deletada com sucesso!"
            )
            adapterAP.clear()
            getData()
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelAp.notEmplyAuditoriasDb() {
        sucessGetProdutosShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { response ->
            if (response != null) {
                try {
                    binding.txtInfo.visibility = View.GONE
                    setDataTxt(response)
                    adapterAP.update(response)
                } catch (e: Exception) {
                    Log.e(TAG, "$e")
                    toastError(
                        this@ProdutoAndressAuditoriaEstoqueApActivity,
                        "Erro ao receber dados!"
                    )
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setDataTxt(response: List<ResponseAuditoriaEstoqueAP>?) {
        var qtdApontPar: Long = 0
        var qtdAuditadaPar: Long = 0
        var qtdApontVol: Long = 0
        var qtdAuditadaVol: Long = 0
        response?.forEach {
            if (it.tipoProduto == "PAR") {
                qtdApontPar += it.quantidadeApontada!!
                qtdAuditadaPar += it.quantidadeAuditada
            }
            if (it.tipoProduto == "VOLUME") {
                qtdApontVol += it.quantidadeApontada!!
                qtdAuditadaVol += it.quantidadeAuditada
            }
        }
        binding.txtQtdPares.text = "$qtdApontPar/$qtdAuditadaPar"
        binding.txtQtdVol.text = "$qtdApontVol/$qtdAuditadaVol"
    }

    private fun AuditoriaEstoqueApontmentoViewModelAp.errorDb() {
        errorDbShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { error ->
            alertDialog.alertMessageErrorSimples(
                this@ProdutoAndressAuditoriaEstoqueApActivity, error
            )
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelAp.errorAll() {
        errorAllShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { error ->
            forcaApontamento = "N"
            alertDialog.alertMessageErrorSimples(
                this@ProdutoAndressAuditoriaEstoqueApActivity, error
            )
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelAp.validaProgress() {
        progressShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { result ->
            binding.progress.isVisible = result
            loanding = result
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelAp.errorApontAp() {
        errorDbApontShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { error ->
            forcaApontamento = "N"
            alertDialog.alertMessageErrorSimplesAction(
                this@ProdutoAndressAuditoriaEstoqueApActivity, error, action = {}
            )
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelAp.responseApontAp() {
        sucessAPontEndProdShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { res ->
            if (res.erro == "true") {
                binding.txtInfo.visibility = View.GONE
                alertDialog.alertMessageAtencaoOptionAction(
                    context = this@ProdutoAndressAuditoriaEstoqueApActivity,
                    res.mensagemErro,
                    actionNo = {},
                    actionYes = {
                        forcaApontamento = "S"
                        readingAndress(scan = codigoApontamento)
                    }
                )
            } else {
                forcaApontamento = "N"
                sonsMp3.somSucess(this@ProdutoAndressAuditoriaEstoqueApActivity)
                adapterAP.clear()
                getData()
            }
        }
    }


    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            readingAndress(scanData.toString().trim())
        }
    }

    private fun clickKey() {
        binding.imageView3.setOnClickListener {
            alertDialog.alertEditText(
                context = this,
                title = "Auditoria de estoque",
                subTitle = "Digite um produto que deseja apontar",
                actionYes = { readingAndress(it) },
                actionNo = { hideKeyExtensionActivity(binding.editEndereco) }
            )
        }
    }


    private fun readingAndress(scan: String) {
        if (!loanding) {
            codigoApontamento = scan
            val body = BodyApontEndProdutoAuditoriaEstoque(
                codigoBarras = scan,
                forcarApontamento = forcaApontamento
            )
            viewModel.apontaProdutoAP(
                token = token!!,
                idArmazem = idArmazem!!,
                body = body,
                contagem = contagem.toString(),
                idAuditoriaEstoque = auditoria!!.id,
                idEndereco = andress!!.idEndereco.toString()
            )
        } else {
            toastError(this, "Aguarde a resposta do servidor!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}