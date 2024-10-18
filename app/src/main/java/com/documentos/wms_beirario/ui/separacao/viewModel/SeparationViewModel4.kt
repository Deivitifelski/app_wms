package com.documentos.wms_beirario.ui.separacao.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.separation.BodySepararEtiquetar
import com.documentos.wms_beirario.model.separation.ResponseEtiquetarSeparar
import com.documentos.wms_beirario.model.separation.SeparacaoProdAndress4
import com.documentos.wms_beirario.model.separation.filtros.BodyProdutoSeparacao
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.utils.SingleLiveEvent
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class SeparationViewModel4(private val mRepository: SeparacaoRepository) : ViewModel() {

    private var mSucessoGetProdutos = MutableLiveData<SeparacaoProdAndress4>()
    val mSucessoGetProdutosShow: LiveData<SeparacaoProdAndress4>
        get() = mSucessoGetProdutos

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

    //etiquetar e separar -->
    private var mSucessPostSepEti = MutableLiveData<ResponseEtiquetarSeparar>()
    val mSucessPostSepEtiShow: LiveData<ResponseEtiquetarSeparar>
        get() = mSucessPostSepEti

    private var mErrorSepEti = MutableLiveData<String>()
    val mErrorSepEtiShow: LiveData<String>
        get() = mErrorSepEti


    fun postBuscaProdutos(
        body: BodyProdutoSeparacao,
        idArmazem: Int,
        token: String
    ) {
        viewModelScope.launch {
            try {
                mValidationProgress.postValue(true)
                val request = mRepository.getProdAndress(
                    body = body,
                    idArmazem = idArmazem,
                    token = token
                )
                if (request.isSuccessful) {
                    request.let { response ->
                        mSucessoGetProdutos.postValue(response.body())
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

    fun postAndressEtiquetarSeparar(
        body: BodySepararEtiquetar,
        idEnderecoOrigem: Int,
        idArmazem: Int,
        token: String
    ) {
        viewModelScope.launch {
            try {
                mValidationProgress.postValue(true)
                val request = mRepository.postSepararEtiquetar(
                    bodySeparationEtiquetar = body,
                    idEnderecoOrigem = idEnderecoOrigem,
                    idArmazem,
                    token
                )
                if (request.isSuccessful) {
                    request.let { response ->
                        mSucessPostSepEti.postValue(response.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mErrorSepEti.postValue(messageEdit)
                }

            } catch (e: Exception) {
                mErrorSEparation3All.postValue(validaErrorException(e))
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