package com.documentos.wms_beirario.ui.productionreceipt.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.ReceiptProductFragment2Binding
import com.documentos.wms_beirario.repository.receiptproduct.ReceiptProductRepository
import com.documentos.wms_beirario.ui.productionreceipt.adapters.AdapterReceiptProduct2
import com.documentos.wms_beirario.ui.productionreceipt.viewModels.ReceiptProductViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.navAnimationCreateback
import com.documentos.wms_beirario.utils.extensions.vibrateExtension

class ReceiptProductFragment2 : Fragment(R.layout.receipt_product_fragment2) {

    private var mBinding: ReceiptProductFragment2Binding? = null
    val binding get() = mBinding!!
    private val mService = ServiceApi.getInstance()
    private lateinit var mAdapter: AdapterReceiptProduct2
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mViewModel: ReceiptProductViewModel2
    private val mArgs: ReceiptProductFragment2Args by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSharedPreferences = CustomSharedPreferences(requireContext())
        mViewModel = ViewModelProvider(
            this, ReceiptProductViewModel2.ReceiptProductFactory2(
                ReceiptProductRepository(mService)
            )
        )[ReceiptProductViewModel2::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = ReceiptProductFragment2Binding.inflate(layoutInflater)
        setREcyclerView()
        setupToolbar()
        callApi()
        setObservables()
        return binding.root
    }

    private fun setREcyclerView() {
        mAdapter = AdapterReceiptProduct2()
       mBinding!!.recyclerView.apply {
           layoutManager = LinearLayoutManager(requireContext())
           adapter = mAdapter
       }
    }

    private fun callApi() {
        val idOperador = mSharedPreferences.getString(CustomSharedPreferences.ID_OPERADOR)
        mViewModel.getItem(
            idOperador = idOperador.toString(),
            filtrarOperario = true,
            pedido = mArgs.responseClick.pedido
        )
    }

    private fun setupToolbar() {
        mBinding!!.toolbar2.apply {
            setNavigationOnClickListener {
                val action = ReceiptProductFragment2Directions.backFrag1()
                findNavController().navAnimationCreateback(action)
            }
            title = context.getString(R.string.order_receipt2_toolbar, mArgs.responseClick.pedido)
        }
    }

    private fun setObservables() {
        mViewModel.mSucessReceiptShow2.observe(viewLifecycleOwner) { listSucess ->
                     mAdapter.submitList(listSucess)
        }
        mViewModel.mErrorReceiptShow2.observe(viewLifecycleOwner) { messageError ->
            vibrateExtension(500)
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), messageError)
        }
        mViewModel.mValidaProgressReceiptShow2.observe(viewLifecycleOwner) { validProgress ->
            if (validProgress) AppExtensions.visibilityProgressBar(mBinding!!.progress)
            else AppExtensions.visibilityProgressBar(mBinding!!.progress, visibility = false)
        }
    }


}