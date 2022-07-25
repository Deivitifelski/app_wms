package com.documentos.wms_beirario.ui.etiquetagem.activitys

import AdapterLabelingRequisicao
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityEtiquetagemPendenciaRequisicaoBinding
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import com.documentos.wms_beirario.ui.etiquetagem.adapter.AdapterLabelingPendencyNF
import com.documentos.wms_beirario.ui.etiquetagem.viewmodel.LabelingPendencyNfViewModel
import com.documentos.wms_beirario.ui.etiquetagem.viewmodel.LabelingPendingRequisicaoViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation

class EtiquetagemPendenciaRequisicaoActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityEtiquetagemPendenciaRequisicaoBinding
    private lateinit var mViewModel: LabelingPendingRequisicaoViewModel
    private lateinit var mAdapter: AdapterLabelingRequisicao
    private lateinit var mAlert: CustomAlertDialogCustom


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityEtiquetagemPendenciaRequisicaoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        initViewModel()
        setToolbar()
        initRv()
        callApi()
        setObservable()
    }

    private fun setToolbar() {
        mBinding.toolbar7.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }


    private fun initViewModel() {
        mAlert = CustomAlertDialogCustom()
        mViewModel = ViewModelProvider(
            this,
            LabelingPendingRequisicaoViewModel.LabelingViewModelRequisicaoFactory(
                EtiquetagemRepository()
            )
        )[LabelingPendingRequisicaoViewModel::class.java]
    }

    private fun initRv() {
        mAdapter = AdapterLabelingRequisicao()
        mBinding.rvPendencyRequisicao.apply {
            layoutManager = LinearLayoutManager(this@EtiquetagemPendenciaRequisicaoActivity)
            adapter = mAdapter
        }
    }

    private fun callApi() {
        mViewModel.getRequisicao()
    }


    private fun setObservable() {
        mViewModel.apply {
            //SUCESSO ->
            mSucessShow.observe(this@EtiquetagemPendenciaRequisicaoActivity) { listSucess ->
                if (listSucess.isEmpty()) {
                    mBinding.linearTopTotais.visibility = View.INVISIBLE
                    mBinding.txtInfo.visibility = View.VISIBLE
                    mBinding.linearTopTotais.visibility = View.INVISIBLE
                } else {
                    var totalPendencias = 0
                    var totalNotas = 0
                    listSucess.forEach { list ->
                        totalPendencias += list.quantidadeVolumesPendentes
                        totalNotas += list.quantidadeVolumes
                    }
                    mBinding.totalPedidos.text = listSucess.size.toString()
                    mBinding.totalVolumes.text = totalNotas.toString()
                    mBinding.totalPendencias.text = totalPendencias.toString()
                    mBinding.txtInfo.visibility = View.INVISIBLE
                    mAdapter.submitList(listSucess)
                }
            }
            //ERRO ->
            mErrorShow.observe(this@EtiquetagemPendenciaRequisicaoActivity) { error ->
                mAlert.alertMessageErrorSimples(this@EtiquetagemPendenciaRequisicaoActivity, error)
            }
            //ERRO all ->
            mErrorShow.observe(this@EtiquetagemPendenciaRequisicaoActivity) { errorAll ->
                mAlert.alertMessageErrorSimples(
                    this@EtiquetagemPendenciaRequisicaoActivity,
                    errorAll
                )
            }
            //progress ->
            mProgressShow.observe(this@EtiquetagemPendenciaRequisicaoActivity) { progress ->
                mBinding.progressReq.isVisible = progress
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

}