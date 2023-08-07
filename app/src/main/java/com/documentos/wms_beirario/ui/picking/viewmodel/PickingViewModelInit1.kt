package com.documentos.wms_beirario.ui.picking.viewmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.picking.PickingResponseModel1
import com.documentos.wms_beirario.repository.picking.PickingRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class PickingViewModelInit1(private val mRepository: PickingRepository) : ViewModel() {


    private var mSucessPickingReturn = MutableLiveData<List<PickingResponseModel1>>()
    val mSucessPickingReturnShows: LiveData<List<PickingResponseModel1>>
        get() = mSucessPickingReturn

    //--------------------->
    private var mErrorPicking = MutableLiveData<String>()
    val mErrorPickingShow: LiveData<String>
        get() = mErrorPicking

    //--------------------->
    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow: LiveData<String>
        get() = mErrorAll

    //--------------------->
    private var mValidProgressInit = MutableLiveData<Boolean>()
    val mValidProgressInitShow: LiveData<Boolean>
        get() = mValidProgressInit

    private var mValidProgressEdit = MutableLiveData<Boolean>()
    val mValidProgressEditShow: LiveData<Boolean>
        get() = mValidProgressEdit

    init {
        mValidProgressInit.postValue(true)
        mValidProgressEdit.postValue(false)
    }


    fun getItensPicking1(idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mValidProgressInit.postValue(true)
                val request =
                    this@PickingViewModelInit1.mRepository.getAreasPicking1(idArmazem, token)
                if (request.isSuccessful) {
                    request.let { list ->
                        mSucessPickingReturn.postValue(list.body())
                    }
                } else {
                    val message1 = request.errorBody()!!.string()
                    val message2 = JSONObject(message1).getString("message")
                    mErrorPicking.postValue(message2)
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
                mValidProgressInit.postValue(false)
            }
        }

    }


    /** --------------------------------Picking 2 ViewModelFactory------------------------------------ */
    class Picking1ViewModelFactory1 constructor(private val repository: PickingRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(PickingViewModelInit1::class.java)) {
                PickingViewModelInit1(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}