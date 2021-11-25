package com.documentos.wms_beirario.ui.separacao

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.separation.ResponseItemsSeparationItem
import com.documentos.wms_beirario.model.separation.ResponseListCheckBoxItem
import com.documentos.wms_beirario.model.separation.SeparationListCheckBox
import com.documentos.wms_beirario.repository.SeparacaoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class SeparacaoViewModel(private val mRepository: SeparacaoRepository) : ViewModel() {


    //-------------------------->
    private var mSucess = MutableLiveData<List<ResponseItemsSeparationItem>>()
    val mShowShow: LiveData<List<ResponseItemsSeparationItem>>
        get() = mSucess

    //-------------------------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError
    //-------------------------->
    private var mValidaTxt = MutableLiveData<Boolean>()
    val mValidaTxtShow: LiveData<Boolean>
        get() = mValidaTxt
    //--------------------------->

    /**---------------------CHAMADA 01 BUSCA DAS ESTANTES ----------------------------------------*/
    fun getItemsSeparation() {
        viewModelScope.launch(Dispatchers.IO) {
            val request = this@SeparacaoViewModel.mRepository.getItemsSeparation()
            try {
                if (request.isSuccessful) {
                    if (request.body().isNullOrEmpty()){
                        mError.postValue("Lista Vazia!")
                        viewModelScope.launch(Dispatchers.Main){
                            mValidaTxt.value = false
                        }
                    }else {
                        viewModelScope.launch(Dispatchers.Main){
                            mValidaTxt.value = true
                        }
                        mSucess.postValue(request.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }

            } catch (e: Exception) {
                mError.postValue(e.toString())
            }
        }
    }


    class SeparacaoItensViewModelFactory constructor(private val repository: SeparacaoRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(SeparacaoViewModel::class.java)) {
                SeparacaoViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }


}