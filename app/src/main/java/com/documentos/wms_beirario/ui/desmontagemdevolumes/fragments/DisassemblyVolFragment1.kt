package com.documentos.wms_beirario.ui.desmontagemdevolumes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.FragmentDisassemblyVol1Binding
import com.documentos.wms_beirario.ui.desmontagemdevolumes.adapter.AdapterDisassembly1
import com.documentos.wms_beirario.ui.desmontagemdevolumes.vielmodel.DisassemblyViewModel1
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import org.koin.android.viewmodel.ext.android.viewModel

class DisassemblyVolFragment1 : Fragment() {

    private lateinit var mAdapter: AdapterDisassembly1
    private var mBinding: FragmentDisassemblyVol1Binding? = null
    val binding get() = mBinding!!
    private val mViewModel: DisassemblyViewModel1 by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDisassemblyVol1Binding.inflate(layoutInflater)
        setToolbar()
        setupRecyclerView()
        calApi()
        setupObservables()
        return binding.root
    }

    private fun setToolbar() {
        mBinding!!.toolbarDesmontagemvol1.setNavigationOnClickListener {
            requireActivity().onBackTransitionExtension()
        }
    }

    private fun setupRecyclerView() {
        mAdapter = AdapterDisassembly1()
        mBinding!!.rvDesmontagemVol1.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    private fun calApi() {
        mViewModel.getDisassembly1()
    }

    private fun setupObservables() {
        mViewModel.mSucessShow.observe(viewLifecycleOwner) { listSucess ->
            if (listSucess.isEmpty()) {
                mBinding!!.txtInf.visibility = View.VISIBLE
                mBinding!!.linearDesmontagemVol1.visibility = View.INVISIBLE
                mBinding!!.imageEmply.visibility = View.VISIBLE
            } else {
                mBinding!!.txtInf.visibility = View.INVISIBLE
                mBinding!!.imageEmply.visibility = View.INVISIBLE
                mBinding!!.linearDesmontagemVol1.visibility = View.VISIBLE
                mAdapter.submitList(listSucess)
            }

        }
        mViewModel.mErrorShow.observe(viewLifecycleOwner) { messageError ->
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), messageError)
        }
        mViewModel.mValidProgressShow.observe(viewLifecycleOwner) { validProgress ->
            if (validProgress) mBinding!!.progressDesmontagemVol1.visibility = View.VISIBLE
            else
                mBinding!!.progressDesmontagemVol1.visibility = View.INVISIBLE
        }
    }
}
