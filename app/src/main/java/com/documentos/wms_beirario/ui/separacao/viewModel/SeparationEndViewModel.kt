package com.documentos.wms_beirario.ui.separacao.viewModel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.separation.ResponseListCheckBoxItem
import com.documentos.wms_beirario.model.separation.SeparationEnd
import com.documentos.wms_beirario.model.separation.SeparationListCheckBox
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class SeparationEndViewModel(private val mRepository: SeparacaoRepository) : ViewModel() {

    private var mSucess02 = MutableLiveData<List<ResponseListCheckBoxItem>>()
    val mShowShow2: LiveData<List<ResponseListCheckBoxItem>>
        get() = mSucess02

    //------------------------->
    private var mError2 = MutableLiveData<String>()
    val mErrorShow2: LiveData<String>
        get() = mError2

    //-------------------------->
    private var mValidationProgress = MutableLiveData<Boolean>()
    val mValidationProgressShow: LiveData<Boolean>
        get() = mValidationProgress

    //-------------------------->
    private var mSeparationEnd = SingleLiveEvent<String>()
    val mSeparationEndShow: SingleLiveEvent<String>
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
    fun postListCheck(listCheck: SeparationListCheckBox) {
        viewModelScope.launch {
            try {
                mProgressInit.postValue(true)
                val request = this@SeparationEndViewModel.mRepository.postListCheckBox(listCheck)
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
    fun postSeparationEnd(separationEnd: SeparationEnd) {
        viewModelScope.launch {
            val requestEnd =
                this@SeparationEndViewModel.mRepository.postSeparationEnd(separationEnd = separationEnd)
            try {
                mProgress.postValue(true)
                if (requestEnd.isSuccessful) {
                    mSeparationEnd.postValue("")
                } else {
                    val error = requestEnd.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mErrorSeparationEnd.postValue(messageEdit)
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

    class ViewModelEndSeparationFactory constructor(private val repository: SeparacaoRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(SeparationEndViewModel::class.java)) {
                SeparationEndViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }

}