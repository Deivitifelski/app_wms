package com.documentos.wms_beirario.ui.inventory.viewModel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.inventario.ResponseListRecyclerView
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import kotlinx.coroutines.launch
import org.json.JSONObject

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

    fun getItensRecyclerView(
        idEndereco: Int,
        idInventario: Int,
        numeroContagem: Int
    ) {
        mValidaProgress.value = true
        viewModelScope.launch {

            try {
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
                    mValidaProgress.value = false

                } else {
                    mValidaProgress.value = false
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val message = error2.replace("nao", "não").replace("CODIGO", "CÓDIGO")
                        .replace("INVALIDO", "INVÁLIDO")
                    mError.postValue(message)
                }

            } catch (e: Exception) {
                mError.postValue("Ops! Erro inesperado...")
            }
        }
    }
}