package com.documentos.wms_beirario.ui.recebimento

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.recebimento.RecebimentoDocTrans
import com.documentos.wms_beirario.model.recebimento.request.RecRequestCodBarras
import com.documentos.wms_beirario.repository.recebimento.ReceivementRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class RecebimentoViewModel(private val mReceivementRepository: ReceivementRepository) :
    ViewModel() {

    private var mSucessPostCodBarras1 = MutableLiveData<RecebimentoDocTrans>()
    val mSucessPostCodBarrasShow1: LiveData<RecebimentoDocTrans>
        get() = mSucessPostCodBarras1
    //------------->

    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError
    //------------->

    private var mProgressValid = MutableLiveData<Boolean>()
    val mProgressValidShow: LiveData<Boolean>
        get() = mProgressValid

    fun mReceivementRepository1(postDocumentoRequestRec1: RecRequestCodBarras) {
        viewModelScope.launch {
            val request = this@RecebimentoViewModel.mReceivementRepository.receivementPost1(
                postDocumentoRequestRec1 = postDocumentoRequestRec1
            )
            mProgressValid.value = true
            try {
                if (request.isSuccessful) {
                    mProgressValid.value = false
                    mSucessPostCodBarras1.postValue(request.body())
                } else {
                    mProgressValid.value = false
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }

            } catch (e: Exception) {
                mProgressValid.value = false
                mError.postValue(e.toString())
            }
        }
    }

    class RecebimentoFactory(private var receivementRepository: ReceivementRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(RecebimentoViewModel::class.java)) {
                RecebimentoViewModel(this.receivementRepository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }

    }
}