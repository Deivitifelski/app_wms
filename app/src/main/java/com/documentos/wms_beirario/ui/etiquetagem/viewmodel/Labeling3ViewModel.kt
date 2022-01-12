package com.documentos.wms_beirario.ui.etiquetagem.viewmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequestModel3
import com.documentos.wms_beirario.model.etiquetagem.response.EtiquetagemResponse3
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class Labeling3ViewModel(private val mRepository: EtiquetagemRepository) : ViewModel() {

    private var mSucess = MutableLiveData<List<EtiquetagemResponse3>>()
    val mSucessShow: LiveData<List<EtiquetagemResponse3>>
        get() = mSucess

    //--------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //------------>
    private var mValidProgress = MutableLiveData<Boolean>()
    val mValidProgressShow: LiveData<Boolean>
        get() = mValidProgress


    fun getLabeling3(etiquetagemRequestModel3: EtiquetagemRequestModel3) {
        viewModelScope.launch {
            try {
                val request =
                    this@Labeling3ViewModel.mRepository.labelingget3(etiquetagemRequestModel3 = etiquetagemRequestModel3)
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
                mError.postValue("Ops! Erro inesperado...")
            }
        }
    }
}