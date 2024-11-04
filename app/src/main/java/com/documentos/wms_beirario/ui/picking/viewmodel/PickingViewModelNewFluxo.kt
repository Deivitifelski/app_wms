package com.documentos.wms_beirario.ui.picking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.picking.ResponsePickingReturnGrouped
import com.documentos.wms_beirario.model.picking.SendDataPicing1
import com.documentos.wms_beirario.repository.picking.PickingRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class PickingViewModelNewFluxo(private val mRepository: PickingRepository) : ViewModel() {


    private var mSucessPickingReturn = MutableLiveData<ResponsePickingReturnGrouped>()
    val mSucessPickingReturnShows: LiveData<ResponsePickingReturnGrouped>
        get() = mSucessPickingReturn

    //--------------------->
    private var mErrorPicking = MutableLiveData<String>()
    val mErrorPickingShow: LiveData<String>
        get() = mErrorPicking

    //--------------------->
    private var mValidProgressInit = MutableLiveData<Boolean>()
    val mValidProgressInitShow: LiveData<Boolean>
        get() = mValidProgressInit

    private var mValidProgressEdit = MutableLiveData<Boolean>()
    val mValidProgressEditShow: LiveData<Boolean>
        get() = mValidProgressEdit

    /**SUCESS READing -->*/
    private var mSucessPickingRead = MutableLiveData<Unit>()
    val mSucessPickingReadShow: LiveData<Unit>
        get() = mSucessPickingRead

    private var mErrorReadingPicking = MutableLiveData<String>()
    val mErrorReadingPickingShow: LiveData<String>
        get() = mErrorReadingPicking

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow: LiveData<String>
        get() = mErrorAll

    init {
        mValidProgressInit.postValue(true)
        mValidProgressEdit.postValue(false)
    }


    fun getItensPicking2(idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mValidProgressInit.postValue(true)
                val request = this@PickingViewModelNewFluxo.mRepository.getReturnGroupedProduct(
                    idArmazem,
                    token
                )
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

    /**LENDO DADOS -->*/
    fun reandingData(scanData: String, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mValidProgressEdit.postValue(true)
                val request =
                    this@PickingViewModelNewFluxo.mRepository.posReandingData(
                        SendDataPicing1(
                            scanData
                        ),
                        idArmazem,
                        token
                    )
                if (request.isSuccessful) {
                    request.let {
                        mSucessPickingRead.postValue(Unit)
                    }
                } else {
                    val message1 = request.errorBody()!!.string()
                    val message2 = JSONObject(message1).getString("message")
                    val msg3 = message2.replace("NAO", "NÃO")
                    mErrorReadingPicking.postValue(msg3)
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


    /** --------------------------------Picking 2 ViewModelFactory------------------------------------ */
    class Picking1ViewModelFactory constructor(private val repository: PickingRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(PickingViewModelNewFluxo::class.java)) {
                PickingViewModelNewFluxo(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}