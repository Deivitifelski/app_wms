package com.documentos.wms_beirario.ui.inventory.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.inventario.ResponseInventoryPending1
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

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
    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow: LiveData<String>
        get() = mErrorAll

    //----------->
    private var mValidaProgress = MutableLiveData<Boolean>()
    val mValidaProgressShow: LiveData<Boolean>
        get() = mValidaProgress

    fun getPending1() {
        viewModelScope.launch {
            try {
                val request =
                    this@PendingTaskInventoryViewModel1.repository1.pendingTaskInventory1()
                if (request.isSuccessful) {
                    if (request.body().isNullOrEmpty()) {
                        mValidadTxt.value = true
                    } else {
                        mValidadTxt.value = false
                        mSucess.postValue(request.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
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
                mValidaProgress.postValue(false)
            }
        }
    }

    /** --------------------------------INVENTARIO ViewModelFactory------------------------------------ */
    class PendingTaskFragiewModelFactory constructor(private val repository: InventoryoRepository1) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(PendingTaskInventoryViewModel1::class.java)) {
                PendingTaskInventoryViewModel1(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}