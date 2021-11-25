package com.documentos.wms_beirario.ui.Tarefas

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.tipo_tarefa.TipoTarefaResponseItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class TipoTarefaViewModel(private val mRepository: TipoTarefaRepository) : ViewModel() {

    val mResponseSucess = MutableLiveData<List<TipoTarefaResponseItem>>()
    val mResponseError = MutableLiveData<String>()
    private val TAG = "TipoTarefaViewModel--->"

    fun getTarefas() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.e(TAG, Thread.currentThread().name)
            val request = this@TipoTarefaViewModel.mRepository.getTarefas()
            try {
                if (request.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Log.e(TAG, Thread.currentThread().name)
                        request.body().let { listTarefas ->
                            mResponseSucess.value = listTarefas
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.e(TAG, Thread.currentThread().name)
                        val error = request.errorBody()!!.string()
                        val error2 = JSONObject(error).getString("message")
                        mResponseError.value = error2
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mResponseError.value = "Erro inesperado!"
                }
            }
        }
    }

    class TipoTarefaViewModelFactory constructor(private val repository: TipoTarefaRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(TipoTarefaViewModel::class.java)) {
                TipoTarefaViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }

}