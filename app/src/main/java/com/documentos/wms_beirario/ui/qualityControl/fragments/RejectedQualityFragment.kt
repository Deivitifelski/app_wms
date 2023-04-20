package com.documentos.wms_beirario.ui.qualityControl.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.FragmentRejectedQualityBinding
import com.documentos.wms_beirario.model.qualityControl.BodySetAprovadoQuality
import com.documentos.wms_beirario.model.qualityControl.Rejeitado
import com.documentos.wms_beirario.repository.qualityControl.QualityControlRepository
import com.documentos.wms_beirario.ui.qualityControl.activity.QualityControlActivity
import com.documentos.wms_beirario.ui.qualityControl.adapter.AdapterQualityControlReject
import com.documentos.wms_beirario.ui.qualityControl.viewModel.QualityControlViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.clearEdit
import com.tsuryo.swipeablerv.SwipeLeftRightCallback


class RejectedQualityFragment(private val list: MutableList<Rejeitado>) : Fragment() {


    interface InterfacePending {
        fun setPendingReject(set: Boolean)
    }

    private var mBinding: FragmentRejectedQualityBinding? = null
    private val binding get() = mBinding!!
    private lateinit var mAdapter: AdapterQualityControlReject
    private lateinit var mViewModel: QualityControlViewModel
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mInterface: InterfacePending

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentRejectedQualityBinding.inflate(layoutInflater)
        initConst()
        setRv()
        setSwip()
        setObserver()
        return mBinding!!.root
    }


    private fun initConst() {
        mInterface = context as InterfacePending
        mAlert = CustomAlertDialogCustom()
        mViewModel = ViewModelProvider(
            this,
            QualityControlViewModel.QualityControlViewModelFactory1(QualityControlRepository())
        )[QualityControlViewModel::class.java]
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

    private fun setRv() {
        mAdapter = AdapterQualityControlReject()
        binding.rvApproved.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
        mAdapter.submitList(list)
    }

    private fun setObserver() {
        mViewModel.apply {
            mSucessPendentesShow.observe(requireActivity()) { sucess ->
                mInterface.setPendingReject(set = true)
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