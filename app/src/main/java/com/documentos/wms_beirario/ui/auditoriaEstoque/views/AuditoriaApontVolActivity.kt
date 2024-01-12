package com.documentos.wms_beirario.ui.auditoriaEstoque.views

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityAuditoriaApontVolBinding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.request.BodyApontEndQtdAuditoriaEstoque
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListEnderecosAuditoriaEstoque3Item
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasItem
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels.AuditoriaEstoqueApontmentoViewModelCv
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.hideKeyBoardFocus
import com.documentos.wms_beirario.utils.extensions.hideKeyExtensionActivity
import java.util.Observable
import java.util.Observer

class AuditoriaApontVolActivity : AppCompatActivity(), Observer {

    private val TAG = "AuditoriaAPontVolActivity"
    private lateinit var binding: ActivityAuditoriaApontVolBinding
    private lateinit var alertDialog: CustomAlertDialogCustom
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var somMp3: CustomMediaSonsMp3
    private var auditoria: ListaAuditoriasItem? = null
    private var estante: String? = null
    private var volumes: String? = null
    private var avulso: String? = null
    private var idArmazem: Int? = null
    private var token: String? = null
    private var andress: ListEnderecosAuditoriaEstoque3Item? = null
    private var contagem: Int? = null
    private lateinit var viewModel: AuditoriaEstoqueApontmentoViewModelCv
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuditoriaApontVolBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initConst()
        getIntentActivity()
        setLayout()
        initDataWedge()
        setupDataWedge()
        clickKey()
        setToolbar()
        observer()
        clickButtonClose()
    }

    private fun clickButtonClose() {
        binding.buttonClose.setOnClickListener {
            finishAndRemoveTask()
            setResult(RESULT_CANCELED)
        }
    }

    private fun setToolbar() {
        binding.toolbarApont.apply {
            setNavigationOnClickListener {
                finishAndRemoveTask()
                setResult(RESULT_CANCELED)
            }
        }
    }

    private fun initConst() {
        viewModel = ViewModelProvider(
            this,
            AuditoriaEstoqueApontmentoViewModelCv.AuditoriaEstoqueApontmentoViewModelCvFactory(
                AuditoriaEstoqueRepository()
            )
        )[AuditoriaEstoqueApontmentoViewModelCv::class.java]
        binding.editApontVol.hideKeyBoardFocus()
        alertDialog = CustomAlertDialogCustom()
        somMp3 = CustomMediaSonsMp3()
        sharedPreferences = CustomSharedPreferences(this)
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN)
    }


    private fun clickKey() {
        binding.imageView3.setOnClickListener {
            alertDialog.alertEditText(
                context = this,
                title = "Auditoria de estoque/Aponta volume",
                subTitle = "Digite um Volume que deseja apontar",
                actionYes = { sendScan(it) },
                actionNo = { hideKeyExtensionActivity(binding.editApontVol) }
            )
        }
    }


    private fun getIntentActivity() {
        try {
            if (intent != null) {
                auditoria = intent.getSerializableExtra("AUDITORIA_SELECT") as ListaAuditoriasItem?
                estante = intent.getStringExtra("ESTANTE")
                volumes = intent.getStringExtra("VOLUMES")
                avulso = intent.getStringExtra("AVULSO")
                andress =
                    intent.getSerializableExtra("ANDRESS_SELECT") as ListEnderecosAuditoriaEstoque3Item
                contagem = intent.getIntExtra("CONTAGEM", 0)
                if (auditoria != null && estante != null && andress != null && contagem != 0) {

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

    private fun errorInitScreen() {
        alertDialog.alertMessageErrorSimplesAction(this,
            "Ocorreu um erro ao receber dados, volte e tente novamente!",
            action = {
                finishAndRemoveTask()
            })
    }

    private fun setLayout() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val largura: Int = displayMetrics.widthPixels
        val altura: Int = displayMetrics.heightPixels
        window.setLayout((largura * 0.9).toInt(), (altura * 0.5).toInt())
        val params = window.attributes
        params.gravity = Gravity.CENTER
//        params.x = 0
//        params.y = 80
        window.attributes = params
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


    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            sendScan(scanData.toString())
        }
    }

    private fun sendScan(scan: String) {
        if (auditoria != null && estante != null && andress != null && contagem != 0 && avulso != null && volumes != null) {
            val body = BodyApontEndQtdAuditoriaEstoque(
                numeroSerie = scan,
                quantidadePar = avulso!!.toInt(),
                quantidadeVol = volumes!!.toInt(),
                tipoProdutoPar = "PAR",
                tipoProdutoVol = "VOLUME"
            )
            viewModel.saveEndQtd(
                idEndereco = andress?.idEndereco!!,
                idAuditoria = auditoria?.id!!,
                idArmazem = idArmazem!!,
                token = token!!,
                contagem = contagem.toString(),
                body = body
            )
        } else {
            errorInitScreen()
        }
    }

    private fun observer() {
        viewModel.apply {
            errorDb()
            errorAll()
            validaProgress()
            sucessSaveEndQtd()
            erroSaveVol()
            errorSave()
        }
    }


    private fun AuditoriaEstoqueApontmentoViewModelCv.erroSaveVol() {
        errorSaveEndQtdShow.observe(this@AuditoriaApontVolActivity) { result ->
            alertDialog.alertMessageErrorSimples(
                this@AuditoriaApontVolActivity,
                result
            )
        }
    }


    private fun AuditoriaEstoqueApontmentoViewModelCv.sucessSaveEndQtd() {
        sucessSaveEndQtdShow.observe(this@AuditoriaApontVolActivity) { res ->
            if (res.erro == "true") {
                alertDialog.alertMessageAtencaoOptionAction(
                    context = this@AuditoriaApontVolActivity,
                    res.mensagemErro,
                    actionNo = {},
                    actionYes = {
                        binding.editApontVol.hideKeyBoardFocus()
                    }
                )
            } else {
                val intent = Intent()
                intent.putExtra("SUCESS", res.mensagemErro)
                setResult(RESULT_OK)
                finishAndRemoveTask()
            }
        }
    }


    private fun AuditoriaEstoqueApontmentoViewModelCv.errorDb() {
        errorDbShow.observe(this@AuditoriaApontVolActivity) { error ->
            alertDialog.alertMessageErrorSimples(
                this@AuditoriaApontVolActivity, error
            )
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelCv.errorAll() {
        errorAllShow.observe(this@AuditoriaApontVolActivity) { error ->
            alertDialog.alertMessageErrorSimples(
                this@AuditoriaApontVolActivity, error
            )
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelCv.errorSave() {
        errorSaveDbShow.observe(this@AuditoriaApontVolActivity) { error ->
            alertDialog.alertMessageErrorSimples(
                this@AuditoriaApontVolActivity, error
            )
        }
    }

    private fun AuditoriaEstoqueApontmentoViewModelCv.validaProgress() {
        progressShow.observe(this@AuditoriaApontVolActivity) { result ->
            binding.progress.isVisible = result
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        finishAndRemoveTask()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}