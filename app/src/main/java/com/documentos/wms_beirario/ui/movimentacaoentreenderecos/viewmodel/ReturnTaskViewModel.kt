package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.viewmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementNewTask
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementResponseModel1
import com.documentos.wms_beirario.repository.movimentacaoentreenderecos.MovimentacaoEntreEnderecosRepository
import com.documentos.wms_beirario.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class ReturnTaskViewModel(private var repository: MovimentacaoEntreEnderecosRepository) :
    ViewModel() {

    private var mSucess = MutableLiveData<List<MovementResponseModel1>>()
    val mSucessShow: LiveData<List<MovementResponseModel1>>
        get() = mSucess

    //--------------->
    private var mSucessEmply = MutableLiveData<Boolean>()
    val mSucessEmplyShow: LiveData<Boolean>
        get() = mSucessEmply

    //------------>
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //-------------->
    private var mValidProgress = MutableLiveData<Boolean>()
    val mValidProgressShow: LiveData<Boolean>
        get() = mValidProgress

    //-------------->
    private var mcreateNewTsk = SingleLiveEvent<MovementNewTask>()
    val mcreateNewTskShow: SingleLiveEvent<MovementNewTask>
        get() = mcreateNewTsk

    /**
     * CHAMADA ONDE RETORNA AS MOVIMENTAÇOES
     * (MOVIMENTAÇAO -> GET (Retornar tarefas de movimentação, com opção de filtro por operador)
     */
    fun returnTaskMov() {
        viewModelScope.launch {
            try {
                mValidProgress.postValue(true)
                val request =
                    this@ReturnTaskViewModel.repository.movementReturnTaskMovement()
                if (request.isSuccessful) {
                    if (request.body().isNullOrEmpty()) {
                        mSucessEmply.value = true
                    } else {
                        mSucessEmply.value = false
                        mSucess.postValue(request.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }

            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mValidProgress.value = false
                        mError.postValue("Verifique sua internet")
                    }
                    else -> {
                        mValidProgress.value = false
                        mError.postValue("Ops! Erro inesperado...")
                    }
                }
            } finally {
                mValidProgress.postValue(false)
            }
        }
    }

    /**
     * Movimentação -> POST (Criar nova tarefa de movimentação)
     */
    fun newTask() {
        viewModelScope.launch {
            try {
                mValidProgress.postValue(true)
                val requestNewTask = this@ReturnTaskViewModel.repository.movementNewTask()
                if (requestNewTask.isSuccessful) {
                    mcreateNewTsk.postValue(requestNewTask.body())
                } else {
                    val error = requestNewTask.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }
            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mError.postValue("Verifique sua internet!")
                    }
                    is SocketTimeoutException -> {
                        mError.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    is TimeoutException -> {
                        mError.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    else -> {
                        mError.postValue(e.toString())
                    }
                }
            } finally {
                mValidProgress.postValue(false)
            }
        }
    }

    /** --------------------------------movimentaçao 01 ViewModelFactory------------------------------------ */
    class Mov1ViewModelFactory constructor(private val repository: MovimentacaoEntreEnderecosRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ReturnTaskViewModel::class.java)) {
                ReturnTaskViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}