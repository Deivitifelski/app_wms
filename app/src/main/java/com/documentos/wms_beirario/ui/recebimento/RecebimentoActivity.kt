package com.documentos.wms_beirario.ui.recebimento

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.ActivityRecebimentoBinding
import com.documentos.wms_beirario.extensions.AppExtensions
import com.documentos.wms_beirario.extensions.onBackTransition
import com.documentos.wms_beirario.extensions.vibrateExtension
import com.documentos.wms_beirario.model.recebimento.request.RecRequestCodBarras
import com.documentos.wms_beirario.repository.recebimento.ReceivementRepository
import com.documentos.wms_beirario.ui.recebimento.adapter.AdapterApontados
import com.documentos.wms_beirario.ui.recebimento.adapter.AdapterNaoApontados
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom

class RecebimentoActivity : AppCompatActivity() {

    private lateinit var mAdapterApontados: AdapterApontados
    private lateinit var mAdapterNaoApontados: AdapterNaoApontados
    private lateinit var mBinding: ActivityRecebimentoBinding
    private val mRetrofitService = ServiceApi.getInstance()
    private lateinit var mViewModel: RecebimentoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRecebimentoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mViewModel = ViewModelProvider(
            this, RecebimentoViewModel.RecebimentoFactory(
                ReceivementRepository(mRetrofitService)
            )
        )[RecebimentoViewModel::class.java]
        setupRecyclerViews()
        setupViews()
        setupToolbar()
        setupEditText()
        setupObservables()
    }

    private fun setupRecyclerViews() {
        mAdapterNaoApontados = AdapterNaoApontados()
        mAdapterApontados = AdapterApontados()
        mBinding.rvApontados.apply {
            layoutManager = LinearLayoutManager(this@RecebimentoActivity)
            adapter = mAdapterApontados
        }
        mBinding.rvNaoApontados.apply {
            layoutManager = LinearLayoutManager(this@RecebimentoActivity)
            adapter = mAdapterNaoApontados
        }
    }

    override fun onResume() {
        super.onResume()
        AppExtensions.visibilityProgressBar(mBinding.progressEditRec, visibility = false)
    }

    private fun setupViews() {
        mBinding.apply {
            buttonGroupRecebimento.visibility = View.INVISIBLE
            buttonLimpar.isEnabled = false
            buttonFinalizar.isEnabled = false
            txtInfListEmply.visibility = View.INVISIBLE
        }
    }

    private fun setupToolbar() {
        mBinding.toolbarRec.apply {
            setNavigationOnClickListener {
                onBackTransition()
            }
        }
    }

    private fun setupEditText() {
        mBinding.editRec.requestFocus()
        mBinding.editRec.addTextChangedListener { qrcodeReading ->
            if (qrcodeReading!!.isNotEmpty()) {
                AppExtensions.visibilityProgressBar(mBinding.progressEditRec, visibility = true)
                mViewModel.mReceivementRepository1(RecRequestCodBarras(qrcodeReading.toString()))
                mBinding.editRec.setText("")
                mBinding.editRec.requestFocus()
            }
        }
    }

    private fun setupObservables() {
        /**Sucess Leitura -->*/
        mViewModel.mSucessPostCodBarrasShow1.observe(this) { listRecebimento ->
            mAdapterApontados.submitList(listRecebimento.numerosSerieApontados)
            mAdapterNaoApontados.submitList(listRecebimento.numerosSerieNaoApontados)

        }
        /**Error Leitura -->*/
        mViewModel.mErrorShow.observe(this) { messageError ->
            vibrateExtension(500)
            CustomAlertDialogCustom().alertMessageErrorSimples(this, messageError)
        }
        /**Valida Progress -->*/
        mViewModel.mProgressValidShow.observe(this) { validProgress ->
            if (validProgress) {
                mBinding.progressEditRec.visibility = View.VISIBLE
            }
            mBinding.progressEditRec.visibility = View.INVISIBLE
        }
    }


}