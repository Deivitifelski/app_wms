package com.documentos.wms_beirario.ui.qualityControl.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.FragmentApprovedQualityBinding
import com.documentos.wms_beirario.model.qualityControl.Aprovado
import com.documentos.wms_beirario.ui.qualityControl.adapter.AdapterQualityControlApproved


class ApprovedQualityFragment(private val list: MutableList<Aprovado>) : Fragment() {


    private var mBinding: FragmentApprovedQualityBinding? = null
    private val binding get() = mBinding!!
    private lateinit var mAdapter: AdapterQualityControlApproved

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentApprovedQualityBinding.inflate(layoutInflater)
        setRv()
        return mBinding!!.root
    }

    private fun setRv() {
        mAdapter = AdapterQualityControlApproved()
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