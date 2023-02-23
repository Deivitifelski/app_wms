package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.*
import com.documentos.wms_beirario.repository.movimentacaoentreenderecos.MovimentacaoEntreEnderecosRepository
import com.documentos.wms_beirario.utils.SingleLiveEvent
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException

class ReturnTaskViewModel(private var repository: MovimentacaoEntreEnderecosRepository) :
    ViewModel() {

    private var mSucess = SingleLiveEvent<ResponseMovParesAvulso1>()
    val mSucessShow: LiveData<ResponseMovParesAvulso1>
        get() = mSucess

    //--------------->
    private var mSucessEmply = SingleLiveEvent<Boolean>()
    val mSucessEmplyShow: LiveData<Boolean>
        get() = mSucessEmply

    //------------>
    private var mError = SingleLiveEvent<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //------------>
    private var mEmplyTask = SingleLiveEvent<String>()
    val mEmplyTaskShow: LiveData<String>
        get() = mEmplyTask


    //-------------->
    private var mValidProgress = SingleLiveEvent<Boolean>()
    val mValidProgressShow: LiveData<Boolean>
        get() = mValidProgress

    //-------------->
    private var mReadingAndress2 = SingleLiveEvent<ResponseReadingMov2>()
    val mReadingAndress2Show: SingleLiveEvent<ResponseReadingMov2>
        get() = mReadingAndress2

    //-------------->
    private var mAddProductMov3 = SingleLiveEvent<String>()
    val mAddProductMov3Show: SingleLiveEvent<String>
        get() = mAddProductMov3

    //-------------->
    private var finishTask = SingleLiveEvent<Unit>()
    val finishTaskShow: SingleLiveEvent<Unit>
        get() = finishTask

    //-------------->
    private var cancelTask = SingleLiveEvent<ResponseCancelMov5>()
    val cancelTaskShow: SingleLiveEvent<ResponseCancelMov5>
        get() = cancelTask
    /**
     * CHAMADA ONDE RETORNA AS MOVIMENTAÇOES
     * (MOVIMENTAÇAO -> GET (Retornar tarefas de movimentação, com opção de filtro por operador)
     *
     * Ao entrar na tela de movimentação deverá exibir a tarefa pendente e itens, se existir.
    exibir documento (tabela tarefa), data criação (tabela tarefa, campo dthr inclusão), sku,
    grade (se par, não possui), quantidade, endereço origem, ordenar retorno dos itens por data
    de inclusão decrescente.
     */

    /** RETORNA AS TAREFAS PENDENTES DO OPERADOR 01*/
    fun returnTaskMov() {
        viewModelScope.launch {
            try {
                mValidProgress.postValue(true)
                val request = this@ReturnTaskViewModel.repository.movementReturnTaskMovement()
                if (request.isSuccessful) {
                    mSucess.postValue(request.body())
                } else if (request.code() == 404) {
                    mEmplyTask.postValue("Operador sem tarefas pendentes!\n${request.code()}")
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }

            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mError.postValue("Verifique sua internet")
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

    /**
     * RETORNA LEITURA DO ENDEREÇO-->
     */
    fun readingAndres2(body: RequestReadingAndressMov2) {
        viewModelScope.launch {
            try {
                mValidProgress.postValue(true)
                val requestNewTask =
                    this@ReturnTaskViewModel.repository.readingAndressMov2(body = body)
                if (requestNewTask.isSuccessful) {
                    mReadingAndress2.postValue(requestNewTask.body())
                } else {
                    val error = requestNewTask.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }
            } catch (e: Exception) {
                mError.postValue(validaErrorException(e))
            } finally {
                mValidProgress.postValue(false)
            }
        }
    }

    /**
     * ADICIONA PRODUTO-->
     */
    fun addProductMov3(body: RequestAddProductMov3) {
        viewModelScope.launch {
            try {
                mValidProgress.postValue(true)
                val requestAddProduct =
                    this@ReturnTaskViewModel.repository.addProductMov3(body = body)
                if (requestAddProduct.isSuccessful) {
                    mAddProductMov3.postValue(requestAddProduct.body()?.result ?: "")
                } else {
                    val error = requestAddProduct.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }
            } catch (e: Exception) {
                mError.postValue(validaErrorException(e))
            } finally {
                mValidProgress.postValue(false)
            }
        }
    }

    /**
     * Finaliza tarefa -->
     */
    fun finishTask4(body: RequestBodyFinalizarMov4) {
        viewModelScope.launch {
            try {
                val requestFinish =
                    this@ReturnTaskViewModel.repository.finishTaskMov4(body = body)
                if (requestFinish.isSuccessful) {
                    finishTask.postValue(requestFinish.body())
                } else {
                    val error = requestFinish.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }
            } catch (e: Exception) {
                mError.postValue(validaErrorException(e))
            }
        }
    }

    /**
     * Cancelar tarefa -->
     */
    fun cancelTask(body: BodyCancelMov5) {
        viewModelScope.launch {
            try {
                mValidProgress.postValue(true)
                val requestFinish =
                    this@ReturnTaskViewModel.repository.cancelMov5(body = body)
                if (requestFinish.isSuccessful) {
                    cancelTask.postValue(requestFinish.body())
                } else {
                    val error = requestFinish.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }
            } catch (e: Exception) {
                mError.postValue(validaErrorException(e))
            } finally {
                mValidProgress.postValue(false)
            }
        }
    }

    //cancelMov5
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