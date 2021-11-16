package com.documentos.wms_beirario.ui.consultacodbarras

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.documentos.wms_beirario.R

class FragmentMainCodBarras : Fragment() {

    companion object {
        fun newInstance() = FragmentMainCodBarras()
    }

    private lateinit var viewModel: FragmentMainCodBarrasViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_cod_barras_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FragmentMainCodBarrasViewModel::class.java)
        // TODO: Use the ViewModel
    }

}