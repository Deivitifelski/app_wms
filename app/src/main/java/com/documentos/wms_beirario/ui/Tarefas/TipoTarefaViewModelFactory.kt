package com.documentos.wms_beirario.ui.Tarefas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TipoTarefaViewModelFactory constructor(private val repository: TipoTarefaRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(TipoTarefaViewModel::class.java)) {
            TipoTarefaViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}



