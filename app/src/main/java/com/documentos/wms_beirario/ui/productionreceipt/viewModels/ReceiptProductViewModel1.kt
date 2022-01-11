package com.documentos.wms_beirario.ui.productionreceipt.viewModels

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.receiptproduct.*
import com.documentos.wms_beirario.repository.receiptproduct.ReceiptProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

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
    private var mSucessReceiptValidLogin = MutableLiveData<Unit>()
    val mSucessReceiptValidLoginShow: LiveData<Unit>
        get() = mSucessReceiptValidLogin
    //----------->
    private var mSucessGetPendenceOperator = MutableLiveData<List<ReceiptIdOperador>>()
    val mSucessGetPendenceOperatorShow: LiveData<List<ReceiptIdOperador>>
        get() = mSucessGetPendenceOperator
    //----------->
    private var mErrorGetPendenceOperator = MutableLiveData<String>()
    val mErrorGetPendenceOperatorShow: LiveData<String>
        get() = mErrorGetPendenceOperator


    fun getReceipt1(filtrarOperador: Boolean, mIdOperador: String) {
        viewModelScope.launch {
            val request = this@ReceiptProductViewModel1.mRepository.getReceiptProduct1(
                filtrarOperador,
                mIdOperador
            )
            try {
                mValidaProgressReceipt.value = false
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
                mValidaProgressReceipt.value = false
                mErrorReceipt.value = "Ops...Erro inesperado!"
            }
        }
    }

    fun postREadingQrCde(qrCodeReceipt1: QrCodeReceipt1) {
        viewModelScope.launch {
            val request =
                this@ReceiptProductViewModel1.mRepository.postReceiptProduct1(qrCodeReceipt1)
            try {
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
                mErrorReceiptReading.postValue(e.toString())
            }

        }

    }

    fun postValidLoginAcesss(posLoginValidadREceipPorduct: PosLoginValidadREceipPorduct) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = this@ReceiptProductViewModel1.mRepository.validadAcessReceiptProduct(
                    posLoginValidadREceipPorduct
                )
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
                withContext(Dispatchers.Main) {
                    mErrorReceiptReading.postValue("Ops! Erro inesperado...")
                }
            }
        }
    }

    fun callPendenciesOperator() {
        viewModelScope.launch {
            try {
                val request = this@ReceiptProductViewModel1.mRepository.getPendenciesOperatorReceiptProduct()
                if (request.isSuccessful) {
                    request.let { response ->
                        mSucessGetPendenceOperator.postValue(response.body())
                    }
                }else{
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO").replace("PERMISSAO", "PERMISSÃO")
                    mErrorGetPendenceOperator.postValue(messageEdit)
                }
            } catch (e: Exception) {
                mErrorGetPendenceOperator.postValue("Ops! Erro inesperado...")
            }
        }
    }
}