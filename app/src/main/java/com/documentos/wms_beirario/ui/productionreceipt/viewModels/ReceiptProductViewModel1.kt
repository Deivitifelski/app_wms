package com.documentos.wms_beirario.ui.productionreceipt.viewModels

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.receiptproduct.PosLoginValidadREceipPorduct
import com.documentos.wms_beirario.model.receiptproduct.QrCodeReceipt1
import com.documentos.wms_beirario.model.receiptproduct.ReceiptIdOperador
import com.documentos.wms_beirario.model.receiptproduct.ReceiptProduct1
import com.documentos.wms_beirario.repository.armazens.ArmazensRepository
import com.documentos.wms_beirario.repository.receiptproduct.ReceiptProductRepository
import com.documentos.wms_beirario.ui.armazens.ArmazemViewModel
import com.documentos.wms_beirario.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class ReceiptProductViewModel1(private val mRepository: ReceiptProductRepository) : ViewModel() {

    private var mValidadTxt = MutableLiveData<Boolean>()
    val mValidadTxtShow: LiveData<Boolean>
        get() = mValidadTxt
    //----------->

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
    //------READING---------------------->

    private var mSucessReceiptReading = MutableLiveData<Unit>()
    val mSucessReceiptReadingShow: LiveData<Unit>
        get() = mSucessReceiptReading

    private var mErrorReceiptReading = MutableLiveData<String>()
    val mErrorReceiptReadingShow: LiveData<String>
        get() = mErrorReceiptReading

    //----------->
    private var mSucessReceiptValidLogin = SingleLiveEvent<Unit>()
    val mSucessReceiptValidLoginShow: SingleLiveEvent<Unit>
        get() = mSucessReceiptValidLogin

    //----------->
    private var mSucessGetPendenceOperator = SingleLiveEvent<List<ReceiptIdOperador>>()
    val mSucessGetPendenceOperatorShow: SingleLiveEvent<List<ReceiptIdOperador>>
        get() = mSucessGetPendenceOperator

    //----------->
    private var mErrorGetPendenceOperator = MutableLiveData<String>()
    val mErrorGetPendenceOperatorShow: LiveData<String>
        get() = mErrorGetPendenceOperator


    fun getReceipt1(filtrarOperador: Boolean, mIdOperador: String) {
        viewModelScope.launch {
            try {
                val request = this@ReceiptProductViewModel1.mRepository.getReceiptProduct1(
                    filtrarOperador,
                    mIdOperador
                )
                mValidaProgressReceipt.postValue(true)
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

    fun postREadingQrCde(qrCodeReceipt1: QrCodeReceipt1) {
        viewModelScope.launch {
            try {
                mValidaProgressReceipt.postValue(true)
                val request =
                    this@ReceiptProductViewModel1.mRepository.postReceiptProduct1(qrCodeReceipt1)
                mValidaProgressReceipt.postValue(true)
                if (request.isSuccessful) {
                    request.let {
                        mSucessReceiptReading.postValue(request.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mErrorReceiptReading.postValue(messageEdit)
                }
            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mErrorReceiptReading.postValue("ConnectException\nVerifique sua internet!")
                    }
                    is SocketTimeoutException -> {
                        mErrorReceiptReading.postValue("SocketTimeoutException\nTempo de conexão excedido, tente novamente!")
                    }
                    is TimeoutException -> {
                        mErrorReceiptReading.postValue("TimeoutException\nTempo de conexão excedido, tente novamente!")
                    }
                    else -> {
                        mErrorReceiptReading.postValue(e.toString())
                    }
                }
            } finally {
                mValidaProgressReceipt.postValue(false)
            }
        }
    }

    fun postValidLoginAcesss(posLoginValidadREceipPorduct: PosLoginValidadREceipPorduct) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = this@ReceiptProductViewModel1.mRepository.validadAcessReceiptProduct(
                    posLoginValidadREceipPorduct
                )
                mValidaProgressReceipt.postValue(true)
                if (request.isSuccessful) {
                    request.let { sucessLogin ->
                        mSucessReceiptValidLogin.postValue(sucessLogin.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO").replace("PERMISSAO", "PERMISSÃO")
                    mErrorReceiptReading.postValue(messageEdit)
                }

            } catch (error1: Exception) {
                when (error1) {
                    is ConnectException -> {
                        mErrorReceiptReading.postValue("ConnectException\nVerifique sua internet!")
                    }
                    is SocketTimeoutException -> {
                        mErrorReceiptReading.postValue("SocketTimeoutException\nTempo de conexão excedido, tente novamente!")
                    }
                    is TimeoutException -> {
                        mErrorReceiptReading.postValue("TimeoutException\nTempo de conexão excedido, tente novamente!")
                    }
                    else -> {
                        mErrorReceiptReading.postValue(error1.toString())
                    }
                }
            } finally {
                mValidaProgressReceipt.postValue(false)
            }
        }
    }

    fun callPendenciesOperator() {
        viewModelScope.launch {
            try {
                val request =
                    this@ReceiptProductViewModel1.mRepository.getPendenciesOperatorReceiptProduct()
                if (request.isSuccessful) {
                    request.let { response ->
                        mSucessGetPendenceOperator.postValue(response.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO").replace("PERMISSAO", "PERMISSÃO")
                    mErrorGetPendenceOperator.postValue(messageEdit)
                }
            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mErrorGetPendenceOperator.postValue("ConnectException\nVerifique sua internet!")
                    }
                    is SocketTimeoutException -> {
                        mErrorGetPendenceOperator.postValue("SocketTimeoutException\nTempo de conexão excedido, tente novamente!")
                    }
                    is TimeoutException -> {
                        mErrorGetPendenceOperator.postValue("TimeoutException\nTempo de conexão excedido, tente novamente!")
                    }
                    else -> {
                        mErrorGetPendenceOperator.postValue(e.toString())
                    }
                }
            }
        }
    }

    /** --------------------------------RecebimentoDeProduçãoViewModelFactory------------------------------------ */
    class ReceiptProductViewModel1Factory constructor(private val repository: ReceiptProductRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ReceiptProductViewModel1::class.java)) {
                ReceiptProductViewModel1(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}