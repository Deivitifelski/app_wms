package com.documentos.wms_beirario.ui.separacao

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.separation.ResponseListCheckBoxItem
import com.documentos.wms_beirario.model.separation.SeparationEnd
import com.documentos.wms_beirario.model.separation.SeparationListCheckBox
import com.documentos.wms_beirario.repository.SeparacaoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class SeparationEndViewModel(private val mRepository: SeparacaoRepository) : ViewModel() {

    private var mSucess02 = MutableLiveData<List<ResponseListCheckBoxItem>>()
    val mShowShow2: LiveData<List<ResponseListCheckBoxItem>>
        get() = mSucess02

    //-------------------------->
    private var mError2 = MutableLiveData<String>()
    val mErrorShow2: LiveData<String>
        get() = mError2

    //-------------------------->
    private var mValidationProgress = MutableLiveData<Boolean>()
    val mValidationProgressShow: LiveData<Boolean>
        get() = mValidationProgress

    //-------------------------->
    private var mSeparationEnd = MutableLiveData<String>()
    val mSeparationEndShow: LiveData<String>
        get() = mSeparationEnd

    //-------------------------->
    private var mErrorSeparationEnd = MutableLiveData<String>()
    val mErrorSeparationEndShow: LiveData<String>
        get() = mErrorSeparationEnd


    /**---------------------CHAMADA 02 LISTAS ----------------------------------------*/
    suspend fun postListCheck(listCheck: SeparationListCheckBox) {
        viewModelScope.launch(Dispatchers.Main) {
            mValidationProgress.value = true
        }
        viewModelScope.launch(Dispatchers.IO) {
            val request = this@SeparationEndViewModel.mRepository.postListCheckBox(
                listCheck
            )

            try {
                if (request.isSuccessful) {
                    viewModelScope.launch(Dispatchers.Main) {
                        mValidationProgress.value = false
                    }
                    mSucess02.postValue(request.body())
                } else {
                    viewModelScope.launch(Dispatchers.Main) {
                        mValidationProgress.value = false
                    }
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError2.postValue(messageEdit)

                }
            } catch (e: Exception) {
                viewModelScope.launch(Dispatchers.Main) {
                    mValidationProgress.value = false
                }

                mError2.postValue(e.toString())
            }
        }
    }


    suspend fun postSeparationEnd(separationEnd: SeparationEnd) {
        viewModelScope.launch(Dispatchers.IO) {
            val requestEnd =
                this@SeparationEndViewModel.mRepository.postSeparationEnd(separationEnd = separationEnd)
            try {
                if (requestEnd.isSuccessful) {
                    mSeparationEnd.postValue("")
                } else {
                    val error = requestEnd.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mErrorSeparationEnd.postValue(messageEdit)
                }

            } catch (e: Exception) {
                mErrorSeparationEnd.postValue(e.toString())
            }
        }
    }


    class ViewModelEndFactory constructor(private val repository: SeparacaoRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(SeparationEndViewModel::class.java)) {
                SeparationEndViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }

}