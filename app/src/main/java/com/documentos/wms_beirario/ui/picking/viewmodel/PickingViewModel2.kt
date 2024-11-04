package com.documentos.wms_beirario.ui.picking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.picking.PickingRequest1
import com.documentos.wms_beirario.model.picking.PickingResponse2
import com.documentos.wms_beirario.model.picking.PickingResponse3
import com.documentos.wms_beirario.repository.picking.PickingRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class PickingViewModel2(private val mRepository: PickingRepository) : ViewModel() {


    private var sucessVolumesApontados = MutableLiveData<List<PickingResponse2>>()
    val sucessVolumesApontadosShow: LiveData<List<PickingResponse2>>
        get() = sucessVolumesApontados

    private var sucessVolumesNaoApontados = MutableLiveData<List<PickingResponse2>>()
    val sucessVolumesNaoApontadosShow: LiveData<List<PickingResponse2>>
        get() = sucessVolumesNaoApontados

    //--------------------->
    private var errorPicking = MutableLiveData<String>()
    val mErrorPickingShow: LiveData<String>
        get() = errorPicking

    //--------------------->
    private var progress = MutableLiveData<Boolean>()
    val mValidProgressInitShow: LiveData<Boolean>
        get() = progress

    /**SUCESS READing -->*/
    private var mSucessPickingRead = MutableLiveData<Unit>()
    val sucessReandingPicking: LiveData<Unit>
        get() = mSucessPickingRead

    private var mErrorReadingPicking = MutableLiveData<String>()
    val mErrorReadingPickingShow: LiveData<String>
        get() = mErrorReadingPicking

    private var errorAll = MutableLiveData<String>()
    val mErrorAllShow: LiveData<String>
        get() = errorAll

    //LIVE DATA PARA BUSCAR OS ITENS E VALIDAR BUTTON -->
    private var mSucess = MutableLiveData<List<PickingResponse3>>()
    val mSucessShow: LiveData<List<PickingResponse3>>
        get() = mSucess

    //------------>
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    init {
        progress.postValue(true)
    }


    fun getVolApontados(idArea: Int, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                progress.postValue(true)
                val request = this@PickingViewModel2.mRepository.getVolApontados(
                    idArea = idArea,
                    idArmazem,
                    token
                )
                if (request.isSuccessful) {
                    request.let { list ->
                        sucessVolumesApontados.postValue(list.body())
                    }
                } else {
                    val message1 = request.errorBody()!!.string()
                    val message2 = JSONObject(message1).getString("message")
                    errorPicking.postValue(message2)
                }
            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        errorAll.postValue("Verifique sua internet!")
                    }

                    is SocketTimeoutException -> {
                        errorAll.postValue("Tempo de conexão excedido, tente novamente!")
                    }

                    is TimeoutException -> {
                        errorAll.postValue("Tempo de conexão excedido, tente novamente!")
                    }

                    else -> {
                        errorAll.postValue(e.toString())
                    }
                }
            } finally {
                progress.postValue(false)
            }
        }
    }


    fun getVolNaoApontados(idArea: Int, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                progress.postValue(true)
                val request = this@PickingViewModel2.mRepository.getVolNaoApontados(
                    idArea = idArea,
                    idArmazem,
                    token
                )
                if (request.isSuccessful) {
                    request.let { list ->
                        sucessVolumesNaoApontados.postValue(list.body())
                    }
                } else {
                    errorPicking.postValue(validaErrorDb(request))
                }
            } catch (e: Exception) {
                errorAll.postValue(validaErrorException(e))
            } finally {
                progress.postValue(false)
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
                progress.postValue(true)
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
                        errorAll.postValue("Verifique sua internet!")
                    }

                    is SocketTimeoutException -> {
                        errorAll.postValue("Tempo de conexão excedido, tente novamente!")
                    }

                    is TimeoutException -> {
                        errorAll.postValue("Tempo de conexão excedido, tente novamente!")
                    }

                    else -> {
                        errorAll.postValue(e.toString())
                    }
                }
            } finally {
                progress.postValue(false)
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
                        errorAll.postValue("Verifique sua internet!")
                    }

                    is SocketTimeoutException -> {
                        errorAll.postValue("Tempo de conexão excedido, tente novamente!")
                    }

                    is TimeoutException -> {
                        errorAll.postValue("Tempo de conexão excedido, tente novamente!")
                    }

                    else -> {
                        errorAll.postValue(e.toString())
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