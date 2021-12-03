package com.documentos.wms_beirario.ui.inventario.viewModel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.inventario.Distribuicao
import com.documentos.wms_beirario.model.inventario.InventoryResponseCorrugados
import com.documentos.wms_beirario.model.inventario.ResponseQrCode2
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.SingleLiveEvent
import kotlinx.coroutines.launch
import org.json.JSONObject

class CreateVoidInventoryViewModel(private val mRepository1: InventoryoRepository1) : ViewModel() {

    /**LIVEDATAS CORRUGADOS --> */
    private var mSucess = SingleLiveEvent<InventoryResponseCorrugados>()
    val mSucessCorrugados: SingleLiveEvent<InventoryResponseCorrugados>
        get() = mSucess


    private var mError = MutableLiveData<String>()
    val mErrorCorrugados: LiveData<String>
        get() = mError

    //--------------------------------------------------------------------------------------->
    private var mValidaProgress = MutableLiveData<Boolean>()
    val mValidaProgressShow: LiveData<Boolean>
        get() = mValidaProgress

    private var mSucessCreateList = SingleLiveEvent<List<Int>>()
    val mSucessCreateListShow: SingleLiveEvent<List<Int>>
        get() = mSucessCreateList

    //----------->
    private var mSucessCreateListAlert = SingleLiveEvent<List<Int>>()
    val mSucessCreateListALertShow: SingleLiveEvent<List<Int>>
        get() = mSucessCreateListAlert

    //----------->
    private var mSucessGetResponse = MutableLiveData<ResponseQrCode2>()
    val mSucessGetResponseShow: LiveData<ResponseQrCode2>
        get() = mSucessGetResponse

    //---------------->
    private var mGetList = MutableLiveData<List<Distribuicao>>()
    val mGetListShow: LiveData<List<Distribuicao>>
        get() = mGetList

    //---------------->
    private var mResponseButtonAdd = MutableLiveData<Boolean>()
    val mResponseButtonAddShow: LiveData<Boolean>
        get() = mResponseButtonAdd


    //Criando listas para os alertsDialogs da lista sapatos -->
    fun getListTamShoes(first: Int, last: Int) {
        val list = mutableListOf<Int>()
        for (i in first..last) {
            list.add(i)
        }
        mSucessCreateList.postValue(list)
    }

    fun getListQntShoes(first: Int, last: Int) {
        val list2 = mutableListOf<Int>()
        for (i in first..last) {
            list2.add(i)
        }
        mSucessCreateListAlert.postValue(list2)
    }
//------------------------------------------------>


    fun getResponseQRcODE(responseQrcode: ResponseQrCode2) {
        mSucessGetResponse.value = responseQrcode
    }

    fun getCorrugados() {
        mValidaProgress.value = true
        viewModelScope.launch {
            val requestCorrugados = this@CreateVoidInventoryViewModel.mRepository1.getCorrugados()
            try {
                if (requestCorrugados.isSuccessful) {
                    mSucess.value = requestCorrugados.body()
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

    fun postList(returList: MutableList<Distribuicao>) {
        mGetList.value = returList
    }

    fun setButtonAdd(
        linha: String,
        referencia: String,
        cabedal: String,
        cor: String,
        mQntTotalShoes: Int,
        mQntCorrugadoTotal: Int,
        list: MutableList<Distribuicao>,


        ) {
        mResponseButtonAdd.value = list.isNotEmpty() && mQntTotalShoes <= mQntCorrugadoTotal && linha != "" && referencia != "" && cabedal != "" && cor != ""

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