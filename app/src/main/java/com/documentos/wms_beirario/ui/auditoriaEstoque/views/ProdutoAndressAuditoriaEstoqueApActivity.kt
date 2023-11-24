package com.documentos.wms_beirario.ui.auditoriaEstoque.views

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityProdutoAndressAuditoriaEstoqueApBinding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListEnderecosAuditoriaEstoque3Item
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasItem
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.ui.auditoriaEstoque.adapters.AdapterAuditoriaEstoque1
import com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels.AuditoriaEstoqueApontmentoViewModel3
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.toastError

class ProdutoAndressAuditoriaEstoqueApActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProdutoAndressAuditoriaEstoqueApBinding
    private lateinit var adapterAuditoriaEstoque1: AdapterAuditoriaEstoque1
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var alertDialog: CustomAlertDialogCustom
    private lateinit var viewModel: AuditoriaEstoqueApontmentoViewModel3
    private lateinit var dialogProgress: Dialog
    private var idArmazem: Int? = null
    private var token: String? = null
    private var auditoria: ListaAuditoriasItem? = null
    private var estante: String? = null
    private var andress: ListEnderecosAuditoriaEstoque3Item? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProdutoAndressAuditoriaEstoqueApBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initConst()
        setToolbar()
        getIntentActivity()
        observer()

    }


    private fun getIntentActivity() {
        if (intent != null) {
            auditoria = intent.getSerializableExtra("AUDITORIA_SELECIONADA") as ListaAuditoriasItem?
            estante = intent.getStringExtra("ESTANTE")
            andress =
                intent.getSerializableExtra("ANDRESS_SELECT") as ListEnderecosAuditoriaEstoque3Item
            getData()
        } else {
            errorInitScreen()
        }
    }

    private fun errorInitScreen() {
        alertDialog.alertMessageErrorSimplesAction(
            this,
            "Ocorreu um erro ao receber dados, volte e tente novamente!",
            action = {
                finishAndRemoveTask()
            })
    }


    private fun initConst() {
//        adapterAuditoriaEstoque1 = AdapterAuditoriaEstoque1 { auditoria ->
//            AuditoriaEstoqueEstanteFragment(auditoria).show(supportFragmentManager, "ESTANTES")
//        }
        alertDialog = CustomAlertDialogCustom()
        dialogProgress = CustomAlertDialogCustom().progress(this, "Buscando auditorias...")
        dialogProgress.hide()
        sharedPreferences = CustomSharedPreferences(this)
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN)
        viewModel = ViewModelProvider(
            this,
            AuditoriaEstoqueApontmentoViewModel3.AuditoriaEstoqueApontmentoViewModelFactory3(
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

    private fun getData() {
        viewModel.getProdutoAndressAP(
            endereco = andress!!,
            auditoria = auditoria!!,
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
                this@ProdutoAndressAuditoriaEstoqueApActivity,
                "Sem itens a mostrar!"
            )
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModel3.notEmplyAuditoriasDb() {
        sucessGetProdutosShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { list ->
//            adapterAuditoriaEstoque1.update(list)
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModel3.errorDb() {
        errorDbShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { error ->
            alertDialog.alertMessageErrorSimples(
                this@ProdutoAndressAuditoriaEstoqueApActivity,
                error
            )
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModel3.errorAll() {
        errorAllShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { error ->
            alertDialog.alertMessageErrorSimples(
                this@ProdutoAndressAuditoriaEstoqueApActivity,
                error
            )
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModel3.validaProgress() {
        progressShow.observe(this@ProdutoAndressAuditoriaEstoqueApActivity) { result ->
            binding.progress.isVisible = result
        }
    }
}