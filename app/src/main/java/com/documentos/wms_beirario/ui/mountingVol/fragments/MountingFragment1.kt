package com.documentos.wms_beirario.ui.mountingVol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.FragmentMounting1Binding
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import com.documentos.wms_beirario.repository.mountingvol.MountingVolRepository
import com.documentos.wms_beirario.ui.mountingVol.adapters.AdapterMounting1
import com.documentos.wms_beirario.ui.mountingVol.viewmodels.MountingVolViewModel1
import com.example.coletorwms.constants.CustomSnackBarCustom


class MountingFragment1 : Fragment() {

    private lateinit var mAdapter: AdapterMounting1
    private var mBinding: FragmentMounting1Binding? = null
    val binding get() = mBinding!!
    private val service = ServiceApi.getInstance()
    private lateinit var mViewModel: MountingVolViewModel1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(
            this, MountingVolViewModel1.MontingVolViewModelFactory(
                MountingVolRepository(service)
            )
        )[MountingVolViewModel1::class.java]
    }

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
