package com.documentos.wms_beirario.ui.etiquetagem.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.etiquetagem.ResponsePendencyOndaEtiquetagem
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class LabelingPendingOndaViewModel(private val mRepository: EtiquetagemRepository):ViewModel() {

    private var mSucess = MutableLiveData<ResponsePendencyOndaEtiquetagem>()
    val mSucessShow: LiveData<ResponsePendencyOndaEtiquetagem>
        get() = mSucess

    //--------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError


    //----------->
    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow: LiveData<String>
        get() = mErrorAll

    //----------->
    private var mProgress = MutableLiveData<Boolean>()
    val mProgressShow: LiveData<Boolean>
        get() = mProgress

    init {
        mProgress.postValue(true)
    }

    fun getLabeling() {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val request = this@LabelingPendingOndaViewModel.mRepository.labelinggetOnda()
                if (request.isSuccessful) {
                    mSucess.postValue(request.body())
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val message = error2.replace("nao", "não")
                        .replace("INVALIDO", "INVÁLIDO")
                    mError.postValue(message)
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

    /** --------------------------------Labeling 2 ViewModelFactory------------------------------------ */
    class LabelingViewModelOndaFactory constructor(private val repository: EtiquetagemRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(LabelingPendingOndaViewModel::class.java)) {
                LabelingPendingOndaViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}