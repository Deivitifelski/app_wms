package com.documentos.wms_beirario.ui.picking.viewmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.picking.PickingResponse1
import com.documentos.wms_beirario.repository.picking.PickingRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class PickingViewModel1(private val mRepository: PickingRepository) : ViewModel() {

    private var mSucessPicking = MutableLiveData<List<PickingResponse1>>()
    val mSucessPickingShow: LiveData<List<PickingResponse1>>
        get() = mSucessPicking

    //--------------------->
    private var mErrorPicking = MutableLiveData<String>()
    val mErrorPickingShow: LiveData<String>
        get() = mErrorPicking

    //--------------------->
    private var mValidProgress = MutableLiveData<Boolean>()
    val mValidProgressShow: LiveData<Boolean>
        get() = mValidProgress

    fun getPicking() {
        viewModelScope.launch {
            val request = this@PickingViewModel1.mRepository.getAreaPicking1()
            try {
                if (request.isSuccessful) {
                    mValidProgress.value = false
                    request.let {
                        mSucessPicking.postValue(request.body())
                    }
                } else {
                    val message1 = request.errorBody()!!.string()
                    val message2 = JSONObject(message1).getString("message")
                    mErrorPicking.postValue(message2)
                }

            } catch (e: Exception) {
                mValidProgress.value = false
                mErrorPicking.postValue(e.toString())
            }
        }
    }


    class PickingViewModelFactory constructor(private val repository: PickingRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(PickingViewModel1::class.java)) {
                PickingViewModel1(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}