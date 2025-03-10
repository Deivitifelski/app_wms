package com.documentos.wms_beirario.ui.qualityControl.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.FragmentRejectedQualityBinding
import com.documentos.wms_beirario.model.qualityControl.BodySetPendenceQuality
import com.documentos.wms_beirario.model.qualityControl.Rejeitado
import com.documentos.wms_beirario.repository.qualityControl.QualityControlRepository
import com.documentos.wms_beirario.ui.qualityControl.activity.QualityControlActivity
import com.documentos.wms_beirario.ui.qualityControl.activity.QualityControlActivity.Companion.REQUISICAO
import com.documentos.wms_beirario.ui.qualityControl.adapter.AdapterQualityControlReject
import com.documentos.wms_beirario.ui.qualityControl.viewModel.QualityControlViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.tsuryo.swipeablerv.SwipeLeftRightCallback
import kotlinx.coroutines.launch


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
    private lateinit var mDialog: Dialog
    private lateinit var token: String
    private var idArmazem: Int = 0
    private lateinit var sharedPreferences: CustomSharedPreferences

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
        sharedPreferences = CustomSharedPreferences(requireContext())
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        mDialog = CustomAlertDialogCustom().progress(requireActivity())
        mDialog.hide()
        mInterface = context as InterfacePending
        mAlert = CustomAlertDialogCustom()
        mViewModel = ViewModelProvider(
            this,
            QualityControlViewModel.QualityControlViewModelFactory1(QualityControlRepository())
        )[QualityControlViewModel::class.java]
    }

    /**SWIPE PARA SETAR COMO PENDENTE --> */
    private fun setSwip() {

        binding.rvApproved.setListener(object : SwipeLeftRightCallback.Listener {
            override fun onSwipedLeft(position: Int) {
                //Pendente -->
                if (REQUISICAO == null) {
                    lifecycleScope.launch {
                        mDialog.show()
                        val body = BodySetPendenceQuality(
                            sequencial = list[position].sequencial.toString(),
                            idTarefa = QualityControlActivity.ID_TAREFA_CONTROL_QUALITY
                        )
                        mViewModel.setPendente(body, idArmazem, token)
                    }
                } else {
                    mAdapter.notifyItemChanged(position)
                    mAlert.alertMessageAtencao(
                        context = requireContext(),
                        message = "Requisição já gerada:\n$REQUISICAO",
                    )
                }
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
            //CASO SUCESSO NOTIFICA A ACTIVITY PARA CHAMAR O ENDPOINT 1 QUE BUSCA AS TAREFAS -->
            mSucessPendentesShow.observe(requireActivity()) { sucess ->
                binding.progressSetReject.visibility = View.INVISIBLE
                mInterface.setPendingReject(set = true)
            }
            //Erro Banco -->
            mErrorHttpShow.observe(requireActivity()) { error ->
                mAdapter.notifyDataSetChanged()
                mDialog.hide()
                mAlert.alertMessageErrorSimples(requireContext(), error, 10000)
            }
            //Error Geral -->
            mErrorAllShow.observe(requireActivity()) { error ->
                mAdapter.notifyDataSetChanged()
                mDialog.hide()
                mAlert.alertMessageErrorSimples(requireContext(), error, 10000)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
        mDialog.dismiss()
    }

}