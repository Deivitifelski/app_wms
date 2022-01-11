package com.documentos.wms_beirario.ui.inventory.viewModel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.inventario.ResponseInventoryPending1
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import kotlinx.coroutines.launch
import org.json.JSONObject

class PendingTaskInventoryViewModel1(private val repository1: InventoryoRepository1) : ViewModel() {


    private var mValidadTxt = MutableLiveData<Boolean>()
    val mValidadTxtShow: LiveData<Boolean>
        get() = mValidadTxt
    //----------->

    private var mSucess = MutableLiveData<List<ResponseInventoryPending1>>()
    val mSucessShow: LiveData<List<ResponseInventoryPending1>>
        get() = mSucess

    //----------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //----------->
    private var mValidaProgress = MutableLiveData<Boolean>()
    val mValidaProgressShow: LiveData<Boolean>
        get() = mValidaProgress

    fun getPending1() {
        viewModelScope.launch {
            try {
                val request = this@PendingTaskInventoryViewModel1.repository1.pendingTaskInventory1()
                if (request.isSuccessful) {
                    if (request.body().isNullOrEmpty()) {
                        mValidadTxt.value = true
                        mValidaProgress.value = false
                    } else {
                        mValidadTxt.value = false
                        mSucess.postValue(request.body())
                        mValidaProgress.value = false
                    }
                } else {
                    mValidaProgress.value = false
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }
            } catch (e: Exception) {
                mValidaProgress.value = false
                mError.postValue("Ops! Erro inesperado...")
            }
        }
    }
}