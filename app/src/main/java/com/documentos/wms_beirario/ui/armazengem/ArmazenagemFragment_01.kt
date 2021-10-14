package com.documentos.wms_beirario.ui.armazengem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.documentos.wms_beirario.databinding.FragmentArmazenagem01Binding

class ArmazenagemFragment_01 : Fragment() {
    private var mBinding: FragmentArmazenagem01Binding? = null
    private val _binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentArmazenagem01Binding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments.let { bundle ->
            bundle?.getInt(ID_TAREFA)
        } ?: 0
    }



    companion object {
        const val ID_TAREFA = "id_tarefa_clicada"
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

}