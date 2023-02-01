package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.*
import com.documentos.wms_beirario.repository.movimentacaoentreenderecos.MovimentacaoEntreEnderecosRepository
import com.documentos.wms_beirario.utils.SingleLiveEvent
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class ReturnTaskViewModel(private var repository: MovimentacaoEntreEnderecosRepository) :
    ViewModel() {

    private var mSucess = SingleLiveEvent<ResponseTaskOPeration1>()
    val mSucessShow: LiveData<ResponseTaskOPeration1>
        get() = mSucess

    //--------------->
    private var mSucessEmply = SingleLiveEvent<Boolean>()
    val mSucessEmplyShow: LiveData<Boolean>
        get() = mSucessEmply

    //------------>
    private var mError = SingleLiveEvent<String>()
    val mErrorShow: LiveData<String>
        get() = mError

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
     * RETORNA LEITURA DO ENDEREÇO-->
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