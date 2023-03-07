package com.documentos.wms_beirario.ui.qualityControl.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.FragmentNotApontedQualityBinding
import com.documentos.wms_beirario.model.qualityControl.NaoApontado
import com.documentos.wms_beirario.ui.qualityControl.adapter.AdapterQualityControlNotAponted


class NotApontedQualityFragment(private val list: MutableList<NaoApontado>) : Fragment() {


    private var mBinding: FragmentNotApontedQualityBinding? = null
    private val binding get() = mBinding!!
    private lateinit var mAdapter: AdapterQualityControlNotAponted

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentNotApontedQualityBinding.inflate(layoutInflater)
        setRv()
        return mBinding!!.root
    }

    private fun setRv() {
        mAdapter = AdapterQualityControlNotAponted()
        binding.rvNotAponted.apply {
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