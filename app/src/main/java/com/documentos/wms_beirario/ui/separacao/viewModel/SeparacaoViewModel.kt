package com.documentos.wms_beirario.ui.separacao.viewModel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.separation.ResponseGetAndaresSeparation
import com.documentos.wms_beirario.model.separation.ResponseItemsSeparationItem
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class SeparacaoViewModel(private val mRepository: SeparacaoRepository) : ViewModel() {

    //-------------------------->
    private var mSucess = MutableLiveData<List<ResponseItemsSeparationItem>>()
    val mShowShow: LiveData<List<ResponseItemsSeparationItem>>
        get() = mSucess

    //-------------------------->
    private var mSucessAndares = MutableLiveData<ResponseGetAndaresSeparation>()
    val mShowAndaresShow: LiveData<ResponseGetAndaresSeparation>
        get() = mSucessAndares

    //-------------------------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //-------------------------->
    private var mValidaTxt = MutableLiveData<Boolean>()
    val mValidaTxtShow: LiveData<Boolean>
        get() = mValidaTxt

    //--------------------------->
    private var mValidaProgress = MutableLiveData<Boolean>()
    val mValidaProgressShow: LiveData<Boolean>
        get() = mValidaProgress


    /**---------------------CHAMADA 01 BUSCA DAS ESTANTES ----------------------------------------*/
    fun getItemsEstantesSeparation() {
        viewModelScope.launch {
            try {
                val request = this@SeparacaoViewModel.mRepository.getItemsSeparation()
                mValidaProgress.value = false
                if (request.isSuccessful) {
                    mValidaTxt.value = false
                    if (request.body().isNullOrEmpty()) {
                        mSucess.postValue(request.body())
                    } else {
                        mValidaTxt.value = true
                        mSucess.postValue(request.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }

            } catch (e: Exception) {
                mValidaTxt.value = false
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
                mValidaProgress.postValue(false)
            }
        }
    }

    /**---------------------CHAMADA 01 BUSCA DAS ANDARES ----------------------------------------*/
    fun getItemsAndaresSeparation() {
        viewModelScope.launch {
            try {
                val request = this@SeparacaoViewModel.mRepository.getItemAndares()
                mValidaProgress.value = false
                if (request.isSuccessful) {
                    mSucessAndares.postValue(request.body())
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
                mValidaProgress.postValue(false)
            }
        }
    }


    class SeparacaoItensViewModelFactory constructor(private val repository: SeparacaoRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(SeparacaoViewModel::class.java)) {
                SeparacaoViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}

