package com.documentos.wms_beirario.ui.unmountingVolumes.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.desmontagemVol.RequestDisassamblyVol
import com.documentos.wms_beirario.model.desmontagemVol.ResponseUnMountingFinish
import com.documentos.wms_beirario.repository.desmontagemvolumes.DisassemblyRepository
import com.documentos.wms_beirario.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class ViewModelInmounting2(private val mountingVolRepository: DisassemblyRepository) : ViewModel() {

    private var mSucess = SingleLiveEvent<ResponseUnMountingFinish>()
    val mSucessShow get() = mSucess

    private var mSucessReandingFinish = SingleLiveEvent<Unit>()
    val mSucessReandingFinishShow get() = mSucessReandingFinish

    private var mErrorHttp = SingleLiveEvent<String>()
    val mErrorHttpShow get() = mErrorHttp

    private var mErrorAll = SingleLiveEvent<String>()
    val mErrorAllShow get() = mErrorAll

    private var mProgress = SingleLiveEvent<Boolean>()
    val mProgressShow get() = mProgress

    init {
        mProgress.postValue(true)
    }


    fun getTaskDisassembly2(idEndereco: Int) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val request =
                    this@ViewModelInmounting2.mountingVolRepository.getDisassembly2(idEndereco)
                if (request.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        request.body().let { task ->
                            mSucess.postValue(task)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        val error = request.errorBody()!!.string()
                        val error2 = JSONObject(error).getString("message")
                        mErrorHttp.value = error2
                    }
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

    fun postReanding(body: RequestDisassamblyVol) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val request = this@ViewModelInmounting2.mountingVolRepository.postDisassembly3(body)
                if (request.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        request.body().let { task ->
                            mSucessReandingFinish.postValue(task)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        val error = request.errorBody()!!.string()
                        val error2 = JSONObject(error).getString("message")
                        mErrorHttp.value = error2
                    }
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

    /** --------------------------------ViewModelInmounting1ViewModelFactory------------------------------------ */
    class UnMounting1ViewModelFactory2 constructor(private val repository: DisassemblyRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ViewModelInmounting2::class.java)) {
                ViewModelInmounting2(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}