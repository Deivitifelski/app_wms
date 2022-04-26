package com.documentos.wms_beirario.ui.etiquetagem.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequestModel3
import com.documentos.wms_beirario.model.etiquetagem.response.EtiquetagemResponse3
import com.documentos.wms_beirario.model.etiquetagem.response.ResponsePendencePedidoEtiquetagem
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException

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


    fun getLabelingNf() {
        viewModelScope.launch {
            mValidProgress.postValue(true)
            try {
                val request = this@LabelingPendencyNfViewModel.mRepository.labelinggetNf()
                if (request.isSuccessful) {
                    if (request.body()!!.isEmpty()){
                        mError.postValue("Não há pedidos com pendências")
                    }else{
                        mSucess.postValue(request.body())
                    }
                } else {
                    mError.postValue("Não há pedidos com pendências")
                }
            } catch (e: Exception) {
                when(e){
                    is ConnectException ->{
                        mError.postValue("Ops!Verifique sua internet...")
                    }
                    else ->{
                        mError.postValue("Ops! Erro inesperado...")
                    }
                }

            }finally {
                mValidProgress.postValue(false)
            }
        }
    }
}