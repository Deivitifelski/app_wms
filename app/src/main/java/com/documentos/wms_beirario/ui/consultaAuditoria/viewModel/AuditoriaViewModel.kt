package com.documentos.wms_beirario.ui.consultaAuditoria.viewModel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoria1
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

    init {
        mValidProgressEdit.postValue(false)
    }

    fun getReceipt1(idAuditoria: String) {
        viewModelScope.launch {
            val request =
                this@AuditoriaViewModel.mRepository.getAuditoria1(idAuditoria = idAuditoria)
            try {
                mValidProgressEdit.postValue(true)
                if (request.isSuccessful) {
                    request.let { list ->
                        mSucessAuditoria.postValue(list.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    mErrorAuditoria.postValue(error2)
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
    class Auditoria_1ViewModelFactory constructor(private val repository: AuditoriaRepository) :
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