package com.documentos.wms_beirario.ui.inventory.fragment.bottomNav

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.FragmentVolumeBottomNavBinding
import com.documentos.wms_beirario.databinding.LayoutCustomImpressoraBinding
import com.documentos.wms_beirario.model.inventario.ResponseListRecyclerView
import com.documentos.wms_beirario.model.inventario.VolumesResponseInventarioItem
import com.documentos.wms_beirario.ui.inventory.adapter.AdapterInventoryClickVolume
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.example.coletorwms.constants.CustomMediaSonsMp3


class VolumeBottomNavFragment : Fragment() {

    private lateinit var mAdapter: AdapterInventoryClickVolume
    private lateinit var mArgs: ResponseListRecyclerView
    private var mBinding: FragmentVolumeBottomNavBinding? = null
    private val _binding get() = mBinding!!


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
            alertPrinterTag(getString(R.string.want_to_reprint_the_label),itemPrinter)
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
            /**VALIDAR O ENDPOINT QUE FAZ BUSCA DA IMPRESSAO -->*/
            Toast.makeText(requireContext(), "Clicou em sim", Toast.LENGTH_SHORT).show()
            alertSet.dismiss()
        }
        alertSet.show()
    }


}