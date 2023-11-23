package com.documentos.wms_beirario.ui.auditoriaEstoque.views

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityAuditoriaEstoqueBinding
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.ui.auditoriaEstoque.adapters.AdapterAuditoriaEstoque1
import com.documentos.wms_beirario.ui.auditoriaEstoque.fragment.AuditoriaEstoqueEstanteFragment
import com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels.AuditoriaEstoqueViewModel1
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar

class AuditoriaEstoqueActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuditoriaEstoqueBinding
    private lateinit var adapterAuditoriaEstoque1: AdapterAuditoriaEstoque1
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var alertDialog: CustomAlertDialogCustom
    private lateinit var viewModel: AuditoriaEstoqueViewModel1
    private lateinit var dialogProgress: Dialog
    private var idArmazem: Int? = null
    private var token: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuditoriaEstoqueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initConst()
        setToolbar()
        setRv()
    }

    override fun onResume() {
        super.onResume()
        getData()
        observer()
    }


    private fun initConst() {
        adapterAuditoriaEstoque1 = AdapterAuditoriaEstoque1 { auditoria ->
            AuditoriaEstoqueEstanteFragment(auditoria).show(supportFragmentManager, "ESTANTES")
        }
        alertDialog = CustomAlertDialogCustom()
        dialogProgress = CustomAlertDialogCustom().progress(this, "Buscando auditorias...")
        dialogProgress.hide()
        sharedPreferences = CustomSharedPreferences(this)
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN)
        viewModel = ViewModelProvider(
            this,
            AuditoriaEstoqueViewModel1.AuditoriaEstoqueViewModel1Factory(AuditoriaEstoqueRepository())
        )[AuditoriaEstoqueViewModel1::class.java]
    }

    private fun setRv() {
        binding.rvAuditoria1.apply {
            adapter = adapterAuditoriaEstoque1
            layoutManager = LinearLayoutManager(this@AuditoriaEstoqueActivity)
        }
    }

    private fun setToolbar() {
        binding.toolbarAuditoriaEstoque1.apply {
            setNavigationOnClickListener { onBackPressed() }
            title = "Auditoria de estoque"
            subtitle = getVersionNameToolbar()
        }
    }

    private fun getData() {
        if (idArmazem != null && token != null) {
            viewModel.getAuditorias(idArmazem!!,token!!)
        } else {
            alertDialog.alertErroInitBack(
                this,
                this,
                "Ocorreu um erro ao receber armazem do usuário\nVolte e tente novamente!"
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
        }
    }

    private fun AuditoriaEstoqueViewModel1.emplyAuditoriasDb() {
        sucessGetAuditoriaEmplyShow.observe(this@AuditoriaEstoqueActivity) { emply ->
            binding.txtEmply.apply {
                visibility = View.VISIBLE
                text = emply
            }
        }
    }

    private fun AuditoriaEstoqueViewModel1.notEmplyAuditoriasDb() {
        sucessGetAuditoriaShow.observe(this@AuditoriaEstoqueActivity) { list ->
            binding.txtEmply.visibility = View.GONE
            adapterAuditoriaEstoque1.update(list)
        }
    }

    private fun AuditoriaEstoqueViewModel1.errorDb() {
        errorDbShow.observe(this@AuditoriaEstoqueActivity) { error ->
            binding.txtEmply.visibility = View.GONE
            alertDialog.alertMessageErrorSimples(this@AuditoriaEstoqueActivity,error)
        }
    }

    private fun AuditoriaEstoqueViewModel1.errorAll() {
        errorAllShow.observe(this@AuditoriaEstoqueActivity) { error ->
            binding.txtEmply.visibility = View.GONE
            alertDialog.alertMessageErrorSimples(this@AuditoriaEstoqueActivity,error)
        }
    }

    private fun AuditoriaEstoqueViewModel1.validaProgress() {
        progressShow.observe(this@AuditoriaEstoqueActivity) { progress ->
            binding.txtEmply.visibility = View.GONE
            if (progress) dialogProgress.show() else dialogProgress.hide()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dialogProgress.dismiss()
    }
}