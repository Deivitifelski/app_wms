package com.documentos.wms_beirario.ui.etiquetagem.activitys

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.ActivityEtiquetagemPedidoSnfBinding
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import com.documentos.wms_beirario.ui.etiquetagem.adapter.AdapterLabelingPendencyNF
import com.documentos.wms_beirario.ui.etiquetagem.viewmodel.LabelingPendencyNfViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar


class EtiquetagemPedidoActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityEtiquetagemPedidoSnfBinding
    private lateinit var mAdapter: AdapterLabelingPendencyNF
    private lateinit var mViewModel: LabelingPendencyNfViewModel
    private val TAG = "LabelingPendingFragmentPedido"
    private lateinit var mAlertDialogCustom: CustomAlertDialogCustom
    private lateinit var mSons: CustomMediaSonsMp3
    private var mTotalPed: Int = 0
    private var mTotalPen: Int = 0
    private var mTotalVol: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityEtiquetagemPedidoSnfBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        setToolbar()
        initViewModel()
        initRv()
        initConst()
        getPendency()
        setObservable()
        setItensTop()
    }


    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            LabelingPendencyNfViewModel.LabelingPendencyViewModelFactory(EtiquetagemRepository())
        )[LabelingPendencyNfViewModel::class.java]
    }

    private fun setToolbar() {
        mBinding.toolbar.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = getVersionNameToolbar()
        }
    }


    private fun initRv() {
        mAdapter = AdapterLabelingPendencyNF()
        mBinding.rvLabelingPendencyNf.apply {
            layoutManager = LinearLayoutManager(this@EtiquetagemPedidoActivity)
            adapter = mAdapter
        }
    }

    private fun initConst() {
        mAlertDialogCustom = CustomAlertDialogCustom()
        mSons = CustomMediaSonsMp3()
    }

    private fun setObservable() {
        /**ERROR -->*/
        mViewModel.mErrorShow.observe(this) { error ->
            mBinding.linearTopTotais.visibility = View.INVISIBLE
            mBinding.txtEmply.visibility = View.VISIBLE
            mBinding.txtEmply.text = error
        }
        /**SUCESS -->*/
        mViewModel.mSucessShow.observe(this) { sucess ->
            try {
                if (sucess.isEmpty()) {
                    mBinding.totalPedidos.text = "0"
                    mBinding.totalPendencias.text = "0"
                    mBinding.totalVolumes.text = "0"
                } else {
                    sucess.forEach { itens ->
                        mTotalPen += itens.quantidadePendente
                        mTotalVol += itens.quantidadeVolumes
                    }
                    mBinding.totalPedidos.text = sucess.size.toString()
                    mBinding.totalPendencias.text = mTotalPen.toString()
                    mBinding.totalVolumes.text = mTotalVol.toString()
                    mAdapter.submitList(sucess)
                }

            } catch (e: Exception) {
                mAlertDialogCustom.alertMessageErrorSimples(
                    this,
                    "Erro ao carregar lista!",
                    2000
                )
            }
        }

        mViewModel.mErrorAllShow.observe(this) { errorAll ->
            mAlertDialogCustom.alertMessageErrorSimples(this, errorAll)
        }

        mViewModel.mValidProgressShow.observe(this) { progress ->
            mBinding.progressInit.isVisible = progress
        }

    }

    private fun getPendency() {
        mViewModel.getLabelingNf()
    }

    private fun setItensTop() {
        mBinding.totalPedidos.text = mTotalPed.toString()
        mBinding.totalPendencias.text = mTotalPed.toString()
        mBinding.totalVolumes.text = mTotalVol.toString()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

}