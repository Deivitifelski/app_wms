package com.documentos.wms_beirario.ui.mountingVol.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.mountingVol.RequestMounting6
import com.documentos.wms_beirario.model.mountingVol.ResponseAndressMonting3
import com.documentos.wms_beirario.model.mountingVol.ResponseMounting2
import com.documentos.wms_beirario.model.mountingVol.ResponsePrinterMountingVol
import com.documentos.wms_beirario.repository.mountingvol.MountingVolRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class MountingVolViewModel2(private val mRepository: MountingVolRepository) : ViewModel() {
    private var mSucess = MutableLiveData<ResponseMounting2>()
    val mShowShow: LiveData<ResponseMounting2>
        get() = mSucess

    //-------------------------->
    private var mSucess2 = MutableLiveData<ResponseAndressMonting3>()
    val mShowShow2: LiveData<ResponseAndressMonting3>
        get() = mSucess2


    private var mSucessPrinter = MutableLiveData<ResponsePrinterMountingVol>()
    val mSucessPrinterShow: LiveData<ResponsePrinterMountingVol>
        get() = mSucessPrinter

    private var mSucessPrinterUnica = MutableLiveData<Unit>()
    val mSucessPrinterUnicaShow: LiveData<Unit>
        get() = mSucessPrinterUnica

    //----------->
    private var mError = MutableLiveData<String>()

    val mErrorShow: LiveData<String>
        get() = mError

    //---------------------------->
    private var mValidaProgress = MutableLiveData<Boolean>()

    val mValidaProgressShow: LiveData<Boolean>
        get() = mValidaProgress

    fun getNumSerieVol(kitProd: String, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mValidaProgress.postValue(true)
                val request = this@MountingVolViewModel2.mRepository.getVolMounting2(
                    kitProd,
                    idArmazem,
                    token
                )
                if (request.isSuccessful) {
                    request.let { listSucess ->
                        mSucess.postValue(listSucess.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }

            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mError.postValue("Verifique sua internet!")
                    }
                    is SocketTimeoutException -> {
                        mError.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    is TimeoutException -> {
                        mError.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    else -> {
                        mError.postValue(e.toString())
                    }
                }
            } finally {
                mValidaProgress.postValue(false)
            }
        }
    }

    fun getAndressVol(idOrdemMontagemVolume: String, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mValidaProgress.postValue(true)
                val request =
                    this@MountingVolViewModel2.mRepository.getAndressMounting3(
                        idOrdemMontagemVolume = idOrdemMontagemVolume,
                        idArmazem,
                        token
                    )
                if (request.isSuccessful) {
                    request.let { listSucess ->
                        mSucess2.postValue(listSucess.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }

            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mError.postValue("Verifique sua internet!")
                    }
                    is SocketTimeoutException -> {
                        mError.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    is TimeoutException -> {
                        mError.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    else -> {
                        mError.postValue(e.toString())
                    }
                }
            } finally {
                mValidaProgress.postValue(false)
            }
        }
    }

    //CHAMADA PARA IMPRIMIR -->
    fun getPrinterMounting1(idOrdemMontagemVolume: String, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                val request = this@MountingVolViewModel2.mRepository.getApiPrinterMounting(
                    idOrdemMontagemVolume = idOrdemMontagemVolume,
                    idArmazem, token
                )
                mValidaProgress.value = true
                if (request.isSuccessful) {
                    request.let { listSucess ->
                        mSucessPrinter.postValue(listSucess.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }

            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mError.postValue("Verifique sua internet!")
                    }
                    is SocketTimeoutException -> {
                        mError.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    is TimeoutException -> {
                        mError.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    else -> {
                        mError.postValue(e.toString())
                    }
                }
            } finally {
                mValidaProgress.postValue(false)
            }
        }
    }

    //REIMPRESSÃO UNICA -->
    fun setImpressaoUni(body: RequestMounting6, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                val request = this@MountingVolViewModel2.mRepository.setImpressaoUnica(
                    body = body,
                    idArmazem, token
                )
                mValidaProgress.value = true
                if (request.isSuccessful) {
                    request.let { listSucess ->
                        mSucessPrinterUnica.postValue(listSucess.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }

            } catch (e: Exception) {
                mError.postValue(validaErrorException(e))
            } finally {
                mValidaProgress.postValue(false)
            }
        }
    }


    /** --------------------------------MONTAGEM DE VOL ViewModelFactory------------------------------------ */
    class Mounting2ViewModelFactory constructor(private val repository: MountingVolRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(MountingVolViewModel2::class.java)) {
                MountingVolViewModel2(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }

}