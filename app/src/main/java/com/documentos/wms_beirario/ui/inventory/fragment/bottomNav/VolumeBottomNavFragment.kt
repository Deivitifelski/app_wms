package com.documentos.wms_beirario.ui.inventory.fragment.bottomNav

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.FragmentVolumeBottomNavBinding
import com.documentos.wms_beirario.databinding.LayoutCustomImpressoraBinding
import com.documentos.wms_beirario.model.inventario.*
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import com.documentos.wms_beirario.ui.configuracoes.temperature.ControlActivity
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.ui.inventory.adapter.AdapterInventoryClickVolume
import com.documentos.wms_beirario.ui.inventory.viewModel.CreateVoidInventoryViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import com.example.coletorwms.constants.CustomMediaSonsMp3


class VolumeBottomNavFragment : Fragment() {

    private lateinit var mAdapter: AdapterInventoryClickVolume
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mArgs: ResponseListRecyclerView
    private var mId_inventory: Int? = null
    private var mBinding: FragmentVolumeBottomNavBinding? = null
    private val mRetrofit = ServiceApi.getInstance()
    private lateinit var mViewModel: CreateVoidInventoryViewModel
    private val mPrinterConnection = PrinterConnection()
    private val _binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSharedPreferences = CustomSharedPreferences(requireContext())
        mViewModel = ViewModelProvider(
            this,
            CreateVoidInventoryViewModel.InventoryVCreateVoidiewModelFactoryBarCode(
                InventoryoRepository1(mRetrofit)
            )
        )[CreateVoidInventoryViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentVolumeBottomNavBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onResume() {
        super.onResume()
        getArgs()
        setupClickPrinter()
        setObservables()
    }

    private fun setRecyclerView(mList: ResponseListRecyclerView) {
        this.mAdapter = AdapterInventoryClickVolume()
        mBinding!!.rvVolumeInventory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
        if (mList.volumes.isNullOrEmpty()) {
            AppExtensions.visibilityTxt(mBinding!!.txtInf, visibility = true)
        } else {
            AppExtensions.visibilityTxt(mBinding!!.txtInf, visibility = false)
            mAdapter.submitList(mList.volumes)
        }

    }

    private fun setupClickPrinter() {
        mAdapter.listners = { itemPrinter ->
            alertPrinterTag(getString(R.string.want_to_reprint_the_label), itemPrinter)
        }
    }

    private fun getArgs() {
        mArgs = requireArguments().getSerializable("VOLUME_SHOW_ANDRESS") as ResponseListRecyclerView
        mId_inventory = mSharedPreferences.getInt(CustomSharedPreferences.ID_INVENTORY)
        setRecyclerView(mArgs)
    }

    private fun alertPrinterTag(title: String, itemPrinter: VolumesResponseInventarioItem) {
        AppExtensions.vibrar(requireContext())
        CustomMediaSonsMp3().somAtencao(requireContext())
        val alert = AlertDialog.Builder(requireContext())
        val binding = LayoutCustomImpressoraBinding.inflate(LayoutInflater.from(requireContext()))
        alert.setView(binding.root)
        val alertSet = alert.create()
        binding.textImpressoar1.text = title
        binding.buttonNaoImpressora1.setOnClickListener {
            alertSet.dismiss()
        }
        binding.buttonSimImpressora1.setOnClickListener {
            /**
             * VALIDAR O ENDPOINT QUE FAZ BUSCA DA IMPRESSAO -->
             * */
            mViewModel.postPrinter(
                itemPrinter.idEndereco,
                idInventario = mId_inventory?:0,
                numeroContagem = itemPrinter.numeroContagem,
                createVoidPrinter = CreateVoidPrinter(
                    codigoCorrugado = itemPrinter.codigoCorrugado,
                    combinacoes = null
                )
            )
            alertSet.dismiss()
        }
        alertSet.show()
    }

    private fun setObservables() {
        mViewModel.mSucessPrinterShow.observe(viewLifecycleOwner) { etiqueta ->
            mPrinterConnection.printZebra(
                ControlActivity.mSettings + etiqueta,
                SetupNamePrinter.applicationPrinterAddress
            )
        }
        mViewModel.mErrorPrinterShow.observe(viewLifecycleOwner) { messageError ->
            vibrateExtension(500)
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), messageError)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}
