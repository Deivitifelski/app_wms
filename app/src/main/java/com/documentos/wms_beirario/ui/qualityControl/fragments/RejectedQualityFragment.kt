package com.documentos.wms_beirario.ui.qualityControl.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.FragmentRejectedQualityBinding
import com.documentos.wms_beirario.model.qualityControl.NaoAprovado
import com.documentos.wms_beirario.ui.qualityControl.adapter.AdapterQualityControlReject


class RejectedQualityFragment(private val list: MutableList<NaoAprovado>) : Fragment() {

    private var mBinding: FragmentRejectedQualityBinding? = null
    private val binding get() = mBinding!!
    private lateinit var mAdapter: AdapterQualityControlReject

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentRejectedQualityBinding.inflate(layoutInflater)
        setRv()
        return mBinding!!.root
    }

    private fun setRv() {
        mAdapter = AdapterQualityControlReject()
        binding.rvApproved.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
        mAdapter.submitList(list)
    }


    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

}