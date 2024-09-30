package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.movementVol.BodyAddVolume
import com.documentos.wms_beirario.model.movementVol.ResponseAddVol
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.*
import com.documentos.wms_beirario.repository.movimentacaoentreenderecos.MovimentacaoEntreEnderecosRepository
import com.documentos.wms_beirario.utils.SingleLiveEvent
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
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

    //-------------->
    private var sucessAddVolume = MutableLiveData<ResponseAddVol>()
    val sucessAddVolumeShow: LiveData<ResponseAddVol>
        get() = sucessAddVolume

    private var errorAddVolume = MutableLiveData<String>()
    val errorAddVolumeShow: LiveData<String>
        get() = errorAddVolume
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
    fun returnTaskMov(idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mValidProgress.postValue(true)
                val request =
                    this@ReturnTaskViewModel.repository.movementReturnTaskMovement(idArmazem, token)
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
    fun readingAndres2(body: RequestReadingAndressMov2, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mValidProgress.postValue(true)
                val requestNewTask =
                    this@ReturnTaskViewModel.repository.readingAndressMov2(
                        body = body,
                        idArmazem,
                        token
                    )
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
    fun addProductMov3(body: RequestAddProductMov3, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mValidProgress.postValue(true)
                val requestAddProduct =
                    this@ReturnTaskViewModel.repository.addProductMov3(
                        body = body,
                        idArmazem,
                        token
                    )
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
    fun finishTask4(body: RequestBodyFinalizarMov4, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                val requestFinish =
                    this@ReturnTaskViewModel.repository.finishTaskMov4(
                        body = body,
                        idArmazem,
                        token
                    )
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
    fun cancelTask(body: BodyCancelMov5, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mValidProgress.postValue(true)
                val requestFinish =
                    this@ReturnTaskViewModel.repository.cancelMov5(body = body, idArmazem, token)
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

    /**
     * Adicionar volumes
     */
    fun sendAddVolume(idTask: String? = "", qrCode: String, token: String, idArmazem: Int) {
        viewModelScope.launch {
            try {
                val body = BodyAddVolume(
                    codBarras = qrCode,
                    idTarefa = idTask
                )
                val request = repository.addVolume(
                    body = body,
                    idArmazem = idArmazem,
                    token = token,
                )
                if (request.isSuccessful) {
                    sucessAddVolume.postValue(request.body())
                } else {
                    errorAddVolume.postValue(validaErrorDb(request))
                }

            } catch (e: Exception) {
                errorAddVolume.postValue(validaErrorException(e))
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