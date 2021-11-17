package com.documentos.wms_beirario.ui.consultacodbarras.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.documentos.wms_beirario.databinding.FragmentArmazenagem02Binding
import com.documentos.wms_beirario.databinding.FragmentEnderecoBinding
import com.example.coletorwms.model.codBarras.Cod.EnderecoModel

class EnderecoFragment : Fragment() {

    private var mBinding: FragmentEnderecoBinding? = null
    private val _binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEnderecoBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onResume() {
        super.onResume()
        getData()

    }

    private fun getData() {
        val dataEnd =  requireArguments().getSerializable("ENDERECO") as EnderecoModel
        initItensFixos(dataEnd)
    }


    private fun initItensFixos(dadosEnd: EnderecoModel) {
        mBinding?.itTipoCodBarrasEndereco?.text = dadosEnd.tipo.toString()
        mBinding?.itNomeAreaCodBarrasEndereco?.text = dadosEnd.nomeArea.toString()
        mBinding?.itEnderecoVisualCodBarrasEndereco?.text = dadosEnd.enderecoVisual.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}