package com.documentos.wms_beirario.ui.etiquetagem.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.Labeling3FragmentBinding
import com.documentos.wms_beirario.extensions.AppExtensions
import com.documentos.wms_beirario.extensions.onBackTransition
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequestModel3
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import com.documentos.wms_beirario.ui.etiquetagem.adapter.AdapterLabeling3
import com.documentos.wms_beirario.ui.etiquetagem.viewmodel.Labeling3ViewModel
import com.example.coletorwms.constants.CustomSnackBarCustom

class Labeling3Fragment3 : Fragment() {
    private var mBinding: Labeling3FragmentBinding? = null
    val binding get() = mBinding
    private lateinit var mViewModel: Labeling3ViewModel
    private val retrofitService = ServiceApi.getInstance()
    private lateinit var mAdapter: AdapterLabeling3
    private val mArgs: Labeling3Fragment3Args by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = Labeling3FragmentBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mViewModel = ViewModelProvider(
            this, Labeling3ViewModel.PendingLabelingFactoryBarCode3(
                EtiquetagemRepository(retrofitService)
            )
        )[Labeling3ViewModel::class.java]
        AppExtensions.visibilityProgressBar(mBinding!!.progress, visibility = true)
        setRecyclerView()
        setupToolbar()
        callApi()
        setupObservables()

    }

    private fun setRecyclerView() {
        mAdapter = AdapterLabeling3()
        mBinding!!.rvLabeling3.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    private fun setupToolbar() {
        mBinding!!.toolbar.apply {
            setNavigationOnClickListener {
                requireActivity().onBackTransition()
            }
        }
    }

    private fun callApi() {
        mViewModel.getLabeling3(
            etiquetagemRequestModel3 = EtiquetagemRequestModel3(
                mArgs.getDadosItemclick.empresa,
                mArgs.getDadosItemclick.filial,
                mArgs.getDadosItemclick.numeroNotaFiscal,
                mArgs.getDadosItemclick.serieNotaFiscal,
            )
        )
    }

    private fun setupObservables() {
        mViewModel.mErrorShow.observe(viewLifecycleOwner) { messageError ->
            CustomSnackBarCustom().snackBarErrorSimples(mBinding!!.root, messageError)
        }
        mViewModel.mSucessShow.observe(viewLifecycleOwner) { listSucess ->
            mAdapter.submitList(listSucess)
        }
        mViewModel.mValidProgressShow.observe(viewLifecycleOwner) { validProgress ->
            if (validProgress)
                mBinding!!.progress.visibility = View.VISIBLE
            else
                mBinding!!.progress.visibility = View.INVISIBLE
        }
    }


}