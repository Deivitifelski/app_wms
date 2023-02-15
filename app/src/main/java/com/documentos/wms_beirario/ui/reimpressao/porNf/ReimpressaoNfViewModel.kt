package com.documentos.wms_beirario.ui.reimpressao.porNf

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.reimpressao.RequestEtiquetasReimpressaoBody
import com.documentos.wms_beirario.model.reimpressao.ResponseEtiquetasReimpressao
import com.documentos.wms_beirario.model.reimpressao.ResultReimpressaoDefault
import com.documentos.wms_beirario.model.reimpressao.ResultReimpressaoDefaultItem
import com.documentos.wms_beirario.repository.reimpressao.ReimpressaoRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class ReimpressaoNfViewModel(val mRepository: ReimpressaoRepository) : ViewModel() {
    private var mSucess = MutableLiveData<ResultReimpressaoDefault>()
    val mSucessShow get() = mSucess

    private var mSucessZpls = MutableLiveData<ResponseEtiquetasReimpressao>()
    val mSucessZplsShows get() = mSucessZpls

    private var mErrorHttp = MutableLiveData<String>()
    val mErrorHttpShow get() = mErrorHttp

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow get() = mErrorAll

    private var mProgress = MutableLiveData<Boolean>()
    val mProgressShow get() = mProgress

    init {
        mProgress.value = false
    }

    fun getNumNf(nfNumero: String, nfSerie: String) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val response =
                    this@ReimpressaoNfViewModel.mRepository.getReimpressaoNf(
                        nfNumero = nfNumero,
                        nfSerie = nfSerie
                    )
                if (response.isSuccessful) {
                    response.body().let { response ->
                        mSucess.postValue(response)
                    }
                } else {
                    val error = response.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    mErrorHttp.postValue("Não foram encontradas tarefas.")
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
                mProgress.postValue(false)
            }
        }
    }

    fun getZpls(itemClick: ResultReimpressaoDefaultItem) {
        viewModelScope.launch {
            try {
                val response =
                    this@ReimpressaoNfViewModel.mRepository.getReimpressaoEtiquetas(
                        createBody(itemClick)
                    )
                if (response.isSuccessful) {
                    response.body().let { response ->
                        mSucessZpls.postValue(response)
                    }
                } else {
                    val error = response.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    mErrorHttp.value = error2
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
            }
        }
    }

    private fun createBody(itemClick: ResultReimpressaoDefaultItem): RequestEtiquetasReimpressaoBody {
        return RequestEtiquetasReimpressaoBody(
            idTarefa = itemClick.idTarefa,
            sequencialTarefa = itemClick.sequencialTarefa,
            idOrdemMontagemVolume = itemClick.idOrdemMontagemVolume,
            idInventarioAbastecimentoItem = itemClick.idInventarioAbastecimentoItem
        )
    }


    /** --------------------------------REIMPRESSAO ViewModelFactory------------------------------------ */
    class ReimpressaoNfViewModelFactory constructor(private val repository: ReimpressaoRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ReimpressaoNfViewModel::class.java)) {
                ReimpressaoNfViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }

}