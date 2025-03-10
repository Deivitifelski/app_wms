package com.documentos.wms_beirario.ui.receipt

import ReceiptRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode2
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode3
import com.documentos.wms_beirario.model.recebimento.request.PostReciptQrCode1
import com.documentos.wms_beirario.model.recebimento.response.ReceiptDoc1
import com.documentos.wms_beirario.utils.SingleLiveEvent
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class ReceiptViewModel(private val mReceiptRepository: ReceiptRepository) :
    ViewModel() {

    private var mSucessPostCodBarras1 = MutableLiveData<ReceiptDoc1>()
    val mSucessPostCodBarrasShow1: LiveData<ReceiptDoc1>
        get() = mSucessPostCodBarras1

    //------------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    private var mErrorFinish = MutableLiveData<String>()
    val mErrorFinishShow: LiveData<String>
        get() = mErrorFinish


    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow: LiveData<String>
        get() = mErrorAll

    //------------->
    private var mProgressValid = MutableLiveData<Boolean>()
    val mProgressValidShow: LiveData<Boolean>
        get() = mProgressValid


    //---------->
    private var mSucessPostCodBarras2 = MutableLiveData<ReceiptDoc1>()
    val mSucessPostCodBarrasShow2: LiveData<ReceiptDoc1>
        get() = mSucessPostCodBarras2

    //---------->
    private var mSucessPostCodBarras3 = SingleLiveEvent<String>()
    val mSucessPostCodBarrasShow3: SingleLiveEvent<String>
        get() = mSucessPostCodBarras3

    fun mReceiptPost1(postDocumentoRequestRec1: PostReciptQrCode1, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mProgressValid.postValue(true)
                val request = this@ReceiptViewModel.mReceiptRepository.receiptPost1(
                    postDocumentoRequestRec1 = postDocumentoRequestRec1,
                    idArmazem,
                    token
                )
                if (request.isSuccessful) {
                    mSucessPostCodBarras1.postValue(request.body())
                } else {
                    mError.postValue(validaErrorDb(request))
                }
            } catch (e: Exception) {
                mErrorAll.postValue(validaErrorException(e))
            } finally {
                mProgressValid.postValue(false)
            }
        }
    }

    fun mReceiptPost2(
        mIdTarefa: String? = null,
        postReceiptQrCode2: PostReceiptQrCode2,
        idArmazem: Int,
        token: String
    ) {
        viewModelScope.launch {
            try {
                mProgressValid.postValue(true)
                val request2 = this@ReceiptViewModel.mReceiptRepository.receiptPost2(
                    mIdTarefa.toString(),
                    postReceiptQrCode2,
                    idArmazem,
                    token
                )
                if (request2.isSuccessful) {
                    mSucessPostCodBarras2.postValue(request2.body())
                } else {
                    val error = request2.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }
            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mErrorAll.postValue("Verifique sua internet!")
                    }

                    is SocketTimeoutException -> {
                        mErrorAll.postValue("Tempo de conexão excedido, tente novamente!")
                    }

                    is TimeoutException -> {
                        mErrorAll.postValue("Tempo de conexão excedido, tente novamente!")
                    }

                    else -> {
                        mErrorAll.postValue(e.toString())
                    }
                }
            } finally {
                mProgressValid.postValue(false)
            }
        }
    }

    fun postReceipt3(
        mIdTarefaConferencia: String,
        postReceiptQrCode3: PostReceiptQrCode3,
        idArmazem: Int,
        token: String
    ) {
        viewModelScope.launch {
            try {
                mProgressValid.postValue(true)
                val request3 = this@ReceiptViewModel.mReceiptRepository.receiptPost3(
                    mIdTarefaConferencia,
                    postReceiptQrCode3,
                    idArmazem,
                    token
                )
                if (request3.isSuccessful) {
                    mSucessPostCodBarras3.postValue(request3.body()!!.mensagemFinal)
                } else {
                    val error = request3.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mErrorFinish.postValue(messageEdit)
                }
            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mErrorFinish.postValue("Verifique sua internet!")
                    }

                    is SocketTimeoutException -> {
                        mErrorFinish.postValue("Tempo de conexão excedido, tente novamente!")
                    }

                    is TimeoutException -> {
                        mErrorFinish.postValue("Tempo de conexão excedido, tente novamente!")
                    }

                    else -> {
                        mErrorFinish.postValue(e.toString())
                    }
                }
            } finally {
                mProgressValid.postValue(false)
            }
        }
    }

    /** --------------------------------Recebimento ViewModelFactory------------------------------------ */
    class ReceiptViewModelFactory constructor(private val repository: ReceiptRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ReceiptViewModel::class.java)) {
                ReceiptViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}