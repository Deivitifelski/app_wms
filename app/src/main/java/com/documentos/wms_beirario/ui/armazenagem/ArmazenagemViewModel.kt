package com.documentos.wms_beirario.ui.armazenagem

import ArmazenagemResponse
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.armazenagem.ArmazemRequestFinish
import com.documentos.wms_beirario.repository.armazenagem.ArmazenagemRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class ArmazenagemViewModel(private val mRepository: ArmazenagemRepository) : ViewModel() {


    private var mSucess = MutableLiveData<List<ArmazenagemResponse>>()
    val mSucessShow get() = mSucess

    private var mSucess2 = MutableLiveData<Unit>()
    val mSucessShow2 get() = mSucess2

    private var mErrorHttp = MutableLiveData<String>()
    val mErrorHttpShow get() = mErrorHttp

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow get() = mErrorAll

    private var mProgressInit = MutableLiveData<Boolean>()
    val mProgressInitShow get() = mProgressInit

    init {
        mProgressInit.value = false
    }

    fun getArmazenagem() {
        viewModelScope.launch {
            try {
                mProgressInit.postValue(true)
                val response = this@ArmazenagemViewModel.mRepository.getArmazens()
                if (response.isSuccessful) {
                    response.body().let { listArmazens ->
                        mSucess.postValue(listArmazens)
                    }
                } else {
                    val error = response.errorBody()!!.string()
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
                mProgressInit.postValue(false)
            }
        }
    }

    fun postFinish(armazemRequestFinish: ArmazemRequestFinish) {
        viewModelScope.launch {
            try {
                val request =
                    this@ArmazenagemViewModel.mRepository.finishArmazenagem(armazemRequestFinish = armazemRequestFinish)
                if (request.isSuccessful) {
                    request.let { response ->
                        mSucess2.postValue(response.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val errorEdit = error2.replace("NAO", "NÃO")
                    mErrorHttp.postValue(errorEdit)
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
                mProgressInit.postValue(false)
            }
        }
    }

    /** --------------------------------Armazenagem ViewModelFactory------------------------------------ */
    class ArmazenagemViewModelFactory constructor(private val repository: ArmazenagemRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ArmazenagemViewModel::class.java)) {
                ArmazenagemViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }


}