package com.documentos.wms_beirario.ui.picking.viewmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.picking.PickingRequest1
import com.documentos.wms_beirario.model.picking.PickingResponse2
import com.documentos.wms_beirario.model.picking.PickingResponse3
import com.documentos.wms_beirario.repository.picking.PickingRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class PickingViewModel2(private val mRepository: PickingRepository) : ViewModel() {


    private var mSucessPickingReturn = MutableLiveData<List<PickingResponse2>>()
    val mSucessPickingReturnShows: LiveData<List<PickingResponse2>>
        get() = mSucessPickingReturn

    //--------------------->
    private var mErrorPicking = MutableLiveData<String>()
    val mErrorPickingShow: LiveData<String>
        get() = mErrorPicking

    //--------------------->
    private var mValidProgressInit = MutableLiveData<Boolean>()
    val mValidProgressInitShow: LiveData<Boolean>
        get() = mValidProgressInit

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

    //LIVE DATA PARA BUSCAR OS ITENS E VALIDAR BUTTON -->
    private var mSucess = MutableLiveData<List<PickingResponse3>>()
    val mSucessShow: LiveData<List<PickingResponse3>>
        get() = mSucess

    //------------>
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    init {
        mValidProgressInit.postValue(true)
    }


    fun getItensPicking2(idArea: Int, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mValidProgressInit.postValue(true)
                val request = this@PickingViewModel2.mRepository.getItensPicking2(
                    idArea = idArea,
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

    fun getItensPickingReanding2(
        idArea: Int,
        pickingRepository: PickingRequest1,
        idArmazem: Int,
        token: String
    ) {
        viewModelScope.launch {
            try {
                mValidProgressInit.postValue(true)
                val request = this@PickingViewModel2.mRepository.posPickingReanding2(
                    idArea = idArea,
                    pickingRepository = pickingRepository,
                    idArmazem,
                    token
                )
                if (request.isSuccessful) {
                    request.let { list ->
                        mSucessPickingRead.postValue(list.body())
                    }
                } else {
                    val message1 = request.errorBody()!!.string()
                    val message2 = JSONObject(message1).getString("message")
                    mErrorReadingPicking.postValue(message2)
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

    fun getItensPickingFinishValidadButton(idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                val request = this@PickingViewModel2.mRepository.getTaskPicking(idArmazem, token)
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
            }
        }
    }


    /** --------------------------------Picking 2 ViewModelFactory------------------------------------ */
    class Picking2ViewModelFactory constructor(private val repository: PickingRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(PickingViewModel2::class.java)) {
                PickingViewModel2(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}