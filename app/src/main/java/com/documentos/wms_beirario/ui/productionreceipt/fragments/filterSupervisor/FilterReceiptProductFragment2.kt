package com.documentos.wms_beirario.ui.productionreceipt.fragments.filterSupervisor

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
import com.documentos.wms_beirario.databinding.FragmentFilterReceiptProduct2Binding
import com.documentos.wms_beirario.repository.receiptproduct.ReceiptProductRepository
import com.documentos.wms_beirario.ui.productionreceipt.adapters.AdapterReceiptProduct1
import com.documentos.wms_beirario.ui.productionreceipt.viewModels.FilterReceiptProductViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.navAnimationCreate
import com.documentos.wms_beirario.utils.extensions.navAnimationCreateback
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import org.koin.android.viewmodel.ext.android.viewModel

class FilterReceiptProductFragment2 : Fragment() {

    private var mBinding: FragmentFilterReceiptProduct2Binding? = null
    val binding get() = mBinding!!
    private lateinit var mAdapter: AdapterReceiptProduct1
    private val mViewModel: FilterReceiptProductViewModel2 by viewModel()
    private val mArgs: FilterReceiptProductFragment2Args by navArgs()
    private lateinit var mSharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSharedPreferences = CustomSharedPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFilterReceiptProduct2Binding.inflate(layoutInflater)
        setupToolbar()
        setRecyclerView()
        setupObervable()
        setupTxtInformation()
        callApiPendenceOperator()
        return binding.root
    }

    private fun setupToolbar() {
        val getNameSupervisor =
            mSharedPreferences.getString(CustomSharedPreferences.NOME_SUPERVISOR_LOGADO)
        mBinding!!.toolbarSetOperator.subtitle =
            getString(R.string.supervisor_name, getNameSupervisor)
        mBinding!!.toolbarSetOperator.setNavigationOnClickListener {
            val action = FilterReceiptProductFragment2Directions.clickonBack(
                true,
                mArgs.arrayOperatorPendences
            )
            findNavController().navAnimationCreateback(action)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val action = FilterReceiptProductFragment2Directions.clickonBack(
                true,
                mArgs.arrayOperatorPendences
            )
            findNavController().navAnimationCreateback(action)
        }

    }

    /**SET ADAPTER E CLICK NO ITEM PENDENTE PARA FINALIZAR--->*/
    private fun setRecyclerView() {
        mAdapter = AdapterReceiptProduct1 { itemClick ->
            val action = FilterReceiptProductFragment2Directions.clickfinish(
                receiptProduct = itemClick,
                arrayOperadores = mArgs.arrayOperatorPendences,
                operadorSelect = mArgs.operatorSelect
            )
            findNavController().navAnimationCreate(action)
        }
        mBinding!!.rvOperator1.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    /**CHAMADA API--->*/
    private fun callApiPendenceOperator() {
        val idOperador = mArgs.operatorSelect.idOperadorColetor.toString()
        mViewModel.getReceipt1(filtrarOperador = true, mIdOperador = idOperador)
    }

    private fun setupTxtInformation() {
        mBinding!!.txtInfOperator22.text =
            getString(R.string.Pendence_operator, mArgs.operatorSelect.usuario)
    }

    private fun setupObervable() {
        mViewModel.mSucessReceiptShow.observe(viewLifecycleOwner) { listSucess ->
            mAdapter.submitList(listSucess)
        }
        mViewModel.mErrorReceiptShow.observe(viewLifecycleOwner) { messageError ->
            vibrateExtension(500)
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), messageError)
        }
        mViewModel.mValidaProgressReceiptShow.observe(viewLifecycleOwner) { validProgress ->
            if (validProgress) mBinding!!.progressOperatorfilter2.visibility = View.VISIBLE
            else mBinding!!.progressOperatorfilter2.visibility = View.INVISIBLE
        }
    }


}