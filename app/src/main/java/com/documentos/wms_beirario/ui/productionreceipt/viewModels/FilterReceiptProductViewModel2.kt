package com.documentos.wms_beirario.ui.productionreceipt.viewModels

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.receiptproduct.ReceiptProduct1
import com.documentos.wms_beirario.repository.receiptproduct.ReceiptProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class FilterReceiptProductViewModel2(private val mRepository: ReceiptProductRepository) :
    ViewModel() {

    private var mSucessReceipt = MutableLiveData<List<ReceiptProduct1>>()
    val mSucessReceiptShow: LiveData<List<ReceiptProduct1>>
        get() = mSucessReceipt

    //----------->
    private var mErrorReceipt = MutableLiveData<String>()
    val mErrorReceiptShow: LiveData<String>
        get() = mErrorReceipt

    //----------->
    private var mValidaProgressReceipt = MutableLiveData<Boolean>()
    val mValidaProgressReceiptShow: LiveData<Boolean>
        get() = mValidaProgressReceipt


    fun getReceipt1(filtrarOperador: Boolean, mIdOperador: String) {
        viewModelScope.launch {
            try {
                mValidaProgressReceipt.value = false
                val request = this@FilterReceiptProductViewModel2.mRepository.getReceiptProduct1(
                    filtrarOperador,
                    mIdOperador
                )
                if (request.isSuccessful) {
                    request.let { list ->
                        mSucessReceipt.postValue(list.body())
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        val error = request.errorBody()!!.string()
                        val error2 = JSONObject(error).getString("message")
                        val messageEdit = error2.replace("NAO", "NÃO")
                        mErrorReceipt.postValue(messageEdit)
                    }

                }
            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mErrorReceipt.postValue("ConnectException\nVerifique sua internet!")
                    }
                    is SocketTimeoutException -> {
                        mErrorReceipt.postValue("SocketTimeoutException\nTempo de conexão excedido, tente novamente!")
                    }
                    is TimeoutException -> {
                        mErrorReceipt.postValue("TimeoutException\nTempo de conexão excedido, tente novamente!")
                    }
                    else -> {
                        mErrorReceipt.postValue(e.toString())
                    }
                }
            } finally {
                mValidaProgressReceipt.postValue(false)
            }
        }
    }


    /** --------------------------------RecebimentoDeProduçãoViewModelFactory------------------------------------ */
    class ReceiptProductViewModel1Factory2 constructor(private val repository: ReceiptProductRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(FilterReceiptProductViewModel2::class.java)) {
                FilterReceiptProductViewModel2(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}
