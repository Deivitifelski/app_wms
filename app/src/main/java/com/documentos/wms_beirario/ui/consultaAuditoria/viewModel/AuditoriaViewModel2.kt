package com.documentos.wms_beirario.ui.consultaAuditoria.viewModel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.auditoria.BodyAuditoriaFinish
import com.documentos.wms_beirario.model.auditoria.ResponseFinishAuditoria
import com.documentos.wms_beirario.repository.consultaAuditoria.AuditoriaRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class AuditoriaViewModel2(val mRepository: AuditoriaRepository) : ViewModel() {


    private var mSucessAuditoria3 = MutableLiveData<ResponseFinishAuditoria>()
    val mSucessAuditoria3Show: LiveData<ResponseFinishAuditoria>
        get() = mSucessAuditoria3

    //----------->
    private var mErrorAuditoria = MutableLiveData<String>()
    val mErrorAuditoriaShow: LiveData<String>
        get() = mErrorAuditoria

    //----------->
    private var mValidProgressEdit = MutableLiveData<Boolean>()
    val mValidProgressEditShow: LiveData<Boolean>
        get() = mValidProgressEdit

    //------------>
    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow: LiveData<String>
        get() = mErrorAll

    private var mSucessPost = MutableLiveData<ResponseFinishAuditoria>()
    val mSucessPostShow: LiveData<ResponseFinishAuditoria>
        get() = mSucessPost

    //----------->
    private var mErrorPost = MutableLiveData<String>()
    val mErrorPostShow: LiveData<String>
        get() = mErrorPost


    fun getReceipt3(idAuditoria: String, estantes: String) {
        viewModelScope.launch {
            try {
                val request =
                    this@AuditoriaViewModel2.mRepository.getAuditoriaItemEstantes3(
                        id = idAuditoria,
                        estante = estantes
                    )
                mValidProgressEdit.postValue(true)
                if (request.isSuccessful) {
                    request.let { list ->
                        mSucessAuditoria3.postValue(list.body())
                    }
                } else {
                    if (request.code() == 404) {
                        mErrorAuditoria.postValue("Erro:(${request.code()})\nSem itens a ser auditados!")
                    } else {
                        val error = request.errorBody()!!.string()
                        val error2 = JSONObject(error).getString("message")
                        mErrorAuditoria.postValue(error2)
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
                mValidProgressEdit.postValue(false)
            }
        }
    }

    fun postItens(body: BodyAuditoriaFinish) {
        viewModelScope.launch {
            try {
                val request =
                    this@AuditoriaViewModel2.mRepository.postAuditoriaFinish(bodyAuditoriaFinish = body)
                mValidProgressEdit.postValue(true)
                if (request.isSuccessful) {
                    request.let { list ->
                        mSucessPost.postValue(list.body())
                    }
                } else {
                    if (request.code() == 403) {
                        val error = request.errorBody()!!.string()
                        val error2 = JSONObject(error).getString("erro")
                        mErrorPost.postValue("Erro:(${request.code()})\n$error2")
                    } else {
                        val error = request.errorBody()!!.string()
                        val error2 = JSONObject(error).getString("message")
                        mErrorPost.postValue(error2)
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
                mValidProgressEdit.postValue(false)
            }
        }
    }

    /** --------------------------------AuditoriaViewModelFactory--------------------------------*/
    class Auditoria2ViewModelFactory constructor(private val repository: AuditoriaRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(AuditoriaViewModel2::class.java)) {
                AuditoriaViewModel2(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}