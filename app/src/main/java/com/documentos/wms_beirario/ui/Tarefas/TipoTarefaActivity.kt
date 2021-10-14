package com.documentos.wms_beirario.ui.Tarefas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.documentos.wms_beirario.databinding.ActivityArmazensBinding
import com.documentos.wms_beirario.databinding.ActivityTipoTarefaBinding

class TipoTarefaActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityTipoTarefaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityTipoTarefaBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
    }
}