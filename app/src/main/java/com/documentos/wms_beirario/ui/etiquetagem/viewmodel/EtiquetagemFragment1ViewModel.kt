package com.documentos.wms_beirario.ui.etiquetagem.viewmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequest1
import com.documentos.wms_beirario.model.etiquetagem.ResponseEtiquetagemEdit1
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch
import org.json.JSONObject

class EtiquetagemFragment1ViewModel(private val mRepository: EtiquetagemRepository) : ViewModel() {


    private var mSucesss = MutableLiveData<ResponseEtiquetagemEdit1>()
    val mSucessShow: LiveData<ResponseEtiquetagemEdit1>
        get() = mSucesss

    //----------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //----------->
    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow: LiveData<String>
        get() = mErrorAll



    fun etiquetagemPost(etiquetagemRequest1: EtiquetagemRequest1, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                val request =
                    this@EtiquetagemFragment1ViewModel.mRepository.labelingPost1(
                        etiquetagemRequest1,
                        idArmazem,
                        token
                    )
                if (request.isSuccessful) {
                    mSucesss.postValue(request.body())
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val message = error2.replace("nao", "não").replace("CODIGO", "CÓDIGO")
                        .replace("INVALIDO", "INVÁLIDO")
                    mError.postValue(message)
                }
            } catch (e: Exception) {
                mErrorAll.postValue(validaErrorException(e = e))
            } finally {
            }
        }
    }

    /** --------------------------------Etiquetagem 1 ViewModelFactory------------------------------------ */
    class Etiquetagem1ViewModelFactory constructor(private val repository: EtiquetagemRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(EtiquetagemFragment1ViewModel::class.java)) {
                EtiquetagemFragment1ViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}