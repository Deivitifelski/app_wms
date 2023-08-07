package com.documentos.wms_beirario.ui.mountingVol.viewmodels

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.mountingVol.MountingTaskResponse1
import com.documentos.wms_beirario.repository.mountingvol.MountingVolRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class MountingVolViewModel1(private val mRepository: MountingVolRepository) : ViewModel() {

    private var mSucess = MutableLiveData<List<MountingTaskResponse1>>()
    val mShowShow: LiveData<List<MountingTaskResponse1>>
        get() = mSucess


    //-------------------------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //---------------------------->
    private var mValidaProgress = MutableLiveData<Boolean>()
    val mValidaProgressShow: LiveData<Boolean>
        get() = mValidaProgress


    fun getMounting1(idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                val request = this@MountingVolViewModel1.mRepository.getApi(idArmazem, token)
                mValidaProgress.value = true
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


    /** --------------------------------MONTAGEM DE VOL ViewModelFactory------------------------------------ */
    class MountingViewModelFactory constructor(private val repository: MountingVolRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(MountingVolViewModel1::class.java)) {
                MountingVolViewModel1(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }

}