package com.documentos.wms_beirario.ui.reimpressao.porNumSerie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.reimpressao.ResponseEtiquetasReimpressao
import com.documentos.wms_beirario.model.reimpressao.ResultReimpressaoDefault
import com.documentos.wms_beirario.repository.reimpressao.ReimpressaoRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class ReimpressaoNumSerieViewModel(val repository: ReimpressaoRepository) : ViewModel() {

    private var mSucess = MutableLiveData<ResultReimpressaoDefault>()
    val mSucessShow get() = mSucess

    private var mSucessZpls = MutableLiveData<ResponseEtiquetasReimpressao>()
    val mSucessZplsShows get() = mSucessZpls

    private var mErrorHttp = MutableLiveData<String>()
    val mErrorHttpShow get() = mErrorHttp

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow get() = mErrorAll


    fun getNumSerie(numSerie: String) {
        viewModelScope.launch {
            try {
                val response =
                    this@ReimpressaoNumSerieViewModel.repository.getReimpressaoNumSerie(
                        numserie = numSerie
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
            }
        }
    }

    fun getZpls(idTarefa: String, sequencialTarefa: String) {
        viewModelScope.launch {
            try {
                val response =
                    this@ReimpressaoNumSerieViewModel.repository.getReimpressaoEtiquetas(
                        idTarefa = idTarefa, sequencialTarefa = sequencialTarefa
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