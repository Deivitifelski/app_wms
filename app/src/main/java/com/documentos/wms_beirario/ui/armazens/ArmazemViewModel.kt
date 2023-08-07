package com.documentos.wms_beirario.ui.armazens

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.armazens.ArmazensResponse
import com.documentos.wms_beirario.repository.armazens.ArmazensRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class ArmazemViewModel(val mRepository: ArmazensRepository) : ViewModel() {

    private var mSucess = MutableLiveData<List<ArmazensResponse>>()
    val mSucessShow get() = mSucess

    private var mErrorHttp = MutableLiveData<String>()
    val mErrorHttpShow get() = mErrorHttp

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow get() = mErrorAll

    private var mProgress = MutableLiveData<Boolean>()
    val mProgressShow get() = mProgress


    fun getArmazens(token: String) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val request = this@ArmazemViewModel.mRepository.getArmazens(token)
                if (request.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        request.body().let { listArmazens ->
                            mSucess.postValue(listArmazens)
                        }
                    }
                } else {
                        val error = request.errorBody()!!.string()
                        val error2 = JSONObject(error).getString("message")
                        mErrorHttp.value = error2
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
                mProgress.postValue(false)
            }
        }
    }

    /** --------------------------------ArmazensViewModelFactory------------------------------------ */
    class ArmazensViewModelFactory constructor(private val repository: ArmazensRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ArmazemViewModel::class.java)) {
                ArmazemViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}