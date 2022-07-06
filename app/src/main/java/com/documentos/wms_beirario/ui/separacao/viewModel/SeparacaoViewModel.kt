package com.documentos.wms_beirario.ui.separacao.viewModel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.separation.ResponseItemsSeparationItem
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
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
    private var mValidaProgress = MutableLiveData<Boolean>()
    val mValidaProgressShow: LiveData<Boolean>
        get() = mValidaProgress


    /**---------------------CHAMADA 01 BUSCA DAS ESTANTES ----------------------------------------*/
    fun getItemsSeparation() {
        viewModelScope.launch {
            try {
                val request = this@SeparacaoViewModel.mRepository.getItemsSeparation()
                mValidaProgress.value = false
                if (request.isSuccessful) {
                    mValidaTxt.value = false
                    if (request.body().isNullOrEmpty()) {
                        mSucess.postValue(request.body())
                    } else {
                        mValidaTxt.value = true
                        mSucess.postValue(request.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }

            } catch (e: Exception) {
                mValidaProgress.value = false
                mValidaTxt.value = false
                mError.postValue("Ops! Erro inesperado...")
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