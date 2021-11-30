package com.documentos.wms_beirario.ui.inventario.viewModel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.inventario.InventoryResponseCorrugados
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import kotlinx.coroutines.launch
import org.json.JSONObject

class CreateVoidInventoryViewModel(private val mRepository1: InventoryoRepository1) : ViewModel() {

    private var mSucess = MutableLiveData<InventoryResponseCorrugados>()
    val mSucessCorrugados: LiveData<InventoryResponseCorrugados>
        get() = mSucess

    //----------->
    private var mError = MutableLiveData<String>()
    val mErrorCorrugados: LiveData<String>
        get() = mError

    //----------->
    private var mValidaProgress = MutableLiveData<Boolean>()
    val mValidaProgressShow: LiveData<Boolean>
        get() = mValidaProgress

    private var mSucessCreateList = MutableLiveData<List<Int>>()
    val mSucessCreateListShow: MutableLiveData<List<Int>>
        get() = mSucessCreateList

    //----------->
    private var mSucessCreateListAlert = MutableLiveData<List<Int>>()
    val mSucessCreateListALertShow: MutableLiveData<List<Int>>
        get() = mSucessCreateListAlert


    //Criando listas para os alertsDialogs da lista sapatos -->
    fun getListTamShoes(first: Int, last: Int) {
        val list = mutableListOf<Int>()
        for (i in first..last) {
            list.add(i)
        }
        mSucessCreateList.postValue(list)
    }

    fun getListTamAlertShoes(first: Int, last: Int) {
        val list2 = mutableListOf<Int>()
        for (i in first..last) {
            list2.add(i)
        }
        mSucessCreateListAlert.postValue(list2)
    }
//------------------------------------------------>

    fun getCorrugados() {
        mValidaProgress.value = true
        viewModelScope.launch {
            val requestCorrugados = this@CreateVoidInventoryViewModel.mRepository1.getCorrugados()
            try {
                if (requestCorrugados.isSuccessful) {
                    mSucess.postValue(requestCorrugados.body())
                } else {
                    mValidaProgress.value = false
                    val error = requestCorrugados.errorBody()!!.string()
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


    /**FACTORY--->*/
    class InventoryVCreateVoidiewModelFactoryBarCode constructor(private val repository: InventoryoRepository1) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(CreateVoidInventoryViewModel::class.java)) {
                CreateVoidInventoryViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}