package com.documentos.wms_beirario.ui.qualityControl.activity

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityQualityControl2Binding
import com.documentos.wms_beirario.model.qualityControl.BodyFinishQualityControl
import com.documentos.wms_beirario.model.qualityControl.BodyGenerateRequestControlQuality
import com.documentos.wms_beirario.model.qualityControl.ResponseControlQuality1
import com.documentos.wms_beirario.repository.qualityControl.QualityControlRepository
import com.documentos.wms_beirario.ui.qualityControl.activity.QualityControlActivity.Companion.FINALIZOU
import com.documentos.wms_beirario.ui.qualityControl.activity.QualityControlActivity.Companion.REQUISICAO
import com.documentos.wms_beirario.ui.qualityControl.viewModel.QualityControlViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.Observable
import java.util.Observer

class QualityControlActivity2 : AppCompatActivity(), Observer {

    private lateinit var binding: ActivityQualityControl2Binding
    private lateinit var mViewModel: QualityControlViewModel
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private var mAprovado: Int = 0
    private var mRejeitado: Int = 0
    private lateinit var mIdTarefa: String
    private lateinit var mList: ResponseControlQuality1
    private var mRejected: Boolean = false
    private var mApproved: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQualityControl2Binding.inflate(layoutInflater)
        setContentView(binding.root)


        initConst()
        setToolbar()
        getInput()
        initDataWedge()
        setupDataWedge()
        clickButtonGeraRequisicao()
        clickButtonLerEndereco()
        setObserver()

    }

    private fun setToolbar() {
        binding.toolbarQuality2.apply {
            subtitle = getVersionNameToolbar()
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun getInput() {
        try {
            if (intent != null) {
                val aprovado = intent.getIntExtra("APROVADOS", 0)
                mAprovado = aprovado
                val rejeitado = intent.getIntExtra("REPROVADOS", 0)
                mRejeitado = rejeitado
                val idTarefa = intent.getStringExtra("ID_TAREFA")
                mIdTarefa = idTarefa!!
                val list = intent.getSerializableExtra("LIST") as ResponseControlQuality1
                mList = list
            }
        } catch (e: Exception) {
            mAlert.alertErroInitBack(
                this,
                this,
                "erro ao receber dados!\nVoltar a tela anterior, e tente novamente!"
            )
        }
        setFluxos()
    }

    private fun initConst() {
        mViewModel = ViewModelProvider(
            this,
            QualityControlViewModel.QualityControlViewModelFactory1(QualityControlRepository())
        )[QualityControlViewModel::class.java]
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    private fun setFluxos() {
        binding.txtInfDefault.text =
            if (REQUISICAO == null) "Faça a leitura do endereço dos itens aprovados." else
                "Faça a leitura do endereço dos itens aprovados.\nRequisição gerada n°: $REQUISICAO"
        if (mList.rejeitados.isEmpty()) {
            binding.apply {
                txtInf.text = "Aprovados"
                txtInfQnt.text = mAprovado.toString()
                buttonGeraRequisicao.isEnabled = false
                buttonEndDestino.isEnabled = true
            }
        } else {
            setRejeitados()
        }
    }

    private fun setRejeitados() {
        binding.txtInf.text = "Reprovados"
        binding.txtInfQnt.text = mRejeitado.toString()
        binding.buttonGeraRequisicao.isEnabled = REQUISICAO == null
        binding.buttonEndDestino.isEnabled = !FINALIZOU
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

    private fun clickButtonGeraRequisicao() {
        /**CLIQUE NO BOTÃO CHAMA FUNÇÃO PARA GERAR A REQUISIÇÃO */
        binding.buttonGeraRequisicao.setOnClickListener {
            binding.buttonGeraRequisicao.isEnabled = false
            val body = BodyGenerateRequestControlQuality(idTarefa = mIdTarefa)
            mViewModel.generateRequest(body = body)
        }
    }


    private fun clickButtonLerEndereco() {
        binding.buttonEndDestino.setOnClickListener {
            mAlert.alertReadingAction(
                context = this,
                tittle = "Leia um enderçeo de destino",
                actionBipagem = { codBarras ->
                    val body = BodyFinishQualityControl(
                        codigoBarrasEndDest = codBarras.trim(),
                        idTarefa = mIdTarefa
                    )
                    mViewModel.finish(body)
                },
                actionCancel = {
                    mToast.toastDefault(this, "Operação de leitura cancelada!")
                }
            )
        }
    }

    private fun setObserver() {
        /**SUCESSO AO FINALIZAR -->*/
        mViewModel.mSucessFinishShow.observe(this) { sucesso ->
            afterGetRequisicaoBack()
        }
        /**RESPONSE FINALIZAR -->*/
        mViewModel.mErrorHttpShow.observe(this) { errorAll ->
            mAlert.alertMessageErrorSimples(this, errorAll)
        }

        /**ERRO FINALIZAÇÃO -->*/
        mViewModel.mErrorFinishShow.observe(this) { error ->
            mAlert.alertMessageErrorSimples(this, error)
        }

        mViewModel.mErrorAllShow.observe(this) { errorHttp ->
            mAlert.alertMessageErrorSimples(this, errorHttp)
        }

        /**RESPONSE GERA REQUISIÇÃO -->*/
        mViewModel.mSucessGenerateRequestShow.observe(this) { requisicao ->
            REQUISICAO = requisicao[0].numeroRequisicao
            mAlert.alertMessageSucessAction(
                context = this,
                message = "Requisição: ${requisicao[0].numeroRequisicao}",
                action = {
                    /*Caso todos os itens sejam reprovados, gera a requisição e volta a tela anterior
                     caso contrário segue o fluxo normal,fazendo a leitura para armazenagem -->*/
                    if (mList.rejeitados.size == mList.apontados.size) {
                        Handler(Looper.myLooper()!!).postDelayed({
                            afterGetRequisicaoBack()
                        }, 1000)
                    } else {
                        afterGetRequisicao()
                    }
                }
            )
        }

        /**ERROR GERA REQUISIÇÃO -->*/
        mViewModel.mErrorAllGenerateRequestShow.observe(this) { error ->
            binding.buttonGeraRequisicao.isEnabled = true
            mAlert.alertMessageErrorSimples(this, error)
        }
        /**ERROR GERA http REQUISIÇÃO -->*/
        mViewModel.mErrorHttpGenerateRequestShow.observe(this) { error ->
            binding.buttonGeraRequisicao.isEnabled = true
            mAlert.alertMessageErrorSimples(this, error)
        }
        /**PROGRESS -->*/
        mViewModel.mProgressShow.observe(this) { progress ->
            if (progress) binding.progressFinish.visibility = View.VISIBLE
            else binding.progressFinish.visibility = View.INVISIBLE
        }
    }


    private fun afterGetRequisicaoBack() {
        mAlert.alertMessageSucessAction(
            context = this,
            message = "Qualidade e armazenagem concluídas com sucesso!",
            action = {
                setResult(RESULT_OK)
                finish()
                onBackTransitionExtension()
            }
        )
    }

    private fun afterGetRequisicao() {
        binding.apply {
            txtInf.text = "Aprovados"
            txtInfQnt.text = mAprovado.toString()
            binding.txtInfDefault.text =
                if (REQUISICAO == null) "Faça a leitura do endereço dos itens aprovados." else
                    "Faça a leitura do endereço dos itens aprovados.\nRequisição gerada n°: $REQUISICAO"
            buttonGeraRequisicao.isEnabled = false
            buttonEndDestino.isEnabled = true
        }
    }


    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            Log.e("QUALITY 2 -->", "onNewIntent --> $scanData")
            UIUtil.hideKeyboard(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}