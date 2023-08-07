package com.documentos.wms_beirario.ui.mountingVol.viewmodels

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.mountingVol.RequestMounting5
import com.documentos.wms_beirario.model.mountingVol.ResponseMounting4
import com.documentos.wms_beirario.repository.mountingvol.MountingVolRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class MountingVolViewModel4(private val mRepository: MountingVolRepository) : ViewModel() {
    private var mSucess = MutableLiveData<ResponseMounting4>()
    val mShowShow: LiveData<ResponseMounting4>
        get() = mSucess

    //----------->
    private var mSucess5 = MutableLiveData<Unit>()
    val mShowShow5: LiveData<Unit>
        get() = mSucess5

    //----------->
    private var mError = MutableLiveData<String>()

    val mErrorShow: LiveData<String>
        get() = mError

    //---------------------------->
    private var mValidaProgress = MutableLiveData<Boolean>()

    val mValidaProgressShow: LiveData<Boolean>
        get() = mValidaProgress

    fun getProd(
        idOrdemMontagemVolume: String,
        idEnderecoOrigem: String,
        idArmazem: Int,
        token: String
    ) {
        viewModelScope.launch {
            try {
                mValidaProgress.postValue(true)
                val request = this@MountingVolViewModel4.mRepository.getProdMounting4(
                    idEnderecoOrigem = idEnderecoOrigem,
                    idOrdemMontagemVolume = idOrdemMontagemVolume,
                    idArmazem = idArmazem,
                    token = token
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

    fun addProdEan5(body: RequestMounting5, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mValidaProgress.postValue(true)
                val request = this@MountingVolViewModel4.mRepository.addProdEan5(
                    body5 = body,
                    idArmazem, token
                )
                if (request.isSuccessful) {
                    request.let { listSucess ->
                        mSucess5.postValue(listSucess.body())
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


    /** --------------------------------MONTAGEM DE VOL ViewModelFactory------------------------------------ */
    class Mounting4ViewModelFactory constructor(private val repository: MountingVolRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(MountingVolViewModel4::class.java)) {
                MountingVolViewModel4(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }

}