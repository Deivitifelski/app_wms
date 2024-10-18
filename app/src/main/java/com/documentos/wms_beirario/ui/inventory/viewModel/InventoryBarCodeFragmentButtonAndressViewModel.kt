package com.documentos.wms_beirario.ui.inventory.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.inventario.ResponseListRecyclerView
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class InventoryBarCodeFragmentButtonAndressViewModel(private val mRepository1: InventoryoRepository1) :
    ViewModel() {


    private var mSucess = MutableLiveData<ResponseListRecyclerView>()
    val mSucessShowbuttonAndress: LiveData<ResponseListRecyclerView>
        get() = mSucess

    //----------->
    private var mError = MutableLiveData<String>()
    val mErrorShowButtonAndress: LiveData<String>
        get() = mError

    //----------->
    private var mValidaProgress = MutableLiveData<Boolean>()
    val mValidaProgressShow: LiveData<Boolean>
        get() = mValidaProgress

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow get() = mErrorAll

    fun getItensRecyclerView(
        idEndereco: Int,
        idInventario: Int,
        numeroContagem: Int
    ) {
        viewModelScope.launch {

            try {
                mValidaProgress.postValue(true)
                val request =
                    this@InventoryBarCodeFragmentButtonAndressViewModel.mRepository1.inventoryResponseRecyclerView(
                        idEndereco,
                        idInventario,
                        numeroContagem
                    )
                if (request.isSuccessful) {
                    request.body().let {
                        mSucess.postValue(request.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val message = error2.replace("nao", "não").replace("CODIGO", "CÓDIGO")
                        .replace("INVALIDO", "INVÁLIDO")
                    mError.postValue(message)
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
    class BarCodeFragiewModelFactory constructor(private val repository: InventoryoRepository1) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(InventoryBarCodeFragmentButtonAndressViewModel::class.java)) {
                InventoryBarCodeFragmentButtonAndressViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}