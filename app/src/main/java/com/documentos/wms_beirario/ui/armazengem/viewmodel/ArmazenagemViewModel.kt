package com.documentos.wms_beirario.ui.armazengem.viewmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.armazenagem.ArmazemRequestFinish
import com.documentos.wms_beirario.model.armazenagem.ArmazenagemResponse
import com.documentos.wms_beirario.repository.armazenagem.ArmazenagemRepository
import com.documentos.wms_beirario.ui.armazengem.DataMock
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class ArmazenagemViewModel constructor(private var repository: ArmazenagemRepository) :
    ViewModel() {


    var messageError = MutableLiveData<String>()
    var mSucess = MutableLiveData<List<ArmazenagemResponse>>()
    var mValidProgress = MutableLiveData<Boolean>()

    private var mSucessFinish = MutableLiveData<Unit>()
    val mSucessFinishshow: LiveData<Unit>
        get() = mSucessFinish

    /**
     * MOCK---------->
     */
    private var mSucessFinishMock = MutableLiveData<String>()
    val mSucessFinishMockShow: LiveData<String>
        get() = mSucessFinishMock


    fun getArmazenagem() {
        viewModelScope.launch {
            try {
                val request = repository.getArmazens()
                mValidProgress.value = false
                if (request.isSuccessful) {
                    request.let { list ->
                        mSucess.postValue(list.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    messageError.postValue(error2)
                }

            } catch (e: Exception) {
                mValidProgress.value = false
                messageError.postValue("Ops! Erro inesperado...")
            }
        }
    }

    fun postFinish(armazemRequestFinish: ArmazemRequestFinish) {
        viewModelScope.launch {
            try {
                val request =
                    this@ArmazenagemViewModel.repository.finishArmazenagem(armazemRequestFinish = armazemRequestFinish)
                if (request.isSuccessful) {
                    request.let { response ->
                        mSucessFinish.postValue(response.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    messageError.postValue(error2)
                }
            } catch (e: Exception) {
                messageError.postValue("Ops!Erro inesperado...")
            } catch (http: HttpException) {
                messageError.postValue("Verifique sua internet!")
            }
        }
    }

    fun postFinishMock(qrcode: String) {
//        DataMock.returnArmazens().map {
//            if (it.visualEnderecoDestino == qrcode){
//                mSucessFinishMock.postValue("Ok")
//            }
//        }
        DataMock.returnArmazens().map {
            if (it.visualEnderecoDestino != qrcode){
                messageError.postValue("ERRO NAO CONTEM ITEM")
            }
        }

    }

    class ArmazenagemViewModelFactory constructor(private val repository: ArmazenagemRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ArmazenagemViewModel::class.java)) {
                ArmazenagemViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}


