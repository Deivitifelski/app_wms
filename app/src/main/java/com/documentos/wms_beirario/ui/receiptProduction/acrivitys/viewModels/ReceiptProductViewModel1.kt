package com.documentos.wms_beirario.ui.receiptProduction.acrivitys.viewModels

import android.util.Log
import androidx.lifecycle.*
import com.documentos.wms_beirario.model.receiptproduct.*
import com.documentos.wms_beirario.repository.receiptproduct.ReceiptProductRepository
import com.documentos.wms_beirario.utils.SingleLiveEvent
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ReceiptProductViewModel1(private val mRepository: ReceiptProductRepository) : ViewModel() {

    private var mValidadTxt = SingleLiveEvent<Boolean>()
    val mValidadTxtShow: SingleLiveEvent<Boolean>
        get() = mValidadTxt
    //----------->

    private var mSucessReceipt = SingleLiveEvent<List<ReceiptProduct1>>()
    val mSucessReceiptShow: SingleLiveEvent<List<ReceiptProduct1>>
        get() = mSucessReceipt

    //----------->
    private var mErrorReceipt = SingleLiveEvent<String>()
    val mErrorReceiptShow: SingleLiveEvent<String>
        get() = mErrorReceipt

    //----------->
    private var mValidaProgressReceipt = SingleLiveEvent<Boolean>()
    val mValidaProgressReceiptShow: SingleLiveEvent<Boolean>
        get() = mValidaProgressReceipt

    //------READING---------------------->
    private var mSucessReceiptReading = MutableLiveData<Unit>()
    val mSucessReceiptReadingShow: LiveData<Unit>
        get() = mSucessReceiptReading

    private var mErrorReceiptReading = SingleLiveEvent<String>()
    val mErrorReceiptReadingShow: SingleLiveEvent<String>
        get() = mErrorReceiptReading

    //----------->
    private var mSucessReceiptValidLogin = SingleLiveEvent<Unit>()
    val mSucessReceiptValidLoginShow: SingleLiveEvent<Unit>
        get() = mSucessReceiptValidLogin

    //----------->
    private var mSucessGetPendenceOperator = SingleLiveEvent<List<ReceiptIdOperadorSeriazable>>()
    val mSucessGetPendenceOperatorShow: SingleLiveEvent<List<ReceiptIdOperadorSeriazable>>
        get() = mSucessGetPendenceOperator

    //----------->
    private var mErrorGetPendenceOperator = SingleLiveEvent<String>()
    val mErrorGetPendenceOperatorShow: SingleLiveEvent<String>
        get() = mErrorGetPendenceOperator

    //----------->
    //----------->
    private var mSucessFinishAllOrder = SingleLiveEvent<Unit>()
    val mSucessFinishAllOrderShow: SingleLiveEvent<Unit>
        get() = mSucessFinishAllOrder

    private var mErrorFinishAll = SingleLiveEvent<String>()
    val mErrorFinishAllSHow: SingleLiveEvent<String>
        get() = mErrorFinishAll


    fun getReceipt1(filtrarOperador: Boolean, mIdOperador: String) {
        viewModelScope.launch {
            try {
                mValidaProgressReceipt.postValue(true)
                val request = this@ReceiptProductViewModel1.mRepository.getReceiptProduct1(
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
                mErrorReceipt.postValue(validaErrorException(e))
            } finally {
                mValidaProgressReceipt.postValue(false)
            }
        }
    }

    fun postReadingQrCde(qrCodeReceipt1: QrCodeReceipt1) {
        viewModelScope.launch {
            try {
                mValidaProgressReceipt.postValue(true)
                val request =
                    this@ReceiptProductViewModel1.mRepository.postReceiptProduct1(qrCodeReceipt1)
                if (request.isSuccessful) {
                    request.let { unit ->
                        mSucessReceiptReading.postValue(unit.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mErrorReceiptReading.postValue(messageEdit)
                }
            } catch (e: Exception) {
                mErrorReceiptReading.postValue(validaErrorException(e))
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
                mErrorReceiptReading.postValue(validaErrorException(error1))
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
                mErrorGetPendenceOperator.postValue(validaErrorException(e))
            }
        }
    }

    fun finalizeAllOrders(orderQrCode: PostCodScanFinish) {
        viewModelScope.launch {
            try {
                val request =
                    this@ReceiptProductViewModel1.mRepository.postFinishOrders(orderQrCode)
                if (request.isSuccessful) {
                    request.let { response ->
                        mSucessFinishAllOrder.postValue(response.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO").replace("PERMISSAO", "PERMISSÃO")
                    mErrorFinishAll.postValue(messageEdit)
                }
            } catch (e: Exception) {
                mErrorFinishAll.postValue(validaErrorException(e))
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