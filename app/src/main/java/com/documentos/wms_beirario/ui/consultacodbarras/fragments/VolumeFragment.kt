package com.documentos.wms_beirario.ui.consultacodbarras.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.databinding.CodBarrasMainFragmentBinding
import com.documentos.wms_beirario.databinding.FragmentVolumeBinding
import com.documentos.wms_beirario.ui.consultacodbarras.ConsultaCodBarrasViewModel

class VolumeFragment : Fragment() {

    private lateinit var mViewModel: ConsultaCodBarrasViewModel
    private lateinit var mRetrofitService: RetrofitService
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private var mIdArmazem: Int = 0
    private lateinit var mToken: String
    private var Binding: FragmentVolumeBinding? = null
    private val mBinding get() = Binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Binding = FragmentVolumeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Binding = null
    }

}