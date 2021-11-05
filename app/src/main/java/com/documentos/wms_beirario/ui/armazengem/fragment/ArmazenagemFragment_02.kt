package com.documentos.wms_beirario.ui.armazengem.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.FragmentArmazenagem01Binding
import com.documentos.wms_beirario.databinding.FragmentArmazenagem02Binding

class ArmazenagemFragment_02 : Fragment() {
    private var mBinding: FragmentArmazenagem02Binding? = null
    private val _binding get() = mBinding!!
    val userArgs : ArmazenagemFragment_02Args by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentArmazenagem02Binding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

}