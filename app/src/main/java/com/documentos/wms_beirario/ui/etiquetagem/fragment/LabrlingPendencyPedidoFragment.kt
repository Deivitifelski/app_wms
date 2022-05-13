package com.documentos.wms_beirario.ui.etiquetagem.fragment

import com.documentos.wms_beirario.ui.etiquetagem.viewmodel.LabelingPendencyNfViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.FragmentLabrlingPendencyNfBinding
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import com.documentos.wms_beirario.ui.etiquetagem.adapter.AdapterLabelingPendencyNF
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension


class LabrlingPendencyPedidoFragment : Fragment() {
    private var mBinding: FragmentLabrlingPendencyNfBinding? = null
    val binding get() = mBinding!!
    private lateinit var mAdapter: AdapterLabelingPendencyNF
    private lateinit var mViewModel: LabelingPendencyNfViewModel
    private val TAG = "LabelingPendingFragmentPedido"
    private lateinit var mAlertDialogCustom: CustomAlertDialogCustom
    private lateinit var mSons: CustomMediaSonsMp3
    private var mTotalPed: Int = 0
    private var mTotalPen: Int = 0
    private var mTotalVol: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentLabrlingPendencyNfBinding.inflate(layoutInflater)
        setToolbar()
        initViewModel()
        initRv()
        initConst()
        getPendency()
        setObservable()
        setItensTop()
        return binding.root
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            LabelingPendencyNfViewModel.LabelingPendencyViewModelFactory(EtiquetagemRepository())
        )[LabelingPendencyNfViewModel::class.java]
    }

    private fun setToolbar() {
        mBinding!!.toolbar.apply {
            setNavigationOnClickListener {
                requireActivity().onBackTransitionExtension()
            }
        }
    }


    private fun initRv() {
        mAdapter = AdapterLabelingPendencyNF()
        mBinding!!.rvLabelingPendencyNf.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    private fun initConst() {
        mAlertDialogCustom = CustomAlertDialogCustom()
        mSons = CustomMediaSonsMp3()
    }

    private fun setObservable() {
        /**ERROR -->*/
        mViewModel.mErrorShow.observe(viewLifecycleOwner, { error ->
            mBinding?.linearTopTotais?.visibility = View.INVISIBLE
            mBinding?.txtEmply?.visibility = View.VISIBLE
            mBinding?.txtEmply?.text = error
        })
        /**SUCESS -->*/
        mViewModel.mSucessShow.observe(viewLifecycleOwner, { sucess ->
            try {
                if (sucess.isEmpty()) {
                    mBinding!!.totalPedidos.text = "0"
                    mBinding!!.totalPendencias.text = "0"
                    mBinding!!.totalVolumes.text = "0"
                } else {
                    sucess.forEach { itens ->
                        mTotalPen += itens.quantidadePendente
                        mTotalVol += itens.quantidadeVolumes
                    }
                    mBinding!!.totalPedidos.text = sucess.size.toString()
                    mBinding!!.totalPendencias.text = mTotalPen.toString()
                    mBinding!!.totalVolumes.text = mTotalVol.toString()
                    mAdapter.submitList(sucess)
                }

            } catch (e: Exception) {
                mAlertDialogCustom.alertMessageErrorSimples(
                    requireContext(),
                    "Erro ao carregar lista!",
                    2000
                )
            }
        })

        mViewModel.mErrorAllShow.observe(viewLifecycleOwner, { errorAll ->
            mAlertDialogCustom.alertMessageErrorSimples(requireContext(), errorAll, 2000)
        })

        mViewModel.mValidProgressShow.observe(requireActivity(), { progress ->
            mBinding!!.progressInit.isVisible = progress
        })

    }

    private fun getPendency() {
        mViewModel.getLabelingNf()
    }

    private fun setItensTop() {
        mBinding!!.totalPedidos.text = mTotalPed.toString()
        mBinding!!.totalPendencias.text = mTotalPed.toString()
        mBinding!!.totalVolumes.text = mTotalVol.toString()
    }


}