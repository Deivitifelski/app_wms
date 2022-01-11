package com.documentos.wms_beirario.ui.picking.viewmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.picking.PickingRequest1
import com.documentos.wms_beirario.model.picking.PickingResponse2
import com.documentos.wms_beirario.repository.picking.PickingRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class PickingViewModel2(private val mRepository: PickingRepository) : ViewModel() {

    private var mSucessPicking = MutableLiveData<List<PickingResponse2>>()
    val mSucessPickingShow: LiveData<List<PickingResponse2>>
        get() = mSucessPicking
    //--------------------->
    private var mErrorPicking = MutableLiveData<String>()
    val mErrorPickingShow: LiveData<String>
        get() = mErrorPicking

    //--------------------->
    private var mValidProgress = MutableLiveData<Boolean>()
    val mValidProgressShow: LiveData<Boolean>
        get() = mValidProgress

    /**SUCESS READ -->*/
    private var mSucessPickingRead = MutableLiveData<Unit>()
    val mSucessPickingReadShow: LiveData<Unit>
        get() = mSucessPickingRead

    private var mErrorReadingPicking = MutableLiveData<String>()
    val mErrorReadingPickingShow: LiveData<String>
        get() = mErrorReadingPicking


    fun getItensPicking2(idArea: Int) {
        viewModelScope.launch {
            try {
                val request = this@PickingViewModel2.mRepository.getItensPicking2(idArea)
                if (request.isSuccessful) {
                    mValidProgress.value = false
                    request.let { list ->
                            mSucessPicking.postValue(list.body())
                        }
                } else {
                    val message1 = request.errorBody()!!.string()
                    val message2 = JSONObject(message1).getString("message")
                    mErrorPicking.postValue(message2)
                }
            } catch (e: Exception) {
                mValidProgress.value = false
                mErrorPicking.postValue("Ops! Erro inesperado...")
            }
        }

    }

    //----------POST------>
    fun postPickingRead(idArea: Int, pickingRepository: PickingRequest1) {
        viewModelScope.launch {
            val requestRead =
                this@PickingViewModel2.mRepository.posPicking(idArea, pickingRepository)
            try {
                if (requestRead.isSuccessful) {
                    requestRead.let {
                        mSucessPickingRead.postValue(requestRead.body())
                    }
                } else {
                    val message1 = requestRead.errorBody()!!.string()
                    val message2 = JSONObject(message1).getString("message")
                    val messageEdit = message2.replace("JA","JÁ").replace("NAO","NÃO")
                    mErrorReadingPicking.postValue(messageEdit)
                }

            } catch (e: Exception) {
                mErrorReadingPicking.postValue(e.toString())
            }
        }
    }
}