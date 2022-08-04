package com.documentos.wms_beirario.ui.inventory.activitys.bottomNav

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
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
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class VolumeBottomNavFragment : Fragment() {

    private lateinit var mAdapter: AdapterInventoryClickVolume
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mArgs: ResponseListRecyclerView
    private var mBinding: FragmentVolumeBottomNavBinding? = null
    private lateinit var mPrinterConnection: PrinterConnection
    private lateinit var mViewModel: VolumePrinterViewModel
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
        initViewModel()
        setObservables()
        setupClickPrinter()
        mDialog = CustomAlertDialogCustom().progress(requireContext())
        mDialog.hide()
        return _binding.root
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this, VolumePrinterViewModel.VolumePrinterModelFactory(
                InventoryoRepository1()
            )
        )[VolumePrinterViewModel::class.java]
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
            //VERIFICA SE TEM UMA IMPRESSORA SELECIONADA,DIALOG DIFERENTES -->
            if (SetupNamePrinter.mNamePrinterString.isEmpty()) {
                CustomAlertDialogCustom().alertSelectPrinter(requireContext())
            } else {
                alertPrinterTag(getString(R.string.want_to_reprint_the_label), itemPrinter)
            }
        }
    }

    private fun getArgs() {
        mPrinterConnection =
            PrinterConnection(SetupNamePrinter.mNamePrinterString)
        mArgs =
            requireArguments().getSerializable("VOLUME_SHOW_ANDRESS") as ResponseListRecyclerView
        setRecyclerView(mArgs)
    }

    //ALERT DIALOG VERIFICA SE USUARIO QUER REIMPRIMIR -->
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
            mViewModel.printer(itemPrinter.id)
            alertSet.dismiss()
        }
        alertSet.show()
    }

    /**RESPOSTAS DO CLICK NA IMPRESSORA -->*/
    private fun setObservables() {
        mViewModel.mSucessVolShow.observe(viewLifecycleOwner) { etiqueta ->
            try {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                    mPrinterConnection.sendZplOverBluetoothNet(
                        etiqueta.toString()
                    )
                }
                Handler(Looper.getMainLooper()).postDelayed({ mDialog.hide() }, 500)
            } catch (e: Exception) {
                mDialog.hide()
                Toast.makeText(
                    requireContext(),
                    "Erro ao receber zpl!\nTente novamente!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        mViewModel.mErrorVolShow.observe(viewLifecycleOwner) { messageError ->
            mDialog.hide()
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), messageError)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
        mDialog.dismiss()
    }
}
