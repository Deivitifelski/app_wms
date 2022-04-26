package com.documentos.wms_beirario.ui.etiquetagem.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.LabelingFragment2FragmentBinding
import com.documentos.wms_beirario.ui.etiquetagem.adapter.AdapterPending2
import com.documentos.wms_beirario.ui.etiquetagem.viewmodel.LabelingPendingFragment2ViewModel
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.navAnimationCreate
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import com.example.coletorwms.constants.CustomSnackBarCustom
import org.koin.android.viewmodel.ext.android.viewModel

class LabelingPendingFragment2 : Fragment() {

    private var mBinding: LabelingFragment2FragmentBinding? = null
    val binding get() = mBinding!!
    private val mViewModel: LabelingPendingFragment2ViewModel by viewModel()
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

        AppExtensions.visibilityProgressBar(mBinding!!.progress, visibility = true)
        setupRecyclerView()
        mViewModel.getLabeling()
        setObservables()
        setToolbar()
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
                var totalPendencias : Int = 0
                var totalNotas : Int = 0
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