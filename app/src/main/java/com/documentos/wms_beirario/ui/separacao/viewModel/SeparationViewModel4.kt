package com.documentos.wms_beirario.ui.separacao.viewModel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.separation.SeparacaoProdAndress4
import com.documentos.wms_beirario.model.separation.bodySeparation3
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class SeparationViewModel4(private val mRepository: SeparacaoRepository) : ViewModel() {

    private var mSucessGet = MutableLiveData<SeparacaoProdAndress4>()
    val mSucessGetShow: LiveData<SeparacaoProdAndress4>
        get() = mSucessGet

    private var mSucessPost = MutableLiveData<Unit>()
    val mSucessPostShow: LiveData<Unit>
        get() = mSucessPost

    //------------------------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //-------------------------->
    private var mValidationProgress = MutableLiveData<Boolean>()
    val mValidationProgressShow: LiveData<Boolean>
        get() = mValidationProgress

    //-------------------------->
    private var mErrorSEparation3All = SingleLiveEvent<String>()
    val mErrorSeparationSShowAll: SingleLiveEvent<String>
        get() = mErrorSEparation3All


    fun getProdAndress(idEnderecoOrigem: String) {
        viewModelScope.launch {
            try {
                mValidationProgress.postValue(true)
                val request =
                    mRepository.getProdAndress(
                        idEnderecoOrigem = idEnderecoOrigem
                    )
                if (request.isSuccessful) {
                    request.let { response ->
                        mSucessGet.postValue(response.body())
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
                        mErrorSEparation3All.postValue("Verifique sua internet!")
                    }
                    is SocketTimeoutException -> {
                        mErrorSEparation3All.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    is TimeoutException -> {
                        mErrorSEparation3All.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    else -> {
                        mErrorSEparation3All.postValue(e.toString())
                    }
                }
            } finally {
                mValidationProgress.postValue(false)
            }

        }
    }


    fun postAndress(bodySeparation3: bodySeparation3) {
        viewModelScope.launch {
            try {
                mValidationProgress.postValue(true)
                val request = mRepository.postSepProdAndress(bodySeparation3 = bodySeparation3)
                if (request.isSuccessful) {
                    request.let { response ->
                        mSucessPost.postValue(response.body())
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
                        mErrorSEparation3All.postValue("Verifique sua internet!")
                    }
                    is SocketTimeoutException -> {
                        mErrorSEparation3All.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    is TimeoutException -> {
                        mErrorSEparation3All.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    else -> {
                        mErrorSEparation3All.postValue(e.toString())
                    }
                }
            } finally {
                mValidationProgress.postValue(false)
            }
        }
    }

    class ViewModelSeparationFactory3 constructor(private val repository: SeparacaoRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(SeparationViewModel4::class.java)) {
                SeparationViewModel4(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}