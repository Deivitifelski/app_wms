package com.documentos.wms_beirario.ui.recebimento

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.recebimento.ReceiptDoc1
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode2
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode3
import com.documentos.wms_beirario.model.recebimento.request.PostReciptQrCode1
import com.documentos.wms_beirario.repository.recebimento.ReceiptRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class ReceiptViewModel(private val mReceiptRepository: ReceiptRepository) :
    ViewModel() {

    private var mSucessPostCodBarras1 = MutableLiveData<ReceiptDoc1>()
    val mSucessPostCodBarrasShow1: LiveData<ReceiptDoc1>
        get() = mSucessPostCodBarras1

    //------------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //------------->
    private var mProgressValid = MutableLiveData<Boolean>()
    val mProgressValidShow: LiveData<Boolean>
        get() = mProgressValid

    //---------->
    private var mSucessPostCodBarras2 = MutableLiveData<ReceiptDoc1>()
    val mSucessPostCodBarrasShow2: LiveData<ReceiptDoc1>
        get() = mSucessPostCodBarras2

    //---------->
    private var mSucessPostCodBarras3 = MutableLiveData<String>()
    val mSucessPostCodBarrasShow3: LiveData<String>
        get() = mSucessPostCodBarras3

    fun mReceiptPost1(postDocumentoRequestRec1: PostReciptQrCode1) {
        viewModelScope.launch {
            try {
                val request = this@ReceiptViewModel.mReceiptRepository.receiptPost1(
                    postDocumentoRequestRec1 = postDocumentoRequestRec1
                )
                mProgressValid.value = false
                if (request.isSuccessful) {
                    mSucessPostCodBarras1.postValue(request.body())
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }
            } catch (e: Exception) {
                mProgressValid.value = false
                mError.postValue("Ops! Erro inesperado...")
            }
        }
    }

    fun mReceiptPost2(mIdTarefa: String? = null, postReceiptQrCode2: PostReceiptQrCode2) {
        viewModelScope.launch {
            try {
                val request2 =
                    this@ReceiptViewModel.mReceiptRepository.receiptPost2(mIdTarefa.toString(), postReceiptQrCode2)
                if (request2.isSuccessful) {
                    mSucessPostCodBarras2.postValue(request2.body())
                } else {
                    val error = request2.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }
            } catch (e: Exception) {
                mError.postValue(e.toString())
            }
        }
    }

    fun postReceipt3(mIdTarefaConferencia: String, postReceiptQrCode3: PostReceiptQrCode3) {
        viewModelScope.launch {
            try {
                val request3 = this@ReceiptViewModel.mReceiptRepository.receiptPost3(mIdTarefaConferencia, postReceiptQrCode3)
                if (request3.isSuccessful) {
                    mSucessPostCodBarras3.postValue(request3.body()!!.mensagemFinal)
                } else {
                    val error = request3.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }
            } catch (e: Exception) {
                mError.postValue(e.toString())
            }
        }
    }

    class RecebimentoFactory(private var receiptRepository: ReceiptRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ReceiptViewModel::class.java)) {
                ReceiptViewModel(this.receiptRepository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }

    }
}