package com.documentos.wms_beirario.ui.qualityControl.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.FragmentApprovedQualityBinding
import com.documentos.wms_beirario.model.qualityControl.Aprovado
import com.documentos.wms_beirario.model.qualityControl.BodySetAprovadoQuality
import com.documentos.wms_beirario.repository.qualityControl.QualityControlRepository
import com.documentos.wms_beirario.ui.qualityControl.activity.QualityControlActivity
import com.documentos.wms_beirario.ui.qualityControl.adapter.AdapterQualityControlApproved
import com.documentos.wms_beirario.ui.qualityControl.viewModel.QualityControlViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.tsuryo.swipeablerv.SwipeLeftRightCallback


class ApprovedQualityFragment(private val list: MutableList<Aprovado>) : Fragment() {


    interface InterfacePending {
        fun setPendingApproved(set: Boolean)
    }

    private var mBinding: FragmentApprovedQualityBinding? = null
    private val binding get() = mBinding!!
    private lateinit var mAdapter: AdapterQualityControlApproved
    private lateinit var mViewModel: QualityControlViewModel
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mInterface: InterfacePending

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentApprovedQualityBinding.inflate(layoutInflater)
        setRv()
        initConst()
        setSwip()
        setObserver()
        return mBinding!!.root
    }

    private fun initConst() {
        mInterface = context as InterfacePending
        mViewModel = ViewModelProvider(
            this,
            QualityControlViewModel.QualityControlViewModelFactory1(QualityControlRepository())
        )[QualityControlViewModel::class.java]
    }

    private fun setRv() {
        mAdapter = AdapterQualityControlApproved()
        binding.rvApproved.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
        mAdapter.submitList(list)
    }

    private fun setSwip() {
        binding.rvApproved.setListener(object : SwipeLeftRightCallback.Listener {
            override fun onSwipedLeft(position: Int) {
                //Pendente -->
                val body = BodySetAprovadoQuality(
                    codigoBarrasEan = list[position].sequencial.toString(),
                    idTarefa = QualityControlActivity.ID_TAREFA_CONTROL_QUALITY
                )
                mViewModel.setPendente(body)
            }

            override fun onSwipedRight(position: Int) {}
        })
    }

    private fun setObserver() {
        mViewModel.apply {
            mSucessPendentesShow.observe(requireActivity()) { sucess ->
                mInterface.setPendingApproved(set = true)
            }
            //Erro Banco -->
            mErrorHttpShow.observe(requireActivity()) { error ->
                mAlert.alertMessageErrorSimples(requireActivity(), error, 5000)
            }
            //Error Geral -->
            mErrorAllShow.observe(requireActivity()) { error ->
                mAlert.alertMessageErrorSimples(requireActivity(), error, 5000)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

}