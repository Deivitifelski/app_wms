package com.documentos.wms_beirario.ui.consultaAuditoria.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoria1
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoriaEstantes2
import com.documentos.wms_beirario.repository.consultaAuditoria.AuditoriaRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class AuditoriaViewModel(val mRepository: AuditoriaRepository) : ViewModel() {


    private var mSucessAuditoria = MutableLiveData<ResponseAuditoria1>()
    val mSucessAuditoriaShow: LiveData<ResponseAuditoria1>
        get() = mSucessAuditoria

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

    private var mSucessAuditoriaEstantes = MutableLiveData<List<ResponseAuditoriaEstantes2>>()
    val mSucessAuditoriaEstantesShow: LiveData<List<ResponseAuditoriaEstantes2>>
        get() = mSucessAuditoriaEstantes

    //----------->
    private var mErrorAuditoriaEstantes = MutableLiveData<String>()
    val mErrorAuditoriaEstanteshow: LiveData<String>
        get() = mErrorAuditoriaEstantes

    init {
        mValidProgressEdit.postValue(false)
    }

    fun getReceipt1(idAuditoria: String) {
        viewModelScope.launch {
            try {
                val request =
                    this@AuditoriaViewModel.mRepository.getAuditoria1(idAuditoria = idAuditoria)
                mValidProgressEdit.postValue(true)
                if (request.isSuccessful) {
                    request.let { list ->
                        mSucessAuditoria.postValue(list.body())
                    }
                } else {
                    if (request.code() == 404) {
                        mErrorAuditoria.postValue("Erro:(${request.code()})\nAuditoria não encontrada!")
                    } else {
                        mErrorAuditoria.postValue(validaErrorDb(request))
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
                    is SocketTimeoutException -> {
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

    fun getReceiptEstantes2(idAuditoria: String) {
        viewModelScope.launch {
            try {
                val request =
                    this@AuditoriaViewModel.mRepository.getAuditoriaEstantes2(idAuditoria = idAuditoria)
                mValidProgressEdit.postValue(true)
                if (request.isSuccessful) {
                    request.let { list ->
                        mSucessAuditoriaEstantes.postValue(list.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    mErrorAuditoriaEstantes.postValue(error2)
                }
            } catch (e: Exception) {
                mErrorAll.postValue(validaErrorException(e))
            } finally {
                mValidProgressEdit.postValue(false)
            }
        }
    }


    /** --------------------------------AuditoriaViewModelFactory--------------------------------*/
    class Auditoria1ViewModelFactory constructor(private val repository: AuditoriaRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(AuditoriaViewModel::class.java)) {
                AuditoriaViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}



