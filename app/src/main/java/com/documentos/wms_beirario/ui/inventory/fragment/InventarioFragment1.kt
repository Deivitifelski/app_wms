package com.documentos.wms_beirario.ui.inventory.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.FragmentInventario1Binding
import com.documentos.wms_beirario.ui.TaskType.TipoTarefaActivity
import com.documentos.wms_beirario.ui.inventory.adapter.AdapterInventario1
import com.documentos.wms_beirario.ui.inventory.viewModel.PendingTaskInventoryViewModel1
import com.documentos.wms_beirario.utils.extensions.extensionStarBacktActivity
import com.documentos.wms_beirario.utils.extensions.navAnimationCreate
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import com.example.coletorwms.constants.CustomSnackBarCustom
import org.koin.android.viewmodel.ext.android.viewModel

class InventarioFragment1 : Fragment(R.layout.fragment_inventario1) {

    private lateinit var mAdapter: AdapterInventario1
    private var mBinding: FragmentInventario1Binding? = null
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private val _binding get() = mBinding!!
    private val mViewModel: PendingTaskInventoryViewModel1 by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentInventario1Binding.inflate(inflater, container, false)
        mSharedPreferences = CustomSharedPreferences(requireContext())
        setupObservables()
        setupRecyclerView()
        return _binding.root
    }

    override fun onResume() {
        super.onResume()
        setupToolbar()
        callApi()
    }

    private fun setupToolbar() {
        mBinding!!.toolbarInventario.apply {
            setNavigationOnClickListener {
                requireActivity().onBackTransitionExtension()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().extensionStarBacktActivity(TipoTarefaActivity())
            requireActivity().finish()
        }
    }

    private fun callApi() {
        mViewModel.getPending1()
    }

    private fun setupRecyclerView() {
        /**CLIQUE EM UM ITEM--> */
        mAdapter = AdapterInventario1 { clickAdapter ->
            mSharedPreferences.saveInt(CustomSharedPreferences.ID_INVENTORY, clickAdapter.id)
            val action =
                InventarioFragment1Directions.actionInventarioFragment1ToInventoryReadingFragment2(
                    clickAdapter
                )
            findNavController().navAnimationCreate(action)
        }
        mBinding!!.rvInventario1.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    private fun setupObservables() {
        /**LISTA VAZIA -->*/
        mViewModel.mValidadTxtShow.observe(viewLifecycleOwner, { validadTxt ->
            if (validadTxt) {
                mBinding!!.txtInfo.text = getString(R.string.denied_information)
            } else {
                mBinding!!.txtInfo.text = getString(R.string.click_select_item)
            }

        })
        /**LISTA COM ITENS -->*/
        mViewModel.mSucessShow.observe(viewLifecycleOwner, { listPending ->
            if (listPending.isEmpty()) {
                mBinding!!.lottie.visibility = View.VISIBLE
            } else {
                mBinding!!.lottie.visibility = View.INVISIBLE
                mAdapter.submitList(listPending)
            }

        })
        /**ERRO AO BUSCAR LISTA--> */
        mViewModel.mErrorShow.observe(viewLifecycleOwner) { messageError ->
            vibrateExtension(500)
            CustomSnackBarCustom().snackBarPadraoSimplesBlack(mBinding!!.root, messageError)
        }
        /**PROGRESSBAR--> */
        mViewModel.mValidaProgressShow.observe(viewLifecycleOwner, { validadProgress ->
            if (validadProgress) {
                mBinding!!.progressBarInventario.visibility = View.VISIBLE
            } else {
                mBinding!!.progressBarInventario.visibility = View.INVISIBLE
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

}