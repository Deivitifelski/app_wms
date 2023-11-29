package com.documentos.wms_beirario.ui.auditoriaEstoque.views

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityProdutoAndressAuditoriaEstoqueCpBinding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.request.BodyApontEndQtdAuditoriaEstoque
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListEnderecosAuditoriaEstoque3Item
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasItem
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseAuditoriaEstoqueAP
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.ui.auditoriaEstoque.adapters.AdapterAuditoriaEstoqueCv
import com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels.AuditoriaEstoqueApontmentoViewModelCv
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.toastError
import com.documentos.wms_beirario.utils.extensions.toastSucess

class ProdutoAndressAuditoriaEstoqueCVActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProdutoAndressAuditoriaEstoqueCpBinding
    private var auditoria: ListaAuditoriasItem? = null
    private var estante: String? = null
    private var idArmazem: Int? = null
    private var token: String? = null
    private var andress: ListEnderecosAuditoriaEstoque3Item? = null
    private lateinit var adapterCv: AdapterAuditoriaEstoqueCv
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var alertDialog: CustomAlertDialogCustom
    private lateinit var sonsMp3: CustomMediaSonsMp3
    private var contagem: Int = 1
    private lateinit var viewModel: AuditoriaEstoqueApontmentoViewModelCv
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProdutoAndressAuditoriaEstoqueCpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()
        initConst()
        getIntentActivity()
        clickButtonFinish()
        setRv()
        observer()
        validaButtonSave()
        clickButtonSave()

    }


    private fun initConst() {
        adapterCv = AdapterAuditoriaEstoqueCv()
        alertDialog = CustomAlertDialogCustom()
        sonsMp3 = CustomMediaSonsMp3()
        sharedPreferences = CustomSharedPreferences(this)
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN)
        viewModel = ViewModelProvider(
            this,
            AuditoriaEstoqueApontmentoViewModelCv.AuditoriaEstoqueApontmentoViewModelCvFactory(
                AuditoriaEstoqueRepository()
            )
        )[AuditoriaEstoqueApontmentoViewModelCv::class.java]
    }


    private fun getIntentActivity() {
        try {
            if (intent != null) {
                auditoria = intent.getSerializableExtra("AUDITORIA_SELECT") as ListaAuditoriasItem?
                estante = intent.getStringExtra("ESTANTE")
                andress =
                    intent.getSerializableExtra("ANDRESS_SELECT") as ListEnderecosAuditoriaEstoque3Item
                if (auditoria != null && estante != null && andress != null) {
                    getData()
                } else {
                    errorInitScreen()
                }
            } else {
                errorInitScreen()
            }
        } catch (e: Exception) {
            errorInitScreen()
        }
    }

    private fun setRv() {
        binding.rvApontamentoCv.apply {
            layoutManager = LinearLayoutManager(this@ProdutoAndressAuditoriaEstoqueCVActivity)
            adapter = adapterCv
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

    private fun clickButtonSave() {
        binding.buttonSaveAuditoria.setOnClickListener {
            alertDialog.alertMessageAtencaoOptionAction(
                context = this,
                message = "Confirma:\nPares: ${binding.editPar.text} - Volumes: ${binding.editVolumes.text}",
                actionNo = {},
                actionYes = {
                    enableButton(false)
                    val createBody = BodyApontEndQtdAuditoriaEstoque(
                        quantidade = binding.editPar.text.toString().toInt(),
                        tipoProduto = "PAR"
                    )
                    viewModel.saveParEndQtd(
                        token = token!!,
                        idEndereco = andress!!.idEndereco,
                        idArmazem = idArmazem!!,
                        contagem = contagem.toString(),
                        idAuditoria = auditoria!!.id,
                        body = createBody
                    )
                }
            )
        }
    }

    private fun enableButton(enable: Boolean) {
        binding.buttonSaveAuditoria.isEnabled = enable
        binding.buttonFinishAuditoria.isEnabled = enable

    }

    private fun validaButtonSave() {
        binding.editVolumes.addTextChangedListener {
            if (it != null) {
                enableButton()
            }
        }

        binding.editPar.addTextChangedListener {
            if (it != null) {
                enableButton()
            }
        }
    }

    private fun enableButton() {
        binding.buttonSaveAuditoria.isEnabled =
            binding.editPar.text.isNotEmpty() && binding.editVolumes.text.isNotEmpty()
    }

    private fun observer() {
        viewModel.apply {
            emplyAuditoriasDb()
            notEmplyAuditoriasDb()
            errorDb()
            errorAll()
            validaProgress()
            responseSavePar()
            responseSaveVol()
            erroSavePar()
            erroSaveVol()
//            validaContagemDb()
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelCv.erroSaveVol() {
        errorParDbShow.observe(this@ProdutoAndressAuditoriaEstoqueCVActivity) { result ->
            enableButton(true)
            alertDialog.alertMessageErrorSimples(
                this@ProdutoAndressAuditoriaEstoqueCVActivity,
                "${result}\nNão foi possivel salvar qtd de volumes."
            )
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelCv.erroSavePar() {
        errorParDbShow.observe(this@ProdutoAndressAuditoriaEstoqueCVActivity) { result ->
            enableButton(true)
            alertDialog.alertMessageErrorSimples(
                this@ProdutoAndressAuditoriaEstoqueCVActivity,
                "${result}\nNão foi possivel salvar qtd de pares."
            )
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelCv.responseSavePar() {
        sucessSaveEndQtdShow.observe(this@ProdutoAndressAuditoriaEstoqueCVActivity) { result ->
            if (result.erro == "true") {
                enableButton(true)
                alertDialog.alertMessageErrorSimples(
                    this@ProdutoAndressAuditoriaEstoqueCVActivity,
                    result.mensagemErro
                )
            } else {
                val createBody = BodyApontEndQtdAuditoriaEstoque(
                    quantidade = binding.editPar.text.toString().toInt(),
                    tipoProduto = "VOLUMES"
                )
                viewModel.saveVolEndQtd(
                    token = token!!,
                    idEndereco = andress!!.idEndereco,
                    idArmazem = idArmazem!!,
                    contagem = contagem.toString(),
                    idAuditoria = auditoria!!.id,
                    body = createBody
                )
            }
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelCv.responseSaveVol() {
        sucessSaveEndQtdShow.observe(this@ProdutoAndressAuditoriaEstoqueCVActivity) { result ->
            enableButton(true)
            if (result.erro == "true") {
                alertDialog.alertMessageErrorSimples(
                    this@ProdutoAndressAuditoriaEstoqueCVActivity,
                    result.mensagemErro
                )
            } else {
                alertDialog.alertMessageSucessAction(
                    this@ProdutoAndressAuditoriaEstoqueCVActivity,
                    "Salvo com sucesso!",
                    action = {
                        toastSucess(this@ProdutoAndressAuditoriaEstoqueCVActivity, "Nada feito!")
                    })
            }
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelCv.emplyAuditoriasDb() {
        sucessGetProdutosEmplyShow.observe(this@ProdutoAndressAuditoriaEstoqueCVActivity) { emply ->
            binding.txtInfo.text = "Sem produtos para auditoria"
            binding.txtInfo.visibility = View.VISIBLE
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelCv.notEmplyAuditoriasDb() {
        sucessGetProdutosShow.observe(this@ProdutoAndressAuditoriaEstoqueCVActivity) { response ->
            if (response != null) {
                try {
                    binding.txtInfo.visibility = View.GONE
                    setDataTxt(response)
                    adapterCv.update(response)
                } catch (e: Exception) {
                    Log.e(TAG, "$e")
                    toastError(
                        this@ProdutoAndressAuditoriaEstoqueCVActivity,
                        "Erro ao receber dados!"
                    )
                }
            }
        }
    }

    private fun setDataTxt(response: List<ResponseAuditoriaEstoqueAP>) {
        var qtdVol = 0
        var qtdPar = 0
        response.forEach { item ->
            if (item.tipoProduto == "VOLUME") {
                qtdVol += item.quantidadeAuditada
            }

            if (item.tipoProduto == "PAR") {
                qtdPar += item.quantidadeAuditada
            }
        }

        binding.txtAllPar.text = qtdPar.toString()
        binding.txtAllVol.text = qtdVol.toString()

    }

    private fun AuditoriaEstoqueApontmentoViewModelCv.errorDb() {
        errorDbShow.observe(this@ProdutoAndressAuditoriaEstoqueCVActivity) { error ->
            alertDialog.alertMessageErrorSimples(
                this@ProdutoAndressAuditoriaEstoqueCVActivity, error
            )
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelCv.errorAll() {
        errorAllShow.observe(this@ProdutoAndressAuditoriaEstoqueCVActivity) { error ->
            alertDialog.alertMessageErrorSimples(
                this@ProdutoAndressAuditoriaEstoqueCVActivity, error
            )
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelCv.validaProgress() {
        progressShow.observe(this@ProdutoAndressAuditoriaEstoqueCVActivity) { result ->
            binding.progress.isVisible = result
        }
    }

    private fun errorInitScreen() {
        alertDialog.alertMessageErrorSimplesAction(this,
            "Ocorreu um erro ao receber dados, volte e tente novamente!",
            action = {
                finishAndRemoveTask()
            })
    }

    private fun getData() {
        viewModel.getProdutoAndressCv(
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