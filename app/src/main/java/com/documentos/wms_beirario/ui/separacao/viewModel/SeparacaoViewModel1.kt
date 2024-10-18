package com.documentos.wms_beirario.ui.separacao.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.separation.ResponseSeparation1
import com.documentos.wms_beirario.model.separation.filtros.BodyAndaresFiltro
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class SeparacaoViewModel1(private val repository: SeparacaoRepository) : ViewModel() {

    //-------------------------->
    private var mSucess = MutableLiveData<List<ResponseSeparation1>>()
    val mShowShow: LiveData<List<ResponseSeparation1>>
        get() = mSucess

    //-------------------------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //--------------------------->
    private var progress = MutableLiveData<Boolean>()
    val mValidaProgressShow: LiveData<Boolean>
        get() = progress


    /**---------------------CHAMADA 01 BUSCA DAS ESTANTES ----------------------------------------*/
    fun getItensAndares(ideArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                val request = this@SeparacaoViewModel1.repository.getBuscaAndaresSeparation(
                    ideArmazem,
                    token
                )
                progress.value = false
                if (request.isSuccessful) {
                    mSucess.postValue(request.body())
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }

            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mError.postValue("Verifique sua internet!")
                    }

                    is SocketTimeoutException -> {
                        mError.postValue("Tempo de conexão excedido, tente novamente!")
                    }

                    is TimeoutException -> {
                        mError.postValue("Tempo de conexão excedido, tente novamente!")
                    }

                    else -> {
                        mError.postValue(e.toString())
                    }
                }
            } finally {
                progress.postValue(false)
            }
        }
    }

    fun getAndaresFiltro(
        token: String,
        idArmazem: Int,
        body: BodyAndaresFiltro
    ) {
        viewModelScope.launch {
            try {
                progress.postValue(true)
                val result = repository.getAndaresFiltro(
                    token = token,
                    idArmazem = idArmazem,
                    body = body
                )
                if (result.isSuccessful) {
                    mSucess.postValue(result.body())
                } else {
                    mError.postValue(validaErrorDb(result))
                }

            } catch (e: Exception) {
                mError.postValue(validaErrorException(e))
            } finally {
                progress.postValue(false)
            }
        }
    }

    class SeparacaoItensViewModelFactory constructor(private val repository: SeparacaoRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(SeparacaoViewModel1::class.java)) {
                SeparacaoViewModel1(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}

