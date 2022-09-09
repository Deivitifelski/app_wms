package com.documentos.wms_beirario.ui.receiptProduction.acrivitys.viewModels

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.receiptproduct.PostFinishReceiptProduct3
import com.documentos.wms_beirario.model.receiptproduct.ReceiptProduct2
import com.documentos.wms_beirario.repository.receiptproduct.ReceiptProductRepository
import com.documentos.wms_beirario.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class ReceiptProductViewModel2(val repository: ReceiptProductRepository) : ViewModel() {

    private var mValidadTxt = MutableLiveData<Boolean>()
    val mValidadTxtShow: LiveData<Boolean>
        get() = mValidadTxt
    //----------->

    private var mSucessReceipt2 = SingleLiveEvent<List<ReceiptProduct2>>()
    val mSucessReceiptShow2: SingleLiveEvent<List<ReceiptProduct2>>
        get() = mSucessReceipt2

    //----------->
    private var mErrorReceipt2 = SingleLiveEvent<String>()
    val mErrorReceiptShow2: SingleLiveEvent<String>
        get() = mErrorReceipt2

    //----------->
    private var mValidaProgressReceipt2 = SingleLiveEvent<Boolean>()
    val mValidaProgressReceiptShow2: SingleLiveEvent<Boolean>
        get() = mValidaProgressReceipt2

    //----------->
    private var mSucessFinish = SingleLiveEvent<Unit>()
    val mSucessFinishShow: SingleLiveEvent<Unit>
        get() = mSucessFinish

    //----------->
    private var mErrorFinish = SingleLiveEvent<String>()
    val mErrorFinishShow: SingleLiveEvent<String>
        get() = mErrorFinish

    //-------------------------->
    private var mSucessFinishtest = SingleLiveEvent<Unit>()
    val mSucessFinishShowtest: SingleLiveEvent<Unit>
        get() = mSucessFinishtest


    fun getItem(idOperador: String, filtrarOperario: Boolean, pedido: String) {
        viewModelScope.launch {
            try {
                val request =
                    this@ReceiptProductViewModel2.repository.getItemProduct2(
                        idOperador = idOperador,
                        filtrarOperador = filtrarOperario,
                        pedido = pedido
                    )
                mValidaProgressReceipt2.value = false
                if (request.isSuccessful) {
                    request.let { list ->
                        mSucessReceipt2.postValue(list.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mErrorReceipt2.postValue(messageEdit)
                }

            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mErrorReceipt2.postValue("ConnectException\nVerifique sua internet!")
                    }
                    is SocketTimeoutException -> {
                        mErrorReceipt2.postValue("SocketTimeoutException\nTempo de conexão excedido, tente novamente!")
                    }
                    is TimeoutException -> {
                        mErrorReceipt2.postValue("TimeoutException\nTempo de conexão excedido, tente novamente!")
                    }
                    else -> {
                        mErrorReceipt2.postValue(e.toString())
                    }
                }
            } finally {
                mValidaProgressReceipt2.postValue(false)
            }
        }
    }

    fun postFinishReceipt(postFinish: PostFinishReceiptProduct3) {
        viewModelScope.launch {
            try {
                val request =
                    this@ReceiptProductViewModel2.repository.postFinishReceiptProduct(postFinish = postFinish)
                if (request.isSuccessful) {
                    request.let { sucess ->
                        mSucessFinish.postValue(sucess.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO").replace("ENDERECO", "ENDEREÇO")
                    mErrorFinish.postValue(messageEdit)
                }
            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mErrorFinish.postValue("ConnectException\nVerifique sua internet!")
                    }
                    is SocketTimeoutException -> {
                        mErrorFinish.postValue("SocketTimeoutException\nTempo de conexão excedido, tente novamente!")
                    }
                    is TimeoutException -> {
                        mErrorFinish.postValue("TimeoutException\nTempo de conexão excedido, tente novamente!")
                    }
                    else -> {
                        mErrorFinish.postValue(e.toString())
                    }
                }
            }
        }
    }

    /** --------------------------------RecebimentoDeProduçãoViewModelFactory------------------------------------ */
    class ReceiptProductViewModel1Factory2 constructor(private val repository: ReceiptProductRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ReceiptProductViewModel2::class.java)) {
                ReceiptProductViewModel2(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}