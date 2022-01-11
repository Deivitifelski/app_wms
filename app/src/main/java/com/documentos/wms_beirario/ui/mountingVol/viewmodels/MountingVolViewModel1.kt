package com.documentos.wms_beirario.ui.mountingVol.viewmodels

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.mountingVol.MountingTaskResponse1
import com.documentos.wms_beirario.repository.mountingvol.MountingVolRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

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


    fun getMounting1() {
        viewModelScope.launch {
            try {
                val request = this@MountingVolViewModel1.mRepository.getApi()
                mValidaProgress.value = false
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
                mValidaProgress.value = false
                mError.postValue("Ops! Erro inesperado...")
            }
        }

    }

}