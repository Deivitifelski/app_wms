package com.documentos.wms_beirario.ui.reimpressao.porNumSerie

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.reimpressao.RequestEtiquetasReimpressaoBody
import com.documentos.wms_beirario.model.reimpressao.ResponseEtiquetasReimpressao
import com.documentos.wms_beirario.model.reimpressao.ResultReimpressaoDefault
import com.documentos.wms_beirario.model.reimpressao.ResultReimpressaoDefaultItem
import com.documentos.wms_beirario.repository.reimpressao.ReimpressaoRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class ReimpressaoNumSerieViewModel(val repository: ReimpressaoRepository) : ViewModel() {

    private var sucessoReimpressaoNumSerie = MutableLiveData<ResultReimpressaoDefault?>()
    val mSucessShow get() = sucessoReimpressaoNumSerie

    private var mSucessZpls = MutableLiveData<ResponseEtiquetasReimpressao>()
    val mSucessZplsShows get() = mSucessZpls

    private var mErrorHttp = MutableLiveData<String>()
    val mErrorHttpShow get() = mErrorHttp

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow get() = mErrorAll


    fun getNumSerie(numSerie: String, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                val response =
                    this@ReimpressaoNumSerieViewModel.repository.getReimpressaoNumSerie(
                        numserie = numSerie,
                        idArmazem = idArmazem,
                        token = token
                    )
                if (response.isSuccessful) {
                    response.body().let { data ->
                        sucessoReimpressaoNumSerie.postValue(data)
                    }
                } else {
                    mErrorHttp.postValue(validaErrorDb(response))
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

    fun getZpls(itemClick: ResultReimpressaoDefaultItem, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                val response =
                    this@ReimpressaoNumSerieViewModel.repository.getReimpressaoEtiquetas(
                        createBody(itemClick),
                        idArmazem,
                        token
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
        Log.e(
            "POST REIMPRESSÕES -->",
            "ENVIADO CLIQUE DA IMPRESSÃO -->\nidTarefa -->${itemClick.idTarefa}\nsequencialTarefa -->${itemClick.sequencialTarefa}\nidOrdemMontagemVolume -->${itemClick.idOrdemMontagemVolume}\nidInventarioAbastecimentoItem->${itemClick.idInventarioAbastecimentoItem} ",
        )
    }

    /** --------------------------------REIMPRESSAO ViewModelFactory------------------------------------ */
    class ReimpressaoNumSerieViewModelFactory constructor(private val repository: ReimpressaoRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ReimpressaoNumSerieViewModel::class.java)) {
                ReimpressaoNumSerieViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}