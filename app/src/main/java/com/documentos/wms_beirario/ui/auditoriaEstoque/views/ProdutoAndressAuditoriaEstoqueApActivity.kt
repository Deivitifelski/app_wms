package com.documentos.wms_beirario.ui.auditoriaEstoque.views

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityProdutoAndressAuditoriaEstoqueApBinding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.DistribuicaoAp
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListEnderecosAuditoriaEstoque3Item
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasItem
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseProdutoEnderecoAuditoriaEstoqueAp
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseProdutoEnderecoAuditoriaEstoqueApCreate
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.ui.auditoriaEstoque.adapters.AdapterAuditoriaEstoqueAP
import com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels.AuditoriaEstoqueApontmentoViewModel3
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.toastError
import java.util.Observable
import java.util.Observer

class ProdutoAndressAuditoriaEstoqueApActivity : AppCompatActivity(), Observer {
    private lateinit var binding: ActivityProdutoAndressAuditoriaEstoqueApBinding
    private lateinit var adapterAP: AdapterAuditoriaEstoqueAP
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var alertDialog: CustomAlertDialogCustom
    private lateinit var viewModel: AuditoriaEstoqueApontmentoViewModel3
    private lateinit var dialogProgress: Dialog
    private var idArmazem: Int? = null
    private var token: String? = null
    private val TAG = "PRODUTO ESTOQUE"
    private var auditoria: ListaAuditoriasItem? = null
    private var estante: String? = null
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private var andress: ListEnderecosAuditoriaEstoque3Item? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProdutoAndressAuditoriaEstoqueApBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initConst()
        setRv()
        setToolbar()
        getIntentActivity()
        observer()
        initDataWedge()
        setupDataWedge()

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


    private fun initConst() {
        adapterAP = AdapterAuditoriaEstoqueAP()
        alertDialog = CustomAlertDialogCustom()
        dialogProgress = CustomAlertDialogCustom().progress(this, "Buscando auditorias...")
        dialogProgress.hide()
        sharedPreferences = CustomSharedPreferences(this)
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN)
        viewModel = ViewModelProvider(
            this, AuditoriaEstoqueApontmentoViewModel3.AuditoriaEstoqueApontmentoViewModelFactory3(
                AuditoriaEstoqueRepository()
            )
        )[AuditoriaEstoqueApontmentoViewModel3::class.java]
    }

    private fun setToolbar() {
        binding.toolbarAp.apply {
            setNavigationOnClickListener { onBackPressed() }
            title = "Auditoria de estoque/${andress?.enderecoVisual}"
            subtitle = "Apont.Produtos|" + getVersionNameToolbar()
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

    private fun observer() {
        viewModel.apply {
            emplyAuditoriasDb()
            notEmplyAuditoriasDb()
            errorDb()
            errorAll()
            validaProgress()
        }
    }


    private fun AuditoriaEstoqueApontmentoViewModel3.emplyAuditoriasDb() {
        sucessGetProdutosEmplyShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { emply ->
            toastError(
                this@ProdutoAndressAuditoriaEstoqueApActivity, "Sem itens a mostrar!"
            )
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModel3.notEmplyAuditoriasDb() {
        sucessGetProdutosShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { response ->
            if (response != null) {
                setDataTxt(response)
                try {
                    adapterAP.update(createArrayListQtdTam(response))
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

    private fun createArrayListQtdTam(response: List<ResponseProdutoEnderecoAuditoriaEstoqueAp>): List<ResponseProdutoEnderecoAuditoriaEstoqueApCreate> {
        val listCreate = mutableListOf<ResponseProdutoEnderecoAuditoriaEstoqueApCreate>()
        listCreate.clear()
        val listDist = mutableListOf<DistribuicaoAp>()
        response.forEach {
            listDist.clear()
            val lqtd = it.listaQuantidade?.split(",")
            val lTam = it.listaTamanho?.split(",")
            lTam?.forEachIndexed { index, tam ->
                listDist.add(
                    DistribuicaoAp(
                        listaTamanho = tam,
                        listaQuantidade = lqtd?.get(index) ?: ""
                    )
                )
            }
            listCreate.add(
                ResponseProdutoEnderecoAuditoriaEstoqueApCreate(
                    idEndereco = it.idEndereco,
                    codigoGrade = it.codigoGrade,
                    dataHoraUltimoApontamento = it.dataHoraUltimoApontamento,
                    idProduto = it.idProduto,
                    idAuditoriaEStoque = it.idAuditoriaEStoque,
                    quantidadeApontada = it.quantidadeApontada,
                    quantidadeApontamentosAtencao = it.quantidadeApontamentosAtencao,
                    quantidadeApontamentosErro = it.quantidadeApontamentosErro,
                    quantidadeAuditada = it.quantidadeAuditada,
                    numeroContagem = it.numeroContagem,
                    tipoProduto = it.tipoProduto,
                    skuProduto = it.skuProduto,
                    listDist = listDist
                )
            )
        }

        return listCreate
    }

    @SuppressLint("SetTextI18n")
    private fun setDataTxt(response: List<ResponseProdutoEnderecoAuditoriaEstoqueAp>) {
        var qtdApontPar: Long = 0
        var qtdAuditadaPar: Long = 0
        var qtdApontVol: Long = 0
        var qtdAuditadaVol: Long = 0
        response.forEach {
            if (it.tipoProduto == "PAR") {
                qtdApontPar += it.quantidadeApontada
                qtdAuditadaPar += it.quantidadeAuditada
            }
            if (it.tipoProduto == "VOLUME") {
                qtdApontVol += it.quantidadeApontada
                qtdAuditadaVol += it.quantidadeAuditada
            }
        }

        binding.txtQtdPares.text = "$qtdApontPar/$qtdAuditadaPar"
        binding.txtQtdVol.text = "$qtdApontVol/$qtdAuditadaVol"

    }

    private fun AuditoriaEstoqueApontmentoViewModel3.errorDb() {
        errorDbShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { error ->
            alertDialog.alertMessageErrorSimples(
                this@ProdutoAndressAuditoriaEstoqueApActivity, error
            )
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModel3.errorAll() {
        errorAllShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { error ->
            alertDialog.alertMessageErrorSimples(
                this@ProdutoAndressAuditoriaEstoqueApActivity, error
            )
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModel3.validaProgress() {
        progressShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { result ->
            binding.progress.isVisible = result
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}