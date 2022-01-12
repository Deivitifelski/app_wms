package com.documentos.wms_beirario.ui.etiquetagem.viewmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequest1
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class EtiquetagemFragment1ViewModel(private val mRepository: EtiquetagemRepository) : ViewModel() {


    private var mSucesss = MutableLiveData<String>()
    val mSucessShow: LiveData<String>
        get() = mSucessShow

    //----------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError


    fun etiquetagemPost(etiquetagemRequest1: EtiquetagemRequest1) {
        viewModelScope.launch {
            try {
                val request =
                    this@EtiquetagemFragment1ViewModel.mRepository.labelingPost1(etiquetagemRequest1)
                if (request.isSuccessful) {
                    mSucesss.postValue(request.body().toString())
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val message = error2.replace("nao", "não").replace("CODIGO", "CÓDIGO")
                        .replace("INVALIDO", "INVÁLIDO")
                    mError.postValue(message)

                }
            } catch (e: Exception) {
                mError.postValue("Ops! Erro inesperado...")
            }
        }
    }
}