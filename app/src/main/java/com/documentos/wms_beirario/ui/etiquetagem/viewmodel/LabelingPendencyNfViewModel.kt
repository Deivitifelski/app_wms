package com.documentos.wms_beirario.ui.etiquetagem.viewmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.etiquetagem.ResponsePendencePedidoEtiquetagem
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class LabelingPendencyNfViewModel(private val mRepository: EtiquetagemRepository) : ViewModel() {
    private var mSucess = MutableLiveData<ResponsePendencePedidoEtiquetagem>()
    val mSucessShow: LiveData<ResponsePendencePedidoEtiquetagem>
        get() = mSucess

    //--------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //------------>
    private var mValidProgress = MutableLiveData<Boolean>()
    val mValidProgressShow: LiveData<Boolean>
        get() = mValidProgress

    //----------->
    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow: LiveData<String>
        get() = mErrorAll


    init {
        mValidProgress.postValue(false)
    }


    fun getLabelingNf() {
        viewModelScope.launch {
            mValidProgress.postValue(true)
            try {
                val request = this@LabelingPendencyNfViewModel.mRepository.labelinggetNf()
                if (request.isSuccessful) {
                    if (request.body()!!.isEmpty()) {
                        mError.postValue("Não há pedidos com pendências")
                    } else {
                        mSucess.postValue(request.body())
                    }
                } else {
                    mError.postValue("Não há pedidos com pendências")
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
                mValidProgress.postValue(false)
            }
        }
    }

    /** --------------------------------Labeling Pendency ViewModelFactory------------------------------------ */
    class LabelingPendencyViewModelFactory constructor(private val repository: EtiquetagemRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(LabelingPendencyNfViewModel::class.java)) {
                LabelingPendencyNfViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}