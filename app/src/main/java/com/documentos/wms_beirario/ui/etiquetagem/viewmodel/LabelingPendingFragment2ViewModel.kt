package com.documentos.wms_beirario.ui.etiquetagem.viewmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.etiquetagem.response.EtiquetagemResponse2
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class LabelingPendingFragment2ViewModel(private val mRepository: EtiquetagemRepository) :
    ViewModel() {


    private var mSucess = MutableLiveData<List<EtiquetagemResponse2>>()
    val mSucessShow: LiveData<List<EtiquetagemResponse2>>
        get() = mSucess

    //--------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //------------>
    private var mValidProgress = MutableLiveData<Boolean>()
    val mValidProgressShow: LiveData<Boolean>
        get() = mValidProgress

     fun getLabeling() {
        viewModelScope.launch {
            val request = this@LabelingPendingFragment2ViewModel.mRepository.labelingGet2()
            try {
                if (request.isSuccessful) {
                    mValidProgress.value = false
                    mSucess.postValue(request.body())
                } else {
                    mValidProgress.value = false
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val message = error2.replace("nao", "não")
                        .replace("INVALIDO", "INVÁLIDO")
                    mError.postValue(message)
                }
            } catch (e: Exception) {
                mValidProgress.value = false
                mError.value = e.toString()
            }
        }

    }

    /**FACTORY--->*/
    class PendingLabelingFactoryBarCode constructor(private val repository: EtiquetagemRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(LabelingPendingFragment2ViewModel::class.java)) {
                LabelingPendingFragment2ViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}