package com.documentos.wms_beirario.ui.consultacodbarras.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.FragmentProdutoBinding
import com.documentos.wms_beirario.databinding.FragmentVolumeBinding

class ProdutoFragment : Fragment() {

    private var Binding: FragmentProdutoBinding? = null
    private val mBinding get() = Binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Binding = FragmentProdutoBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Binding = null
    }

}