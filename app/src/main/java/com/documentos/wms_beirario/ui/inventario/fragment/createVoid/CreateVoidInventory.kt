package com.documentos.wms_beirario.ui.inventario.fragment.createVoid

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.databinding.CustomLayoutRvSelectShoesBinding
import com.documentos.wms_beirario.databinding.FragmentAddVoidBinding
import com.documentos.wms_beirario.databinding.LayoutCorrugadoBinding
import com.documentos.wms_beirario.model.inventario.InventoryResponseCorrugados
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import com.documentos.wms_beirario.ui.inventario.adapter.AdapterCorrugadosInventory
import com.documentos.wms_beirario.ui.inventario.adapter.AdapterInventorySelectNum
import com.documentos.wms_beirario.ui.inventario.viewModel.CreateVoidInventoryViewModel
import com.example.coletorwms.constants.CustomSnackBarCustom
import com.google.android.material.bottomsheet.BottomSheetDialog


class CreateVoidInventory : Fragment() {


    private var mBinding: FragmentAddVoidBinding? = null
    private val _binding get() = mBinding!!
    private lateinit var mAdapterSelectNum: AdapterInventorySelectNum
    private lateinit var mAdapterAlertSelectNum: AdapterAlertSelectTamShoes
    private lateinit var mAdapterCorrugado: AdapterCorrugadosInventory
    private lateinit var mViewModel: CreateVoidInventoryViewModel
    private val mRetrofitService = RetrofitService.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAddVoidBinding.inflate(inflater, container, false)
        if (savedInstanceState == null) {
            setupRecyclerViewTam()
        }
        return _binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(
            this,
            CreateVoidInventoryViewModel.InventoryVCreateVoidiewModelFactoryBarCode(
                InventoryoRepository1(mRetrofitService)
            )
        )[CreateVoidInventoryViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        callCorrugadoApi()
    }


    private fun callCorrugadoApi() {
        mBinding!!.buttonSelectCorrugado.setOnClickListener {
            mViewModel.getCorrugados()
        }
        //Sucess ->
        mViewModel.mSucessCorrugados.observe(viewLifecycleOwner) { responseCorrugados ->
            alertCorrugados(responseCorrugados)
        }
        //Error ->
        mViewModel.mErrorCorrugados.observe(viewLifecycleOwner) { errorCorrugado ->
            CustomSnackBarCustom().snackBarErrorSimples(mBinding!!.root, errorCorrugado)
        }
    }

    private fun alertCorrugados(responseCorrugados: InventoryResponseCorrugados?) {
        val mAlert = BottomSheetDialog(requireContext(), R.style.BottomSheetStyle)
        val mbindingDialog = LayoutCorrugadoBinding.inflate(layoutInflater)
        mAlert.setContentView(mbindingDialog.root)
        mAlert.show()
        /**----------AO CLIQUE EM UM CORRUGADO NO ALERTDIALOG---------*/
        mAdapterCorrugado = AdapterCorrugadosInventory { responseItemClicado ->

            mBinding!!.buttonSelectCorrugado.text = getString(
                R.string.Corrugado_select,
                responseItemClicado.id,
                responseItemClicado.quantidadePares
            )
            mAlert.dismiss()
        }

        mbindingDialog.rvCorrugados.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = mAdapterCorrugado
        }
        mAdapterCorrugado.submitList(responseCorrugados)
    }

    /**RECYCLERVIEW TAMANHO -->*/
    private fun setupRecyclerViewTam() {
        //Funcao que retorna tamanho da lista de calçados -->
        mViewModel.getListTamShoes(first = 20, last = 45)
        //Click no tamanho selecionado -->
        mAdapterSelectNum = AdapterInventorySelectNum { itemClick ->
            alertSelectnum(itemClick)
        }
        mBinding!!.rvTamShoe.apply {
            this.layoutManager = GridLayoutManager(
                requireContext(), 2, GridLayoutManager.HORIZONTAL, false
            )
            this.adapter = mAdapterSelectNum
        }

        mViewModel.mSucessCreateListShow.observe(viewLifecycleOwner) { tamListShoes ->
            mAdapterSelectNum.update(tamListShoes)
        }
    }

    /**RECYCLERVIEW QUANTIDADE -->*/
    private fun alertSelectnum(itemClick: Int) {
        //Funcao que retorna tamanho da lista de calçados -->
        mViewModel.getListTamAlertShoes(first = 0, last = 10)
        val mAlert = AlertDialog.Builder(requireContext())
        val mbindingDialog = CustomLayoutRvSelectShoesBinding.inflate(layoutInflater)
        mAlert.setView(mbindingDialog.root)
        val mShow = mAlert.create()
        mShow.show()
        mAlert.setCancelable(true)
        mbindingDialog.txtInf.text = getString(R.string.selecione_qnt_para_numero, itemClick)
        mAdapterAlertSelectNum = AdapterAlertSelectTamShoes { clickNumSelectAlert ->
            CustomSnackBarCustom().snackBarSimplesBlack(
                mBinding!!.root,
                clickNumSelectAlert.toString()
            )
            mShow.dismiss()
        }
        mbindingDialog.rvSelectAlert.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapterAlertSelectNum
        }
        mViewModel.mSucessCreateListALertShow.observe(viewLifecycleOwner) { retornandolist ->
            mAdapterAlertSelectNum.updateAlertDialog(retornandolist)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }


}