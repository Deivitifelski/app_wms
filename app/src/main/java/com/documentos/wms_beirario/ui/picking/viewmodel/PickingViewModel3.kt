package com.documentos.wms_beirario.ui.picking.viewmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.picking.PickingRequest2
import com.documentos.wms_beirario.model.picking.PickingResponse3
import com.documentos.wms_beirario.repository.picking.PickingRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class PickingViewModel3(private val mRepository: PickingRepository) : ViewModel() {

    private var mSucess = MutableLiveData<List<PickingResponse3>>()
    val mSucessShow: LiveData<List<PickingResponse3>>
        get() = mSucess

    //------------>
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //------------->
    private var mValidProgress = MutableLiveData<Boolean>()
    val mValidProgressShow: LiveData<Boolean>
        get() = mValidProgress

    //-------FINISH READING------------>
    private var mSucessReading = MutableLiveData<Unit>()
    val mSucessReadingShow: LiveData<Unit>
        get() = mSucessReading

    //------------>
    private var mErrorReading = MutableLiveData<String>()
    val mErrorReadingShow: LiveData<String>
        get() = mErrorReading


    fun getItensPicking() {
        viewModelScope.launch {
            try {
                val request = this@PickingViewModel3.mRepository.getTaskPicking()
                mValidProgress.value = false
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
                mValidProgress.value = false
                mError.postValue("Ops! Erro inesperado...")
            }
        }
    }

    //----------------------TASK FINISH----------------->
    fun finishTaskPicking(pickingRequest2: PickingRequest2) {
        viewModelScope.launch {
            val request =
                this@PickingViewModel3.mRepository.postPickinFinish(pickingRequest2 = pickingRequest2)
            try {
                if (request.isSuccessful) {
                    request.let {
                        mSucessReading.postValue(request.body())
                    }
                } else {
                    val message1 = request.errorBody()!!.string()
                    val message2 = JSONObject(message1).getString("message")
                    val messageEdit = message2.replace("ENDERECO", "ENDEREÇO").replace("NAO", "NÃO")
                    mErrorReading.postValue(messageEdit)
                }
            } catch (e: Exception) {
                mErrorReading.postValue(e.toString())
            }
        }
    }
}