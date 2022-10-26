package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.documentos.wms_beirario.databinding.ActivityMovimentacaoEntreEnderecosNewTask3Binding

class MovimentacaoEnderecosActivity3 : AppCompatActivity() {

    private lateinit var mBinding: ActivityMovimentacaoEntreEnderecosNewTask3Binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMovimentacaoEntreEnderecosNewTask3Binding.inflate(layoutInflater)
        setContentView(mBinding.root)

    }

}
