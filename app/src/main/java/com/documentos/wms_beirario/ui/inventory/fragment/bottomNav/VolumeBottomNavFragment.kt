package com.documentos.wms_beirario.ui.inventory.fragment.bottomNav

import android.app.AlertDialog
import android.app.Dialog
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
import com.documentos.wms_beirario.model.inventario.ResponseListRecyclerView
import com.documentos.wms_beirario.model.inventario.VolumesResponseInventarioItem
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.ui.inventory.adapter.AdapterInventoryClickVolume
import com.documentos.wms_beirario.ui.inventory.viewModel.VolumePrinterViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.example.coletorwms.constants.CustomMediaSonsMp3
import org.koin.android.viewmodel.ext.android.viewModel


class VolumeBottomNavFragment : Fragment() {

    private lateinit var mAdapter: AdapterInventoryClickVolume
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mArgs: ResponseListRecyclerView
    private var mBinding: FragmentVolumeBottomNavBinding? = null
    private val mPrinterConnection = PrinterConnection()
    private val mViewModel: VolumePrinterViewModel by viewModel()
    private val _binding get() = mBinding!!
    private lateinit var mDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSharedPreferences = CustomSharedPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentVolumeBottomNavBinding.inflate(inflater, container, false)
        getArgs()
        setObservables()
        setupClickPrinter()
        mDialog = CustomAlertDialogCustom().progress(requireContext())
        mDialog.hide()
        return _binding.root
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
        mArgs =
            requireArguments().getSerializable("VOLUME_SHOW_ANDRESS") as ResponseListRecyclerView
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
            mDialog.show()
            //CHAMADA API DA IMPRESSORA -->
            if (SetupNamePrinter.applicationPrinterAddress.isEmpty()) {
                mDialog.hide()
                alertSet.dismiss()
                CustomAlertDialogCustom().alertSelectPrinter(requireContext())
            } else {
                mViewModel.printer(itemPrinter.id)
                alertSet.dismiss()
            }

        }
        alertSet.show()
    }

    /**RESPOSTAS DO CLICK NA IMPRESSORA -->*/
    private fun setObservables() {
        mViewModel.mSucessVolShow.observe(viewLifecycleOwner) { etiqueta ->
            mDialog.hide()
            mPrinterConnection.printZebra(
                etiqueta.toString(),
                SetupNamePrinter.applicationPrinterAddress
            )
        }

        mViewModel.mErrorVolShow.observe(viewLifecycleOwner) { messageError ->
            mDialog.hide()
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), messageError)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}
