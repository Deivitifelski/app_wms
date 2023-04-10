package com.documentos.wms_beirario.ui.boardingConference.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.FragmentApointedBoardingBinding
import com.documentos.wms_beirario.databinding.FragmentQualityControlApontedBinding
import com.documentos.wms_beirario.model.conferenceBoarding.DataResponseBoarding
import com.documentos.wms_beirario.model.qualityControl.Apontado
import com.documentos.wms_beirario.ui.boardingConference.adapter.AdapterConferenceBoardingAdapter
import com.documentos.wms_beirario.ui.qualityControl.adapter.AdapterQualityControlAponted


class ApointedBoardingFragment(private val list: MutableList<DataResponseBoarding>) : Fragment() {

    private var mBinding: FragmentApointedBoardingBinding? = null
    private val binding get() = mBinding!!
    private lateinit var mAdapter: AdapterConferenceBoardingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentApointedBoardingBinding.inflate(layoutInflater)
        setRv()
        return mBinding!!.root
    }

    private fun setRv() {
        mAdapter = AdapterConferenceBoardingAdapter()
        binding.rvApointedBoarding.apply {
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