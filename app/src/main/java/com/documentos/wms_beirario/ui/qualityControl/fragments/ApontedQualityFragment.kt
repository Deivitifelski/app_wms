package com.documentos.wms_beirario.ui.qualityControl.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.FragmentQualityControlApontedBinding
import com.documentos.wms_beirario.model.qualityControl.Apontado
import com.documentos.wms_beirario.ui.qualityControl.adapter.AdapterQualityControlAponted


class ApontedQualityFragment(private val list: MutableList<Apontado>) : Fragment() {

    private var mBinding: FragmentQualityControlApontedBinding? = null
    private val binding get() = mBinding!!
    private lateinit var mAdapter: AdapterQualityControlAponted

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentQualityControlApontedBinding.inflate(layoutInflater)
        setRv()
        return mBinding!!.root
    }

    private fun setRv() {
        mAdapter = AdapterQualityControlAponted()
        binding.rvAponted.apply {
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