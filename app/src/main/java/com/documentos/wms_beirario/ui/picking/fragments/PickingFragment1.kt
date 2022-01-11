package com.documentos.wms_beirario.ui.picking.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.FragmentPicking1Binding
import com.documentos.wms_beirario.utils.extensions.extensionStarBacktActivity
import com.documentos.wms_beirario.utils.extensions.navAnimationCreate
import com.documentos.wms_beirario.repository.picking.PickingRepository
import com.documentos.wms_beirario.ui.TaskType.TipoTarefaActivity
import com.documentos.wms_beirario.ui.picking.adapters.AdapterPicking1
import com.documentos.wms_beirario.ui.picking.viewmodel.PickingViewModel1
import com.example.coletorwms.constants.CustomMediaSonsMp3
import com.example.coletorwms.constants.CustomSnackBarCustom
import org.koin.android.viewmodel.ext.android.viewModel

class PickingFragment1 : Fragment() {

    private lateinit var mAdapterPicking: AdapterPicking1
    private val mViewModel: PickingViewModel1 by viewModel()
    private var mBinding: FragmentPicking1Binding? = null
    val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPicking1Binding.inflate(layoutInflater)
        setRecyclerview()
        setToolbar()
        return binding.root
    }

    private fun setToolbar() {
        mBinding!!.toolbarPicking1.setNavigationOnClickListener {
            requireActivity().extensionStarBacktActivity(TipoTarefaActivity())
            requireActivity().finish()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().extensionStarBacktActivity(TipoTarefaActivity())
            requireActivity().finish()
        }
    }

    private fun setRecyclerview() {
        //Click no item -->
        mAdapterPicking = AdapterPicking1 { dadosItemClick ->
            CustomMediaSonsMp3().somClick(requireContext())
            val action = PickingFragment1Directions.clickItemPicking(dadosItemClick)
            findNavController().navAnimationCreate(action)
        }
        mBinding!!.rvPicking1.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapterPicking
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding!!.txtInformativoPicking1.text = ""
        callApi()
        setupObservable()
    }

    private fun setupObservable() {
        mViewModel.mSucessPickingShow.observe(viewLifecycleOwner) { listPicking ->
            if (listPicking.isEmpty()) {
                mBinding!!.lottie.visibility = View.VISIBLE
                mBinding!!.txtInformativoPicking1.text =
                    getString(R.string.picking_list_emply)
            } else {
                mBinding!!.txtInformativoPicking1.text = getString(R.string.select_area)
                mBinding!!.lottie.visibility = View.INVISIBLE
                mAdapterPicking.update(listPicking)
            }

        }
        mViewModel.mErrorPickingShow.observe(viewLifecycleOwner) { messageError ->
            CustomSnackBarCustom().snackBarErrorSimples(mBinding!!.root, messageError)
        }
        mViewModel.mValidProgressShow.observe(viewLifecycleOwner) { validProgress ->
            if (validProgress) mBinding!!.progressBarInitPicking1.visibility = View.VISIBLE
            else mBinding!!.progressBarInitPicking1.visibility = View.INVISIBLE
        }
    }

    private fun callApi() {
        mViewModel.getPicking()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}