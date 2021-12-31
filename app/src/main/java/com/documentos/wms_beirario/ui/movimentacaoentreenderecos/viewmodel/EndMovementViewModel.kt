package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.viewmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementAddTask
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementFinishAndress
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementReturnItemClickMov
import com.documentos.wms_beirario.repository.movimentacaoentreenderecos.MovimentacaoEntreEnderecosRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class EndMovementViewModel(private val repository: MovimentacaoEntreEnderecosRepository) :
    ViewModel() {

    private var mSucess = MutableLiveData<List<MovementReturnItemClickMov>>()
    val mSucessShow: LiveData<List<MovementReturnItemClickMov>>
        get() = mSucess

    //------------>
    private var mError = MutableLiveData<String>()
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

    //-----------------------------------FINISH------------------------------------>
    private var mSucessAddTask = MutableLiveData<String>()
    val mSucessAddTaskShow: LiveData<String>
        get() = mSucessAddTask

    //-----------------------------------FINISH------------------------------------>
    private var mSucessFinish = MutableLiveData<String>()
    val mSucessFinishShow: LiveData<String>
        get() = mSucessFinish


    fun getTaskItemClick(id_tarefa: String) {
        viewModelScope.launch {
            try {
                val request = this@EndMovementViewModel.repository.returnTaskItemClick(id_tarefa)
                if (request.isSuccessful) {
                    if (request.body().isNullOrEmpty()) {
                        mValidLinear.value = false
                        mValidProgress.value = false
                    } else {
                        mValidLinear.value = true
                        mSucess.postValue(request.body())
                        mValidProgress.value = false
                    }
                } else {
                    mValidProgress.value = false
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }

            } catch (e: Exception) {
                mError.postValue("Ops! Erro inesperado...")
                mValidProgress.value = false
            }
        }
    }

    /**--------ADICIONAR TAREFA------------------------->*/
    fun addTask(movementAddTask: MovementAddTask) {
        viewModelScope.launch {
            try {
                val requestAddTask =
                    this@EndMovementViewModel.repository.movementAddTask(movementAddTask)
                if (requestAddTask.isSuccessful) {
                    mSucessAddTask.postValue("")
                } else {
                    val error = requestAddTask.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }

            } catch (e: Exception) {
                mError.postValue("Ops! Erro inesperado...")
            }
        }
    }


    /**--------FINALIZAR TAREFA------------------------->*/
    fun finishMovemet(
        postRequestModelFinish: MovementFinishAndress
    ) {
        viewModelScope.launch {
            val requestFinish = this@EndMovementViewModel.repository.movementFinishMovement(
                postRequestModelFinish
            )
            try {
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
                mError.postValue(e.toString())
            }
        }
    }


    class ClickItemMov2ViewModelFactory constructor(private val repository: MovimentacaoEntreEnderecosRepository) :
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