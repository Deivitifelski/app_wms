package com.documentos.wms_beirario.ui.etiquetagem.fragment

import com.documentos.wms_beirario.ui.etiquetagem.viewmodel.LabelingPendingFragment2ViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.LabelingFragment2FragmentBinding
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import com.documentos.wms_beirario.ui.etiquetagem.adapter.AdapterPending2
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.navAnimationCreate
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension

class LabelingPendingFragment2 : Fragment() {

    private var mBinding: LabelingFragment2FragmentBinding? = null
    val binding get() = mBinding!!
    private lateinit var mViewModel: LabelingPendingFragment2ViewModel
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
        initViewModel()
        setupRecyclerView()
        mViewModel.getLabeling()
        setObservables()
        setToolbar()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            LabelingPendingFragment2ViewModel.LabelingViewModelFactory(EtiquetagemRepository())
        )[LabelingPendingFragment2ViewModel::class.java]
    }

    private fun setToolbar() {
        mBinding!!.toolbar.apply {
            setNavigationOnClickListener {
                requireActivity().onBackTransitionExtension()
            }
        }
    }

    private fun setObservables() {
        mViewModel.mSucessShow.observe(viewLifecycleOwner) { listSucess ->
            if (listSucess.isEmpty()) {
                mBinding!!.linearTopTotais.visibility = View.INVISIBLE
                mBinding!!.lottiePendency2.visibility = View.VISIBLE
                mBinding!!.txtInf.visibility = View.VISIBLE
                mBinding!!.linearTopTotais.visibility = View.INVISIBLE
            } else {
                var totalPendencias: Int = 0
                var totalNotas: Int = 0
                listSucess.forEach { list ->
                    totalPendencias += list.quantidadeVolumesPendentes
                    totalNotas += list.quantidadeVolumes
                }
                mBinding!!.totalPedidos.text = listSucess.size.toString()
                mBinding!!.totalVolumes.text = totalNotas.toString()
                mBinding!!.totalPendencias.text = totalPendencias.toString()
                mBinding!!.txtInf.visibility = View.INVISIBLE
                mBinding!!.lottiePendency2.visibility = View.INVISIBLE
                mAdapter.submitList(listSucess)
            }

        }
        mViewModel.mErrorShow.observe(viewLifecycleOwner) { messageError ->
            CustomSnackBarCustom().snackBarErrorSimples(mBinding!!.root, messageError)
        }
        mViewModel.mProgressShow.observe(viewLifecycleOwner) { validProgress ->
            if (validProgress) mBinding!!.progress.visibility = View.VISIBLE else
                mBinding!!.progress.visibility = View.INVISIBLE
        }
        mViewModel.mErrorAllShow.observe(viewLifecycleOwner) { messageError ->
            CustomSnackBarCustom().snackBarErrorSimples(mBinding!!.root, messageError)
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