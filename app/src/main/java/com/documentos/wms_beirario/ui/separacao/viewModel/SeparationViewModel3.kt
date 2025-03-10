package com.documentos.wms_beirario.ui.separacao.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.separation.ResponseTarefasANdaresSEparation3
import com.documentos.wms_beirario.model.separation.SeparationEnd
import com.documentos.wms_beirario.model.separation.filtros.BodyEnderecosFiltro
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.utils.SingleLiveEvent
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class SeparationViewModel3(private val mRepository: SeparacaoRepository) : ViewModel() {

    private var mSucess02 = MutableLiveData<ResponseTarefasANdaresSEparation3>()
    val mShowShow2: LiveData<ResponseTarefasANdaresSEparation3>
        get() = mSucess02

    //------------------------->
    private var mError2 = MutableLiveData<String>()
    val mErrorShow2: LiveData<String>
        get() = mError2


    //-------------------------->
    private var mSeparationEnd = SingleLiveEvent<Unit>()
    val mSeparationEndShow: SingleLiveEvent<Unit>
        get() = mSeparationEnd

    //-------------------------->
    private var mErrorSeparationEnd = MutableLiveData<String>()
    val mErrorSeparationEndShow: LiveData<String>
        get() = mErrorSeparationEnd

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow get() = mErrorAll

    private var mProgress = MutableLiveData<Boolean>()
    val mProgressShow get() = mProgress

    private var mProgressInit = MutableLiveData<Boolean>()
    val mProgressInitShow get() = mProgressInit

    init {
        mProgress.postValue(false)
        mProgressInit.postValue(true)
    }


    /**---------------------CHAMADA 02 LISTAS ----------------------------------------*/
    fun postArrayAndaresEstantes(
        body: BodyEnderecosFiltro,
        idArmazem: Int?,
        token: String
    ) {
        viewModelScope.launch {
            try {
                mProgressInit.postValue(true)
                val request =
                    this@SeparationViewModel3.mRepository.postBuscaEnderecosSeparation(
                        body,
                        idArmazem,
                        token
                    )
                if (request.isSuccessful) {
                    request.let { list ->
                        mSucess02.postValue(list.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError2.postValue(messageEdit)
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
                mProgressInit.postValue(false)
            }
        }
    }


    /**---------------------CHAMADA 03 SEPARAR VOLUMES ----------------------------------------*/
    fun postSeparationEnd(separationEnd: SeparationEnd, idArmazem: Int, token: String) {
        viewModelScope.launch {
            mProgress.postValue(true)
            val requestEnd =
                this@SeparationViewModel3.mRepository.postSeparationEnd(
                    separationEnd = separationEnd,
                    idArmazem,
                    token
                )
            try {
                if (requestEnd.isSuccessful) {
                    mSeparationEnd.postValue(requestEnd.body())
                } else {
                    mErrorSeparationEnd.postValue(validaErrorDb(request = requestEnd))
                }
            } catch (e: Exception) {
                mErrorAll.postValue(validaErrorException(e))
            } finally {
                mProgress.postValue(false)
            }
        }
    }

    class ViewModelEndSeparationFactory constructor(private val repository: SeparacaoRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(SeparationViewModel3::class.java)) {
                SeparationViewModel3(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }

}