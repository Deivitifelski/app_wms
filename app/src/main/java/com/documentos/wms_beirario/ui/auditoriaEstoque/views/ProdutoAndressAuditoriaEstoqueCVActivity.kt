package com.documentos.wms_beirario.ui.auditoriaEstoque.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityProdutoAndressAuditoriaEstoqueApBinding
import com.documentos.wms_beirario.databinding.ActivityProdutoAndressAuditoriaEstoqueCpBinding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListEnderecosAuditoriaEstoque3Item
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasItem
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.ui.auditoriaEstoque.adapters.AdapterAuditoriaEstoqueAP
import com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels.AuditoriaEstoqueApontmentoViewModel3
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.hideKeyBoardFocus
import com.documentos.wms_beirario.utils.extensions.hideKeyExtensionActivity

class ProdutoAndressAuditoriaEstoqueCVActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProdutoAndressAuditoriaEstoqueCpBinding
    private var auditoria: ListaAuditoriasItem? = null
    private var estante: String? = null
    private var idArmazem: Int? = null
    private var token: String? = null
    private var andress: ListEnderecosAuditoriaEstoque3Item? = null
    private lateinit var adapterCv: AdapterAuditoriaEstoqueAP
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var alertDialog: CustomAlertDialogCustom
    private lateinit var sonsMp3: CustomMediaSonsMp3
    private var contagem: Int = 1
    private lateinit var viewModel: AuditoriaEstoqueApontmentoViewModel3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProdutoAndressAuditoriaEstoqueCpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()
        getIntentActivity()
        initConst()
        clickButtonFinish()

    }

    private fun initConst() {
//        binding.editEndereco.hideKeyBoardFocus()
//        hideKeyExtensionActivity(binding.editEndereco)
        adapterCv = AdapterAuditoriaEstoqueAP()
        alertDialog = CustomAlertDialogCustom()
        sonsMp3 = CustomMediaSonsMp3()
        sharedPreferences = CustomSharedPreferences(this)
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN)
        viewModel = ViewModelProvider(
            this, AuditoriaEstoqueApontmentoViewModel3.AuditoriaEstoqueApontmentoViewModelFactory3(
                AuditoriaEstoqueRepository()
            )
        )[AuditoriaEstoqueApontmentoViewModel3::class.java]
    }

    private fun getIntentActivity() {
        if (intent != null) {
            auditoria = intent.getSerializableExtra("AUDITORIA_SELECT") as ListaAuditoriasItem?
            estante = intent.getStringExtra("ESTANTE")
            andress =
                intent.getSerializableExtra("ANDRESS_SELECT") as ListEnderecosAuditoriaEstoque3Item
        } else {
            errorInitScreen()
        }
    }

    private fun setToolbar() {
        binding.toolbarCv.apply {
            setNavigationOnClickListener {
                finishAndRemoveTask()
                extensionBackActivityanimation(this@ProdutoAndressAuditoriaEstoqueCVActivity)
            }
            title = "Auditoria de estoque"
            subtitle = "Conf.Visual|" + getVersionNameToolbar()
        }
    }

    private fun errorInitScreen() {
        alertDialog.alertMessageErrorSimplesAction(this,
            "Ocorreu um erro ao receber dados, volte e tente novamente!",
            action = {
                finishAndRemoveTask()
            })
    }

    private fun clickButtonFinish() {
        binding.buttonFinishAuditoria.setOnClickListener {
            alertDialog.alertMessageAtencaoOptionAction(
                context = this,
                message = "Deseja finalizar auditoria?",
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
}