package com.documentos.wms_beirario.ui.consultaAuditoria.viewModel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.auditoria.BodyAuditoriaFinish
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoria1
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoria3
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoriaEstantes2
import com.documentos.wms_beirario.model.receiptproduct.ReceiptProduct1
import com.documentos.wms_beirario.repository.consultaAuditoria.AuditoriaRepository
import com.documentos.wms_beirario.repository.login.LoginRepository
import com.documentos.wms_beirario.ui.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class AuditoriaViewModel2(val mRepository: AuditoriaRepository) : ViewModel() {


    private var mSucessAuditoria3 = MutableLiveData<List<ResponseAuditoria3>>()
    val mSucessAuditoria3Show: LiveData<List<ResponseAuditoria3>>
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

    private var mSucessPost = MutableLiveData<List<ResponseAuditoria3>>()
    val mSucessPostShow: LiveData<List<ResponseAuditoria3>>
        get() = mSucessPost

    //----------->
    private var mErrorPost = MutableLiveData<String>()
    val mErrorPostShow: LiveData<String>
        get() = mErrorPost

    init {
        mValidProgressEdit.postValue(false)
    }

    fun getReceipt3(idAuditoria: String, estantes: String) {
        viewModelScope.launch {
            val request =
                this@AuditoriaViewModel2.mRepository.getAuditoriaItemEstantes3(
                    id = idAuditoria,
                    estante = estantes
                )
            try {
                mValidProgressEdit.postValue(true)
                if (request.isSuccessful) {
                    request.let { list ->
                        mSucessAuditoria3.postValue(list.body())
                    }
                } else {
                    if (request.code() == 404){
                        mErrorAuditoria.postValue("Erro:(${request.code()})\nSem itens a ser auditados!")
                    }else {
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
            val request =
                this@AuditoriaViewModel2.mRepository.postAuditoriaFinish(bodyAuditoriaFinish = body)
            try {
                mValidProgressEdit.postValue(true)
                if (request.isSuccessful) {
                    request.let { list ->
                        mSucessPost.postValue(list.body())
                    }
                } else {
                    if (request.code() == 404){
                        mErrorAuditoria.postValue("Erro:(${request.code()})\nErro na bipagem!")
                    }else {
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