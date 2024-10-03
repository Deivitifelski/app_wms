package com.documentos.wms_beirario.ui.auditoriaEstoque.views

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityProdutoAndressAuditoriaEstoqueCpBinding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListEnderecosAuditoriaEstoque3Item
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasItem
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseAuditoriaEstoqueAP
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.ui.auditoriaEstoque.adapters.AdapterAuditoriaEstoqueCv
import com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels.AuditoriaEstoqueApontmentoViewModelCv
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarras
import com.documentos.wms_beirario.utils.extensions.getVersion
import com.documentos.wms_beirario.utils.extensions.toastError

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
    private var qtdVol = 0
    private var qtdPar = 0
    private lateinit var viewModel: AuditoriaEstoqueApontmentoViewModelCv
    private val resultBack =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                alertDialog.alertMessageSucessAction(
                    context = this@ProdutoAndressAuditoriaEstoqueCVActivity,
                    message = "Endereço conferido com sucesso!",
                    action = {
                        finishAndRemoveTask()
                        extensionBackActivityanimation(this@ProdutoAndressAuditoriaEstoqueCVActivity)
                    }
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProdutoAndressAuditoriaEstoqueCpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()
        initConst()
        getIntentActivity()
        setRv()
        observer()
        validaButtonSave()
        clickButtonSave()
        clickKeyNext()
        getQtdVolPar()

    }

    private fun getQtdVolPar() {

    }

    private fun clickKeyNext() {
        binding.editVolumes.extensionSetOnEnterExtensionCodBarras {
            binding.editPar.requestFocus()
        }
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
            subtitle = "Conf.Visual | contagem: $contagem | " + getVersion()
        }
    }

    private fun clickButtonSave() {
        binding.buttonSaveAuditoria.setOnClickListener {
            val qtdPar = binding.editPar.text.toString()
            val qtdVol = binding.editVolumes.text.toString()

            if (qtdPar.isBlank() || qtdVol.isBlank()) {
                toastError(this, "Preencha os campos!")
                return@setOnClickListener
            }

            val qtdParAtual = this.qtdPar.toString()
            val qtdVolAtual = this.qtdVol.toString()

            if (qtdPar == qtdParAtual && qtdVol == qtdVolAtual) {
                salvarQuantidades()
            } else {
                mostrarAlertaDivergencia()
            }
        }
    }

    private fun mostrarAlertaDivergencia() {
        alertDialog.alertMessageAtencaoOptionAction(
            context = this,
            message = "Contagem: $contagem\nQuantidades informadas diferem do que consta no sistema. Deseja conferir novamente?",
            actionNo = {
                salvarQuantidades()
            },
            actionYes = {
                contagem += 1
                if (contagem == 4) {
                    mostrarAlertaContagemMaxima()
                } else {
                    resetarCampos()
                }
            }
        )
    }

    private fun mostrarAlertaContagemMaxima() {
        alertDialog.alertMessageErrorSimplesAction(
            context = this,
            message = "Atenção: O limite de 3 contagens foi excedido. A contagem será reiniciada.",
            action = {
                contagem = 1
                resetarCampos()
            }
        )
    }

    private fun resetarCampos() {
        binding.editVolumes.setText("")
        binding.editPar.setText("")
        binding.editVolumes.requestFocus()
        setToolbar()
    }


    private fun salvarQuantidades() {
        val intent = Intent(this, AuditoriaApontVolActivity::class.java)
        intent.putExtra("ANDRESS_SELECT", andress)
        intent.putExtra("AUDITORIA_SELECT", auditoria)
        intent.putExtra("ESTANTE", estante)
        intent.putExtra("CONTAGEM", contagem)
        intent.putExtra("VOLUMES", binding.editVolumes.text.toString())
        intent.putExtra("AVULSO", binding.editPar.text.toString())
        resultBack.launch(intent)
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
        response.forEach { item ->
            if (item.tipoProduto == "VOLUME") {
                qtdVol += item.quantidadeAuditada
            }

            if (item.tipoProduto == "PAR") {
                qtdPar += item.quantidadeAuditada
            }

            Log.e(TAG, "QTD PARRES = $qtdPar")
            Log.e(TAG, "QTD VOLUMES = $qtdVol")
        }
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
}