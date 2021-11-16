package com.documentos.wms_beirario.ui.consultacodbarras.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.FragmentEnderecoBinding
import com.documentos.wms_beirario.databinding.FragmentProdutoBinding

class EnderecoFragment : Fragment() {

    private var Binding: FragmentEnderecoBinding? = null
    private val mBinding get() = Binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_endereco, container, false)
    }
}