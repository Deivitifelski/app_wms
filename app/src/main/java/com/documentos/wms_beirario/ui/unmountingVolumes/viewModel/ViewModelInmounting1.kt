package com.documentos.wms_beirario.ui.unmountingVolumes.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.armazens.ArmazensResponse
import com.documentos.wms_beirario.model.desmontagemVol.UnmountingVolumes1
import com.documentos.wms_beirario.repository.armazens.ArmazensRepository
import com.documentos.wms_beirario.repository.desmontagemvolumes.DisassemblyRepository
import com.documentos.wms_beirario.repository.mountingvol.MountingVolRepository
import com.documentos.wms_beirario.ui.armazens.ArmazemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class ViewModelInmounting1(private val mountingVolRepository: DisassemblyRepository) : ViewModel() {

    private var mSucess = MutableLiveData<UnmountingVolumes1>()
    val mSucessShow get() = mSucess

    private var mErrorHttp = MutableLiveData<String>()
    val mErrorHttpShow get() = mErrorHttp

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow get() = mErrorAll

    private var mProgress = MutableLiveData<Boolean>()
    val mProgressShow get() = mProgress

    init {
        mProgress.postValue(true)
    }


    fun getTaskDisassembly1() {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val request = this@ViewModelInmounting1.mountingVolRepository.getDisassembly1()
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

    /** --------------------------------ViewModelInmounting1ViewModelFactory------------------------------------ */
    class UnMounting1ViewModelFactory constructor(private val repository: DisassemblyRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ViewModelInmounting1::class.java)) {
                ViewModelInmounting1(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}