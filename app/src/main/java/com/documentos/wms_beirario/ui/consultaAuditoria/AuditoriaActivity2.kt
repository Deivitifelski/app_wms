package com.documentos.wms_beirario.ui.consultaAuditoria

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityAuditoria2Binding
import com.documentos.wms_beirario.model.auditoria.BodyAuditoriaFinish
import com.documentos.wms_beirario.model.auditoria.ResponseFinishAuditoria
import com.documentos.wms_beirario.model.auditoria.ResponseFinishAuditoriaItem
import com.documentos.wms_beirario.repository.consultaAuditoria.AuditoriaRepository
import com.documentos.wms_beirario.ui.consultaAuditoria.DialogFragment.DialogFragmentFinishAuditoria
import com.documentos.wms_beirario.ui.consultaAuditoria.adapter.AuditoriaAdapter3
import com.documentos.wms_beirario.ui.consultaAuditoria.viewModel.AuditoriaViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.mSucessToastExtension

class AuditoriaActivity2 : AppCompatActivity(), DialogFragmentFinishAuditoria.Back {

    private val TAG = "AUDITORIA 2"
    private lateinit var mBinding: ActivityAuditoria2Binding
    private lateinit var mAdapter: AuditoriaAdapter3
    private lateinit var mSons: CustomMediaSonsMp3
    private lateinit var mDialog: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
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
        observer()
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
        mSharedPreferences = CustomSharedPreferences(this)
        mViewModel = ViewModelProvider(
            this, AuditoriaViewModel2.Auditoria2ViewModelFactory(
                AuditoriaRepository()
            )
        )[AuditoriaViewModel2::class.java]

        mAdapter = AuditoriaAdapter3 { itemClik ->
            DialogFragmentFinishAuditoria(itemClik).show(
                supportFragmentManager,
                "FINLANIZAR_AUDITORIA"
            )
        }

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
                mSucessToastExtension(this, "Auditoria realizado com sucesso!")
                mAdapter.updateList(sucessPost)

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

    private fun setupRV() {
        mBinding.rvAuditoria2.apply {
            layoutManager = LinearLayoutManager(this@AuditoriaActivity2)
            setHasFixedSize(false)
            adapter = mAdapter
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun backClick(item: ResponseFinishAuditoriaItem, mQnt: String) {
        try {
            Log.e(TAG, "CÓDIGO BIPADO COM SUCESSO (SIM) contem na lista")
            val body = BodyAuditoriaFinish(
                mIntentIdAuditoria.toInt(),
                item.estante,
                item.idEndereco.toString(),
                mQnt
            )
            mViewModel.postItens(body = body)
        } catch (e: Exception) {
            mSons.somError(this)
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}