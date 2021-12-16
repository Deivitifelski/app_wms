package com.documentos.wms_beirario.ui.etiquetagem.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.LabelingFragment2FragmentBinding
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.navAnimationCreate
import com.documentos.wms_beirario.utils.extensions.onBackTransition
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import com.documentos.wms_beirario.ui.etiquetagem.adapter.AdapterPending2
import com.documentos.wms_beirario.ui.etiquetagem.viewmodel.LabelingPendingFragment2ViewModel
import com.example.coletorwms.constants.CustomSnackBarCustom

class LabelingPendingFragment2 : Fragment() {

    private var mBinding: LabelingFragment2FragmentBinding? = null
    val binding get() = mBinding!!
    private lateinit var mViewModel: LabelingPendingFragment2ViewModel
    private val mRetrofit = ServiceApi.getInstance()
    private lateinit var mAdapter: AdapterPending2
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = LabelingFragment2FragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mViewModel = ViewModelProvider(
            this, LabelingPendingFragment2ViewModel.PendingLabelingFactoryBarCode(
                EtiquetagemRepository(mRetrofit)
            )
        )[LabelingPendingFragment2ViewModel::class.java]
        AppExtensions.visibilityProgressBar(mBinding!!.progress, visibility = true)
        setupRecyclerView()
        mViewModel.getLabeling()
        setObservables()
        setToolbar()
    }

    private fun setToolbar() {
        mBinding!!.toolbar.apply {
            setNavigationOnClickListener {
                requireActivity().onBackTransition()
            }
        }
    }

    private fun setObservables() {
        mViewModel.mSucessShow.observe(viewLifecycleOwner) { listSucess ->
            mAdapter.submitList(listSucess)
        }
        mViewModel.mErrorShow.observe(viewLifecycleOwner) { messageError ->
            CustomSnackBarCustom().snackBarErrorSimples(mBinding!!.root, messageError)
        }
        mViewModel.mValidProgressShow.observe(viewLifecycleOwner) { validProgress ->
            if (validProgress) mBinding!!.progress.visibility = View.VISIBLE else
                mBinding!!.progress.visibility = View.INVISIBLE
        }
    }

    private fun setupRecyclerView() {
        //CLICK PROXIMO FRAGMENT -->
        mAdapter = AdapterPending2 { itemPendingClick ->
            val action = LabelingPendingFragment2Directions.clickItem(itemPendingClick)
            findNavController().navAnimationCreate(action)
        }
        mBinding!!.rvEtiquetagem2.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }
}