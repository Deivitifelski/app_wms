package com.documentos.wms_beirario.ui.qualityControl.activity

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
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
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import com.documentos.wms_beirario.utils.extensions.returnNameVersionDb
import com.documentos.wms_beirario.utils.extensions.toastDefault
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.Observable
import java.util.Observer

class QualityControlActivity2 : AppCompatActivity(), Observer {

    private lateinit var binding: ActivityQualityControl2Binding
    private lateinit var viewModel: QualityControlViewModel
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var alertModal: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private var mAprovado: Int = 0
    private var mRejeitado: Int = 0
    private lateinit var idTarefa: String
    private lateinit var mList: ResponseControlQuality1
    private lateinit var token: String
    private var gerandoRequisicao = false
    private var idArmazem: Int = 0
    private lateinit var sharedPreferences: CustomSharedPreferences


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
            subtitle = returnNameVersionDb()
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
                this.idTarefa = idTarefa!!
                val list = intent.getSerializableExtra("LIST") as ResponseControlQuality1
                mList = list
            }
        } catch (e: Exception) {
            alertModal.alertErroInitBack(
                this,
                this,
                "erro ao receber dados!\nVoltar a tela anterior, e tente novamente!"
            )
        }
        setFluxos()
    }

    private fun initConst() {
        sharedPreferences = CustomSharedPreferences(this)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        viewModel = ViewModelProvider(
            this,
            QualityControlViewModel.QualityControlViewModelFactory1(QualityControlRepository())
        )[QualityControlViewModel::class.java]
        mSonsMp3 = CustomMediaSonsMp3()
        alertModal = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    private fun setFluxos() {
        if (REQUISICAO == null) {
            binding.txtInfDefault.text = "Gere a requisição dos itens reprovados."
            if (mList.rejeitados.isEmpty()) {
                binding.apply {
                    txtInfDefault.text = "Faça a leitura do endereço dos itens aprovados."
                    txtInf.text = getString(R.string.aprovedd)
                    txtInfQnt.text = mAprovado.toString()
                    binding.buttonGeraRequisicao.isEnabled = false
                    binding.buttonEndDestino.isEnabled = true
                }
            } else {
                binding.apply {
                    txtInf.text = getString(R.string.reprovedd)
                    txtInfQnt.text = mRejeitado.toString()
                    txtInfDefault.text = "Gere a requisição dos itens reprovados."
                    buttonGeraRequisicao.isEnabled = true
                    buttonEndDestino.isEnabled = false
                }
            }
        } else {
            binding.apply {
                buttonGeraRequisicao.isEnabled = false
                buttonEndDestino.isEnabled = true
                txtInfDefault.text =
                    "Faça a leitura do endereço dos itens aprovados\nRequisição gerada n°: $REQUISICAO"
            }
            if (mList.rejeitados.isEmpty()) {
                binding.apply {
                    txtInfDefault.text =
                        "Faça a leitura do endereço dos itens aprovados.\nRequisição gerada n°: $REQUISICAO"
                    txtInf.text = getString(R.string.aprovedd)
                    txtInfQnt.text = mAprovado.toString()
                }
            } else if (mList.rejeitados.size == mList.apontados.size) {
                binding.apply {
                    buttonEndDestino.text = "Finalizar"
                    txtInf.text = getString(R.string.reprovedd)
                    txtInfQnt.text = mRejeitado.toString()
                    txtInfDefault.text = "Clique em finalizar.\nRequisição gerada n°: $REQUISICAO"
                }
            } else {
                binding.apply {
                    txtInf.text = getString(R.string.reprovedd)
                    txtInfQnt.text = mRejeitado.toString()
                    txtInfDefault.text =
                        "Faça a leitura do endereço dos itens aprovados.\nRequisição gerada n°: $REQUISICAO"
                }


            }
        }
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
            if (!gerandoRequisicao) {
                gerandoRequisicao = true
                binding.buttonGeraRequisicao.isEnabled = false
                val body = BodyGenerateRequestControlQuality(idTarefa = idTarefa)
                viewModel.generateRequest(body = body, idArmazem, token)
            } else {
                alertModal.alertMessageErrorSimples(
                    this,
                    "Aguarde ainda estamos gerando a requisição..."
                )
            }
        }
    }


    private fun clickButtonLerEndereco() {
        binding.buttonEndDestino.setOnClickListener {
            if (binding.buttonEndDestino.text == "Finalizar") {
                val body = BodyFinishQualityControl(
                    codigoBarrasEndDest = null,
                    idTarefa = idTarefa
                )
                viewModel.finish(body, idArmazem, token)
            } else {
                alertModal.alertReadingAction(
                    context = this,
                    tittle = "Leia um enderçeo de destino",
                    actionBipagem = { codBarras ->
                        val body = BodyFinishQualityControl(
                            codigoBarrasEndDest = codBarras.trim(),
                            idTarefa = idTarefa
                        )
                        viewModel.finish(body, idArmazem, token)
                    },
                    actionCancel = {
                        mToast.toastDefault(this, "Operação de leitura cancelada!")
                    }
                )
            }
        }
    }

    private fun setObserver() {
        /**SUCESSO AO FINALIZAR -->*/
        viewModel.mSucessFinishShow.observe(this) { sucesso ->
            afterGetRequisicaoBack()
        }
        /**RESPONSE FINALIZAR -->*/
        viewModel.mErrorHttpShow.observe(this) { errorAll ->
            alertModal.alertMessageErrorSimples(this, errorAll)
        }

        /**ERRO FINALIZAÇÃO -->*/
        viewModel.mErrorFinishShow.observe(this) { error ->
            alertModal.alertMessageErrorSimples(this, error)
        }

        viewModel.mErrorAllShow.observe(this) { errorHttp ->
            alertModal.alertMessageErrorSimples(this, errorHttp)
        }

        /**RESPONSE GERA REQUISIÇÃO -->*/
        viewModel.mSucessGenerateRequestShow.observe(this) { requisicao ->
            REQUISICAO = requisicao[0].numeroRequisicao
            alertModal.alertMessageSucessAction(
                context = this,
                message = "Requisição: ${requisicao[0].numeroRequisicao}",
                action = {
                    FINALIZOU = true
                    /*Caso todos os itens sejam reprovados, o button de ler endereço altera para finalizar e assim ao clicar finaliza com
                    endereço MOV == null
                     */
                    if (mList.rejeitados.size == mList.apontados.size) {
                        binding.apply {
                            txtInfDefault.text =
                                "Clique em finalizar.\nRequisição gerada n°: $REQUISICAO"
                            buttonGeraRequisicao.isEnabled = false
                            buttonEndDestino.isEnabled = true
                            buttonEndDestino.text = "Finalizar"
                        }
                    } else {
                        afterGetRequisicao()
                    }
                }
            )
        }

        /**ERROR GERA REQUISIÇÃO -->*/
        viewModel.mErrorAllGenerateRequestShow.observe(this) { error ->
            gerandoRequisicao = false
            binding.buttonGeraRequisicao.isEnabled = true
            alertModal.alertMessageErrorSimples(this, error)
        }
        /**ERROR GERA http REQUISIÇÃO -->*/
        viewModel.mErrorHttpGenerateRequestShow.observe(this) { error ->
            gerandoRequisicao = false
            binding.buttonGeraRequisicao.isEnabled = true
            alertModal.alertMessageErrorSimples(this, error)
        }
        /**PROGRESS -->*/
        viewModel.mProgressShow.observe(this) { progress ->
            if (progress) binding.progressFinish.visibility = View.VISIBLE
            else binding.progressFinish.visibility = View.INVISIBLE
        }
    }


    private fun afterGetRequisicaoBack() {
        alertModal.alertMessageSucessAction(
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
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            Log.e("QUALITY 2 -->", "onNewIntent --> $scanData")
            UIUtil.hideKeyboard(this)
        }
    }

    override fun onBackPressed() {
        if (!binding.buttonEndDestino.isEnabled) {
            extensionBackActivityanimation(this)
            super.onBackPressed()
        } else {
            mSonsMp3.somError(this)
            vibrateExtension(400)
            toastDefault(this, "Finalize a requisição antes de voltar!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}