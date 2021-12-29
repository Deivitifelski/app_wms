package com.documentos.wms_beirario.ui.productionreceipt.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.FragmentFilterReceiptProduct1Binding
import com.documentos.wms_beirario.repository.receiptproduct.ReceiptProductRepository
import com.documentos.wms_beirario.ui.productionreceipt.adapters.AdapterFilterReceiptProduct
import com.documentos.wms_beirario.ui.productionreceipt.viewModels.FilterReceiptProductViewModel2
import com.documentos.wms_beirario.utils.extensions.navAnimationCreate
import com.documentos.wms_beirario.utils.extensions.navAnimationCreateback
import com.example.coletorwms.constants.CustomMediaSonsMp3


class FilterReceiptProductFragment1 : Fragment(R.layout.fragment_filter_receipt_product1) {

    private var mBinding: FragmentFilterReceiptProduct1Binding? = null
    val binding get() = mBinding!!
    private lateinit var mViewModel: FilterReceiptProductViewModel2
    private val mService = ServiceApi.getInstance()
    private lateinit var mAdapter: AdapterFilterReceiptProduct
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private val mArgs: FilterReceiptProductFragment1Args by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSharedPreferences = CustomSharedPreferences(requireContext())
        mViewModel = ViewModelProvider(
            this, FilterReceiptProductViewModel2.FilterReceiptProductFactory(
                ReceiptProductRepository(mService)
            )
        )[FilterReceiptProductViewModel2::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFilterReceiptProduct1Binding.inflate(layoutInflater)
        setupTxtbottom()
        setupRecyclerView()
        setupToolbar()
        return binding.root
    }

    private fun setupRecyclerView() {
        mAdapter = AdapterFilterReceiptProduct { operatorClick ->
            CustomMediaSonsMp3().somClick(requireContext())
            val action =
                FilterReceiptProductFragment1Directions.clickGetPendences(operatorSelect = operatorClick)
            findNavController().navAnimationCreate(action)
        }
        mBinding!!.rvOperator1.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
        val list = mArgs.pendenceOperator
        mAdapter.update(list)
    }

    private fun setupTxtbottom() {
        /**SET NAME LOGADO DO SUPERVISOR --->*/
        val getNameSupervisor = mSharedPreferences.getString(CustomSharedPreferences.NOME_SUPERVISOR_LOGADO)
        mBinding!!.buttonNameSupervisorfilter1.text = getString(R.string.supervisor_name, getNameSupervisor)
    }

    private fun setupToolbar() {
        mBinding!!.toolbarSetOperator.setNavigationOnClickListener {
            CustomMediaSonsMp3().somClick(requireContext())
            val action = FilterReceiptProductFragment1Directions.onBack1(filterOperator = true)
            findNavController().navAnimationCreateback(action)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val action = FilterReceiptProductFragment1Directions.onBack1(filterOperator = true)
            findNavController().navAnimationCreateback(action)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }


}