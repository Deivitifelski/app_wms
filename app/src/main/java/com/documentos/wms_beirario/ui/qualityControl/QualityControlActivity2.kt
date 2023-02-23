package com.documentos.wms_beirario.ui.qualityControl

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityQualityControl2Binding
import com.documentos.wms_beirario.model.qualityControl.BodyFinishQualityControl
import com.documentos.wms_beirario.model.qualityControl.ResponseQualityResponse1
import com.documentos.wms_beirario.repository.qualityControl.QualityControlRepository
import com.documentos.wms_beirario.ui.qualityControl.viewModel.QualityControlViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionStartActivity
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

class QualityControlActivity2 : AppCompatActivity(), Observer {

    private lateinit var mBinding: ActivityQualityControl2Binding
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
    private lateinit var mList: ResponseQualityResponse1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityQualityControl2Binding.inflate(layoutInflater)
        setContentView(mBinding.root)


        initConst()
        setToolbar()
        getInput()
        initDataWedge()
        setupDataWedge()
        setRejeitados()
        clickButtonGeraRequisicao()
        clickButtonLerEndereco()
        setObserver()
    }


    private fun getInput() {
        try {
            if (intent != null) {
                val aprovado = intent.getIntExtra("APROVADO", 0)
                mAprovado = aprovado
                val rejeitado = intent.getIntExtra("REJEITADO", 0)
                mRejeitado = rejeitado
                val idTarefa = intent.getStringExtra("ID_TAREFA")
                mIdTarefa = idTarefa!!
                val list = intent.getSerializableExtra("LIST") as ResponseQualityResponse1
                mList = list
                Log.e("*", "getInput: $mList")
            }
        } catch (e: Exception) {
            mAlert.alertErroInitBack(
                this,
                this,
                "erro ao receber dados!\nVoltar a tela anterior, e tente novamente!"
            )
        }
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

    private fun setRejeitados() {
        mBinding.txtInf.text = "Rejeitados"
        mBinding.txtInfQnt.text = mRejeitado.toString()
    }

    private fun setToolbar() {
        mBinding.toolbarQuality2.apply {
            subtitle = getVersionNameToolbar()
            setNavigationOnClickListener {
                onBackPressed()
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
        mBinding.buttonGeraRequisicao.setOnClickListener {
            mAlert.alertMessageSucessAction(
                context = this,
                message = "Requisição: 0000000000000",
                action = {
                    if (mList.naoAprovados.size == mList.apontados.size) {
                        afterGetRequisicaoBack()
                    } else {
                        afterGetRequisicao()
                    }
                }
            )
        }
    }

    private fun afterGetRequisicaoBack() {
        val intent = Intent(this, QualityControlctivity::class.java)
        setResult(RESULT_OK, intent)
    }

    private fun afterGetRequisicao() {
        mBinding.apply {
            txtInf.text = "Aprovados"
            txtInfQnt.text = mAprovado.toString()
            txtInfDefault.text = "Faça a leitura do endereço dos itens aprovados."
            buttonGeraRequisicao.isEnabled = false
            buttonEndDestino.isEnabled = true
        }
    }

    private fun clickButtonLerEndereco() {
        mBinding.buttonEndDestino.setOnClickListener {
            mAlert.alertReadingAction(
                context = this,
                tittle = "Leia um enderçeo de destino",
                actionBipagem = { codBarras ->
                    val body = BodyFinishQualityControl(
                        codigoCarrasEndDest = codBarras.trim(),
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
        mViewModel.mErrorAllShow.observe(this) { errorAll ->
            mAlert.alertMessageErrorSimples(this, errorAll)
        }

        mViewModel.mErrorHttpShow.observe(this) { errorHttp ->
            mAlert.alertMessageErrorSimples(this, errorHttp)
        }
        mViewModel.mSucessFinishShow.observe(this) { sucesso ->
            mAlert.alertMessageSucessAction(
                context = this,
                message = "Qualidade e armazenagem concluidas com sucesso.",
                action = {
                    extensionStartActivity(QualityControlctivity())
                    extensionBackActivityanimation(this)
                }
            )
        }
        mViewModel.mProgressShow.observe(this) { progress ->
            if (progress) mBinding.progressFinish.visibility = View.VISIBLE
            else mBinding.progressFinish.visibility = View.INVISIBLE
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