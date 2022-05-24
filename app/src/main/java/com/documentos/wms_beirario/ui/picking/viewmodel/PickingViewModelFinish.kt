package com.documentos.wms_beirario.ui.picking.viewmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.picking.PickingRequest2
import com.documentos.wms_beirario.model.picking.PickingResponse3
import com.documentos.wms_beirario.repository.picking.PickingRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class PickingViewModelFinish(private val mRepository: PickingRepository) : ViewModel() {

    private var mSucess = MutableLiveData<List<PickingResponse3>>()
    val mSucessShow: LiveData<List<PickingResponse3>>
        get() = mSucess

    //------------>
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //------------->
    private var mValidProgress = MutableLiveData<Boolean>()
    val mValidProgressShow: LiveData<Boolean>
        get() = mValidProgress

    //-------FINISH READING------------>
    private var mSucessReading = MutableLiveData<Unit>()
    val mSucessReadingShow: LiveData<Unit>
        get() = mSucessReading

    //------------>
    private var mErrorReading = MutableLiveData<String>()
    val mErrorReadingShow: LiveData<String>
        get() = mErrorReading

    //--------------------->
    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow: LiveData<String>
        get() = mErrorAll


    fun getItensPicking() {
        viewModelScope.launch {
            try {
                val request = this@PickingViewModelFinish.mRepository.getTaskPicking()
                mValidProgress.value = false
                if (request.isSuccessful) {
                    request.let { list ->
                        mSucess.postValue(list.body())
                    }
                } else {
                    val message1 = request.errorBody()!!.string()
                    val message2 = JSONObject(message1).getString("message")
                    val messageEdit = message2.replace("JA", "JÁ").replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
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
                mValidProgress.postValue(false)
            }
        }
    }

    //----------------------TASK FINISH----------------->
    fun finishTaskPicking(pickingRequest2: PickingRequest2) {
        viewModelScope.launch {
            val request =
                this@PickingViewModelFinish.mRepository.postPickinFinish(pickingRequest2 = pickingRequest2)
            try {
                if (request.isSuccessful) {
                    request.let {
                        mSucessReading.postValue(request.body())
                    }
                } else {
                    val message1 = request.errorBody()!!.string()
                    val message2 = JSONObject(message1).getString("message")
                    val messageEdit =
                        message2.replace("ENDERECO", "ENDEREÇO").replace("NAO", "NÃO")
                    mErrorReading.postValue(messageEdit)
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
                mValidProgress.postValue(false)
            }
        }
    }

    /** --------------------------------Picking 3 ViewModelFactory------------------------------------ */
    class Picking2ViewModelFactory constructor(private val repository: PickingRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(PickingViewModelFinish::class.java)) {
                PickingViewModelFinish(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}
