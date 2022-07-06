package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.viewmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementAddTask
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementFinishAndress
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementReturnItemClickMov
import com.documentos.wms_beirario.repository.movimentacaoentreenderecos.MovimentacaoEntreEnderecosRepository
import com.documentos.wms_beirario.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class EndMovementViewModel(private val repository: MovimentacaoEntreEnderecosRepository) :
    ViewModel() {

    private var mSucess = SingleLiveEvent<List<MovementReturnItemClickMov>>()
    val mSucessShow: LiveData<List<MovementReturnItemClickMov>>
        get() = mSucess

    //------------>
    private var mError = SingleLiveEvent<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //-------------->
    private var mValidProgress = MutableLiveData<Boolean>()
    val mValidProgressShow: LiveData<Boolean>
        get() = mValidProgress
    //-------------->

    private var mValidLinear = MutableLiveData<Boolean>()
    val mValidLinearShow: LiveData<Boolean>
        get() = mValidLinear

    //-----------------------------------ADD TAREFA------------------------------------>
    private var mSucessAddTask = SingleLiveEvent<String>()
    val mSucessAddTaskShow: LiveData<String>
        get() = mSucessAddTask

    private var mErrorAddTask = SingleLiveEvent<String>()
    val mErrorAddTaskShow: LiveData<String>
        get() = mErrorAddTask

    private var mErrorAll = SingleLiveEvent<String>()
    val mErrorAllShow: LiveData<String>
        get() = mErrorAll


    //-----------------------------------FINISH------------------------------------>
    private var mSucessFinish = MutableLiveData<String>()
    val mSucessFinishShow: LiveData<String>
        get() = mSucessFinish


    /**
     * Movimentação -> GET (Retornar as Itens tarefas de movimentação)
     */
    fun getTaskItemClick(id_tarefa: String) {
        viewModelScope.launch {
            try {
                mValidProgress.postValue(true)
                val request = this@EndMovementViewModel.repository.returnTaskItemClick(id_tarefa)
                if (request.isSuccessful) {
                    if (request.body().isNullOrEmpty()) {
                        mValidLinear.value = false
                    } else {
                        mValidLinear.value = true
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
                mValidProgress.postValue(false)
            }
        }
    }

    /**
     * Movimentação ->POST (Adiciona Item a tarefa de movimentação)
     */
    fun addTask(movementAddTask: MovementAddTask) {
        viewModelScope.launch {
            try {
                mValidProgress.postValue(true)
                val requestAddTask =
                    this@EndMovementViewModel.repository.movementAddTask(movementAddTask)
                if (requestAddTask.isSuccessful) {
                    mSucessAddTask.postValue("")
                } else {
                    val error = requestAddTask.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit1 = error2.replace("NAO", "NÃO")
                    mErrorAddTask.postValue(messageEdit1)
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
                mValidProgress.postValue(false)
            }
        }
    }


    /**--------FINALIZAR TAREFA------------------------->*/
    fun finishMovemet(
        postRequestModelFinish: MovementFinishAndress
    ) {
        viewModelScope.launch {
            try {
                mValidProgress.postValue(true)
                val requestFinish = this@EndMovementViewModel.repository.movementFinishMovement(
                    postRequestModelFinish
                )
                when {
                    requestFinish.isSuccessful -> {
                        mSucessFinish.postValue("")
                    }
                    else -> {
                        val error = requestFinish.errorBody()!!.string()
                        val error2 = JSONObject(error).getString("message")
                        val messageEdit = error2.replace("NAO", "NÃO")
                        mError.postValue(messageEdit)
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
                mValidProgress.postValue(false)
            }
        }
    }

    /** --------------------------------movimentaçao 02 ViewModelFactory------------------------------------ */
    class Mov2ViewModelFactory constructor(private val repository: MovimentacaoEntreEnderecosRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(EndMovementViewModel::class.java)) {
                EndMovementViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}