package com.documentos.wms_beirario.ui.consultaAuditoria

import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityAuditoria2Binding
import com.documentos.wms_beirario.databinding.LayoutAlertSucessCustomBinding
import com.documentos.wms_beirario.model.auditoria.BodyAuditoriaFinish
import com.documentos.wms_beirario.model.auditoria.ResponseFinishAuditoria
import com.documentos.wms_beirario.repository.consultaAuditoria.AuditoriaRepository
import com.documentos.wms_beirario.ui.consultaAuditoria.adapter.AuditoriaAdapter3
import com.documentos.wms_beirario.ui.consultaAuditoria.viewModel.AuditoriaViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

class AuditoriaActivity2 : AppCompatActivity(), Observer {

    private val TAG = "AUDITORIA 2"
    private lateinit var mBinding: ActivityAuditoria2Binding
    private lateinit var mAdapter: AuditoriaAdapter3
    private lateinit var mSons: CustomMediaSonsMp3
    private lateinit var mDialog: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mIntentIdAuditoria: String
    private lateinit var mIntentEstante: String
    private lateinit var mViewModel: AuditoriaViewModel2
    private lateinit var mSharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityAuditoria2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        setCost()
        setToolbar()
        initIntent()
        getData()
        setupRV()
        setupEdit()
        observer()
    }

    override fun onResume() {
        super.onResume()
        clearEdit()
        visibilityKey()
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun visibilityKey() {
        mBinding.editAuditoria02.setOnClickListener {
            showKeyExtensionActivity(mBinding.editAuditoria02)
        }
    }


    private fun setToolbar() {
        mBinding.toolbarAuditoria2.apply {
            subtitle = getVersionNameToolbar()
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun initIntent() {
        try {
            if (intent.extras != null) {
                val id = intent.getStringExtra("ID")
                val estante = intent.getStringExtra("ESTANTE")
                mIntentIdAuditoria = id.toString()
                mIntentEstante = estante.toString()
                Log.e(TAG, "Iniciando Activity initIntent -> $mIntentIdAuditoria - $mIntentEstante")
            }
        } catch (e: Exception) {
            mDialog.alertErroInitBack(this, this, "Erro ao receber dados!")
        }
    }

    private fun setCost() {
        mBinding.editAuditoria02.requestFocus()
        mSharedPreferences = CustomSharedPreferences(this)
        mViewModel = ViewModelProvider(
            this, AuditoriaViewModel2.Auditoria2ViewModelFactory(
                AuditoriaRepository()
            )
        )[AuditoriaViewModel2::class.java]

        mAdapter = AuditoriaAdapter3()
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
        mDialog = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
        mSons = CustomMediaSonsMp3()
    }


    private fun getData() {
        mViewModel.getReceipt3(mIntentIdAuditoria, mIntentEstante)
        Log.e(TAG, "TENTANDO ENVIAR -> id:$mIntentIdAuditoria estante:$mIntentEstante")
    }

    private fun observer() {
        /**BUSCA ITENS DA ESTANTE (PRIMEIRO GET QUE MOSTRAR ITENS CONTIDOS DENTRO DA ESTANTE)-->*/
        mViewModel.mSucessAuditoria3Show.observe(this) { sucess ->
            if (sucess.isEmpty()) {
                mDialog.alertMessageSucessFinishBack(
                    this,
                    this,
                    "Todos os itens já foram apontados!"
                )
            } else {
                mBinding.txtAllReanding.text = "Total de itens: ${returnSizeItens(sucess)}"
                mAdapter.updateList(sucess)
            }
        }

        mViewModel.mValidProgressEditShow.observe(this) { progress ->
            mBinding.progressAuditoria2.isVisible = progress
        }

        mViewModel.mErrorAuditoriaShow.observe(this) { error ->
            mBinding.txtAllReanding.isVisible = false
            mDialog.alertMessageErrorSimples(this, error)
        }

        mViewModel.mErrorAllShow.observe(this) { error ->
            mBinding.txtAllReanding.isVisible = false
            mDialog.alertMessageErrorSimples(this, error)
        }

        /**RESPOSTA DA BIPAGEM -->*/
        mViewModel.mSucessPostShow.observe(this) { sucessPost ->
            mBinding.txtAllReanding.text = "Total de itens: ${returnSizeItens(sucessPost)}"
            clearEdit()
            mSons.somSucess(this)
            val list = returnSizeItens(sucessPost)
            if (list == "0") {
                mBinding.txtAllReanding.text = "Todos os itens já foram apontados!"
                mAdapter.updateList(sucessPost)
                mDialog.alertMessageSucessFinishBack(
                    this,
                    this,
                    "Todos os itens já foram apontados!"
                )
            } else {
                mAdapter.updateList(sucessPost)
                clearEdit()
            }
        }
        mViewModel.mErrorPostShow.observe(this) { errorPost ->
            mDialog.alertMessageErrorSimples(this, errorPost, 3000)
        }
    }

    /**RETORNA QUANTIDADE DE ITENS AINDA NÃO AUDITADOS -->*/
    private fun returnSizeItens(sucess: ResponseFinishAuditoria): String {
        var count = 0
        sucess.forEach {
            if (!it.auditado) {
                count += 1
                Log.e(TAG, "COD_DE_BARRAS --> ${it.codBarrasEndereco} || ${it.auditado}")
            }
        }
        return count.toString()
    }

    private fun setupEdit() {
        mBinding.editAuditoria02.extensionSetOnEnterExtensionCodBarras {
            if (mBinding.editAuditoria02.text.toString().isEmpty()) {
                mBinding.editLayoutNumAuditoria2.shake { mErroToastExtension(this, "Campo Vazio!") }
            } else {
                sendData(mBinding.editAuditoria02.text.toString())
                clearEdit()
            }
        }
    }

    private fun setupRV() {
        mBinding.rvAuditoria2.apply {
            layoutManager = LinearLayoutManager(this@AuditoriaActivity2)
            setHasFixedSize(false)
            adapter = mAdapter
        }
    }

    /**PEGAR O ITEM BIPAFO (CODIGO DE BARRAS) VERIFICAR SE EXISTE NA LISTA E PEGAR O Objeto  INFORMADO E ENVIA DADOS PELO POST --> */
    private fun sendData(codigo: String) {
        try {
            val mContensCodigoList = mAdapter.returnCodBarras(codigo)
            if (mContensCodigoList != null) {
                Log.e(TAG, "CÓDIGO BIPADO: $codigo || (SIM) contem na lista")
                val body = BodyAuditoriaFinish(
                    mIntentIdAuditoria.toInt(),
                    mContensCodigoList.estante,
                    mContensCodigoList.idEndereco.toString(),
                    mContensCodigoList.quantidade.toString()
                )
                mViewModel.postItens(body = body)
            } else {
                Log.e(TAG, "CÓDIGO BIPADO: $codigo || (NÃO) contem na lista")
                mDialog.alertMessageErrorSimples(
                    this,
                    "Endereço não encontrado ou não Contido na auditoria selecionada!",
                    4000
                )
            }
            clearEdit()
            UIUtil.hideKeyboard(this, mBinding.editAuditoria02)
        } catch (e: Exception) {
            mErroToastExtension(this, "Erro ao enviar dados!\nTente Novamente!")
        }
    }

    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            Log.e(TAG, "onNewIntent AUditoria 2 -> $scanData")
            sendData(scanData.toString())
            clearEdit()
        }
    }

    private fun clearEdit() {
        mBinding.editAuditoria02.requestFocus()
        mBinding.editAuditoria02.text?.clear()
        mBinding.editAuditoria02.setText("")
        hideKeyExtensionActivity(mBinding.editAuditoria02)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    /**
     * MODAL QUANDO FINALIZOU TODOS OS ITENS -->
     */
    private fun alertMessageSucess(message: String) {
        val mAlert = AlertDialog.Builder(this)
        mAlert.setCancelable(false)
        val binding = LayoutAlertSucessCustomBinding.inflate(layoutInflater)
        mAlert.setView(binding.root)
        val mShow = mAlert.show()
        mAlert.create()
        binding.editCustomAlertSucess.addTextChangedListener {
            if (it.toString() != "") {
                mShow.dismiss()
            }
        }
        binding.txtMessageSucess.text = message
        binding.buttonSucessLayoutCustom.setOnClickListener {
            CustomMediaSonsMp3().somClick(this)
            mShow.dismiss()
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}