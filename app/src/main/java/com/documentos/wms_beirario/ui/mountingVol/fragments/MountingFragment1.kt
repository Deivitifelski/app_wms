package com.documentos.wms_beirario.ui.mountingVol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.FragmentMounting1Binding
import com.documentos.wms_beirario.ui.TaskType.TipoTarefaActivity
import com.documentos.wms_beirario.ui.mountingVol.adapters.AdapterMounting1
import com.documentos.wms_beirario.ui.mountingVol.viewmodels.MountingVolViewModel1
import com.documentos.wms_beirario.utils.extensions.extensionStarBacktActivity
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import com.example.coletorwms.constants.CustomSnackBarCustom
import org.koin.android.viewmodel.ext.android.viewModel


class MountingFragment1 : Fragment() {

    private lateinit var mAdapter: AdapterMounting1
    private var mBinding: FragmentMounting1Binding? = null
    val binding get() = mBinding!!
    private val mViewModel: MountingVolViewModel1 by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMounting1Binding.inflate(layoutInflater)
        setToolbar()
        setupRecyclerView()
        callApi()
        setupObservables()
        return binding.root
    }

    private fun callApi() {
        mViewModel.getMounting1()
    }

    private fun setToolbar() {
        mBinding!!.toolbarMontagemdevolumes1.setNavigationOnClickListener {
            requireActivity().onBackTransitionExtension()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().extensionStarBacktActivity(TipoTarefaActivity())
            requireActivity().finish()
        }
    }

    private fun setupRecyclerView() {
        mAdapter = AdapterMounting1 { itemClick ->
            Toast.makeText(requireContext(), itemClick.nome, Toast.LENGTH_SHORT).show()
        }
        mBinding!!.rvMontagem1.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    private fun setupObservables() {
        mViewModel.mShowShow.observe(viewLifecycleOwner) { listSucess ->
            if (listSucess.isEmpty()) {
                mBinding!!.lotie.visibility = View.VISIBLE
                mBinding!!.txtInf.visibility = View.VISIBLE
            } else {
                mBinding!!.txtInf.visibility = View.INVISIBLE
                mBinding!!.lotie.visibility = View.INVISIBLE
                mAdapter.submitList(listSucess)
            }
        }
        mViewModel.mErrorShow.observe(viewLifecycleOwner) { messageError ->
            CustomSnackBarCustom().snackBarSimplesBlack(mBinding!!.root, messageError)
        }
        mViewModel.mValidaProgressShow.observe(viewLifecycleOwner) { validProgress ->
            if (validProgress) mBinding!!.progressBarInitMontagem1.visibility = View.VISIBLE
            else
                mBinding!!.progressBarInitMontagem1.visibility = View.INVISIBLE
        }
    }


}
