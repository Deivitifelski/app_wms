package com.documentos.wms_beirario.ui.auditoriaEstoque.views

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityAuditoriaEstoqueEndereco2Binding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasItem
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.ui.auditoriaEstoque.adapters.AdapterAuditoriaEstoque3
import com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels.AuditoriaEstoqueViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar

class AuditoriaEstoqueEnderecoActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityAuditoriaEstoqueEndereco2Binding
    private lateinit var adapterEnderecos: AdapterAuditoriaEstoque3
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var alertDialog: CustomAlertDialogCustom
    private lateinit var viewModel: AuditoriaEstoqueViewModel2
    private var auditoria: ListaAuditoriasItem? = null
    private var estante: String? = null
    private var idArmazem: Int? = null
    private var token: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuditoriaEstoqueEndereco2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        initConst()
        setToolbar()
        setRv()
        getIntentData()
        getData()
        observer()
    }


    private fun initConst() {
        adapterEnderecos = AdapterAuditoriaEstoque3 { auditoria ->

        }
        alertDialog = CustomAlertDialogCustom()
        sharedPreferences = CustomSharedPreferences(this)
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN)
        viewModel = ViewModelProvider(
            this,
            AuditoriaEstoqueViewModel2.AuditoriaEstoqueViewModel2Factory(AuditoriaEstoqueRepository())
        )[AuditoriaEstoqueViewModel2::class.java]
    }

    private fun setRv() {
        binding.rvEnderecos.apply {
            adapter = adapterEnderecos
            layoutManager = LinearLayoutManager(this@AuditoriaEstoqueEnderecoActivity2)
        }
    }

    private fun setToolbar() {
        binding.toolbarEnderecos.apply {
            setNavigationOnClickListener { onBackPressed() }
            title = "Endereços"
            subtitle = getVersionNameToolbar()
        }
    }

    private fun getIntentData() {
        if (intent != null) {
            auditoria = intent.getSerializableExtra("AUDITORIA_SELECIONADA") as ListaAuditoriasItem?
            estante = intent.getStringExtra("ESTANTE")
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

    private fun getData() {
        if (token != null && idArmazem != null && estante != null && auditoria != null) {
            viewModel.getEnderecos(
                idArmazem = idArmazem!!,
                token = token!!,
                idAuditoriaEstoque = auditoria!!.id,
                estante = estante!!
            )
        } else {
            errorInitScreen()
        }
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


    private fun AuditoriaEstoqueViewModel2.emplyAuditoriasDb() {
        sucessGetAuditoriaEmplyShow.observe(this@AuditoriaEstoqueEnderecoActivity2) { emply ->
            binding.txtInfo.apply {
                visibility = View.VISIBLE
                text = emply
            }
        }
    }

    private fun AuditoriaEstoqueViewModel2.notEmplyAuditoriasDb() {
        sucessGetAuditoriaEnderecoShow.observe(this@AuditoriaEstoqueEnderecoActivity2) { list ->
            adapterEnderecos.update(list)
        }
    }

    private fun AuditoriaEstoqueViewModel2.errorDb() {
        errorDbShow.observe(this@AuditoriaEstoqueEnderecoActivity2) { error ->
            binding.txtInfo.visibility = View.GONE
            alertDialog.alertMessageErrorSimples(this@AuditoriaEstoqueEnderecoActivity2, error)
        }
    }

    private fun AuditoriaEstoqueViewModel2.errorAll() {
        errorAllShow.observe(this@AuditoriaEstoqueEnderecoActivity2) { error ->
            binding.txtInfo.visibility = View.GONE
            alertDialog.alertMessageErrorSimples(this@AuditoriaEstoqueEnderecoActivity2, error)
        }
    }

    private fun AuditoriaEstoqueViewModel2.validaProgress() {
        progressShow.observe(this@AuditoriaEstoqueEnderecoActivity2) { progress ->
            binding.txtInfo.visibility = View.GONE
            binding.progress.isVisible = progress
        }
    }
}