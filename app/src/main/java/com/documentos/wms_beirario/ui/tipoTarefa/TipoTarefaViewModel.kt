package com.documentos.wms_beirario.ui.tipoTarefa

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.tipo_tarefa.TipoTarefaResponseItem
import com.documentos.wms_beirario.repository.tipoTarefa.TypeTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class TipoTarefaViewModel(val mRepository: TypeTaskRepository) : ViewModel() {

    private var mSucess = MutableLiveData<List<TipoTarefaResponseItem>>()
    val mSucessShow get() = mSucess

    private var mErrorHttp = MutableLiveData<String>()
    val mErrorHttpShow get() = mErrorHttp

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow get() = mErrorAll

    private var mProgress = MutableLiveData<Boolean>()
    val mProgressShow get() = mProgress


    fun getTask() {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val request = this@TipoTarefaViewModel.mRepository.getTarefas()
                if (request.isSuccessful) {
                    request.body().let { listTask ->
                        mSucess.postValue(listTask)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        val error = request.errorBody()!!.string()
                        val error2 = JSONObject(error).getString("message")
                        mErrorHttp.value = error2
                    }
                }
            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mErrorAll.postValue("Verifique sua internet!")
                    }
                    is SocketTimeoutException -> {
                        mErrorAll.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    is TimeoutException -> {
                        mErrorAll.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    else -> {
                        mErrorAll.postValue(e.toString())
                    }
                }
            } finally {
                mProgress.postValue(false)
            }
        }
    }


    /** --------------------------------Tarefa ViewModel Factory------------------------------------ */
    class TarefaViewModelFactory constructor(private val repository: TypeTaskRepository) :
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