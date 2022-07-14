package com.documentos.wms_beirario.ui.mountingVol.viewmodels

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.mountingVol.MountingTaskResponse1
import com.documentos.wms_beirario.model.mountingVol.ResponseAndressMonting3
import com.documentos.wms_beirario.model.mountingVol.ResponseMounting2
import com.documentos.wms_beirario.repository.mountingvol.MountingVolRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class MountingVolViewModel2(private val mRepository: MountingVolRepository) : ViewModel() {
    private var mSucess = MutableLiveData<ResponseMounting2>()
    val mShowShow: LiveData<ResponseMounting2>
        get() = mSucess

    //-------------------------->
    private var mSucess2 = MutableLiveData<ResponseAndressMonting3>()
    val mShowShow2: LiveData<ResponseAndressMonting3>
        get() = mSucess2

    //----------->
    private var mError = MutableLiveData<String>()

    val mErrorShow: LiveData<String>
        get() = mError

    //---------------------------->
    private var mValidaProgress = MutableLiveData<Boolean>()

    val mValidaProgressShow: LiveData<Boolean>
        get() = mValidaProgress

    fun getNumSerieVol(kitProd: String) {
        viewModelScope.launch {
            try {
                mValidaProgress.postValue(true)
                val request = this@MountingVolViewModel2.mRepository.getVolMounting2(kitProd)
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

                mError.postValue("Ops! Erro inesperado...")
            } finally {
                mValidaProgress.postValue(false)
            }
        }
    }

    fun getAndressVol(idOrdemMontagemVolume: String) {
        viewModelScope.launch {
            try {
                mValidaProgress.postValue(true)
                val request =
                    this@MountingVolViewModel2.mRepository.getAndressMounting2(idOrdemMontagemVolume = idOrdemMontagemVolume)
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

                mError.postValue("Ops! Erro inesperado...")
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