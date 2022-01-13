package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.viewmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementNewTask
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementResponseModel1
import com.documentos.wms_beirario.repository.movimentacaoentreenderecos.MovimentacaoEntreEnderecosRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

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
                val request =
                    this@ReturnTaskViewModel.repository.movementReturnTaskMovement()
                if (request.isSuccessful) {
                    if (request.body().isNullOrEmpty()) {
                        mSucessEmply.value = true
                        mValidProgress.value = false
                    } else {
                        mValidProgress.value = false
                        mSucessEmply.value = false
                        mSucess.postValue(request.body())
                    }
                } else {
                    mValidProgress.value = false
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }

            } catch (e: Exception) {
                mValidProgress.value = false
                mError.postValue("Ops! Erro inesperado...")
            }
        }
    }

    /**
     * Movimentação -> POST (Criar nova tarefa de movimentação)
     */
    fun newTask() {
        viewModelScope.launch {
            try {
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
                mError.postValue(e.toString())
            }
        }
    }
}