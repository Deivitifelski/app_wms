package com.documentos.wms_beirario.ui.inventario.viewModel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.inventario.RequestInventoryReadingProcess
import com.documentos.wms_beirario.model.inventario.ResponseQrCode2
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import kotlinx.coroutines.launch
import org.json.JSONObject

class InventoryReadingViewModel2(private val repository1: InventoryoRepository1) : ViewModel() {


    private var mValidadTxt = MutableLiveData<Boolean>()
    val mValidadTxtShow: LiveData<Boolean>
        get() = mValidadTxt
    //----------->

    private var mSucess = MutableLiveData<ResponseQrCode2>()
    val mSucessShow: LiveData<ResponseQrCode2>
        get() = mSucess

    //----------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //----------->
    private var mValidaProgress = MutableLiveData<Boolean>()
    val mValidaProgressShow: LiveData<Boolean>
        get() = mValidaProgress

    fun readingQrCode(inventoryReadingProcess: RequestInventoryReadingProcess) {
        mValidaProgress.value = true
        viewModelScope.launch {
            val request =
                this@InventoryReadingViewModel2.repository1.inventoryQrCode2(inventoryReadingProcess = inventoryReadingProcess)
            try {
                if (request.isSuccessful) {
                    mValidaProgress.value = false
                    mSucess.postValue(request.body())
                } else {
                    mValidaProgress.value = false
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val message = error2.replace("nao", "não").replace("CODIGO", "CÓDIGO")
                        .replace("INVALIDO", "INVÁLIDO")
                    mError.postValue(message)
                }

            } catch (e: Exception) {
                mValidaProgress.value = false
                mError.postValue(e.toString())
            }
        }
    }


    class InventoryReadingViewModelFactory constructor(private val repository: InventoryoRepository1) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(InventoryReadingViewModel2::class.java)) {
                InventoryReadingViewModel2(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}