package com.documentos.wms_beirario.ui.inventario.fragment.createVoid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.documentos.wms_beirario.databinding.FragmentAddedVoidBinding


class AddedVoidFragment : Fragment() {

    private var mBinding: FragmentAddedVoidBinding? = null
    val _binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAddedVoidBinding.inflate(inflater, container, false)
        return _binding.root
    }


}