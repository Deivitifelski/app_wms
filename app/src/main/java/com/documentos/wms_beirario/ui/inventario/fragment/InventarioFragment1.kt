package com.documentos.wms_beirario.ui.inventario.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.FragmentInventario1Binding
import com.documentos.wms_beirario.extensions.navAnimationCreate
import com.documentos.wms_beirario.extensions.onBackTransition
import com.documentos.wms_beirario.extensions.vibrateExtension
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import com.documentos.wms_beirario.ui.inventario.adapter.AdapterInventario1
import com.documentos.wms_beirario.ui.inventario.viewModel.PendingTaskInventoryViewModel1
import com.example.coletorwms.constants.CustomSnackBarCustom

class InventarioFragment1 : Fragment(R.layout.fragment_inventario1) {

    private lateinit var mAdapter: AdapterInventario1
    private var mBinding: FragmentInventario1Binding? = null
    private val _binding get() = mBinding!!
    private val mRetrofit = ServiceApi.getInstance()
    private lateinit var mViewModel: PendingTaskInventoryViewModel1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentInventario1Binding.inflate(inflater, container, false)
        setupObservables()
        setupRecyclerView()
        return _binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = ViewModelProvider(
            this, PendingTaskInventoryViewModel1.InventoryViewModelFactory(
                InventoryoRepository1(mRetrofit)
            )
        )[PendingTaskInventoryViewModel1::class.java]
    }


    override fun onResume() {
        super.onResume()
        setupToolbar()
        callApi()
    }

    private fun setupToolbar() {
        mBinding!!.toolbarInventario.apply {
            setNavigationOnClickListener {
                requireActivity().onBackTransition()
            }
        }
    }

    private fun callApi() {
        mViewModel.getPending1()
    }

    private fun setupRecyclerView() {
        /**CLIQUE EM UM ITEM--> */
        mAdapter = AdapterInventario1 { clickAdapter ->
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
            mAdapter.submitList(listPending)
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