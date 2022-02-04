package com.documentos.wms_beirario.ui.consultacodbarras

import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.*
import com.documentos.wms_beirario.model.codBarras.CodigodeBarrasResponse
import com.documentos.wms_beirario.repository.consultacodbarras.ConsultaCodBarrasRepository
import com.example.coletorwms.model.codBarras.Cod.EnderecoModel
import com.example.coletorwms.model.codBarras.CodBarrasProdutoResponseModel
import com.example.coletorwms.model.codBarras.VolumeModelCB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException

class ConsultaCodBarrasViewModel(private var mRepository: ConsultaCodBarrasRepository) :
    ViewModel() {


    private var mSucess = MutableLiveData<CodigodeBarrasResponse>()
    val mSucessShow: LiveData<CodigodeBarrasResponse>
        get() = mSucess

    //---------------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //---------------->
    private var mResponseVolume = MutableLiveData<VolumeModelCB>()
    val mResponseCheckVolume: LiveData<VolumeModelCB>
        get() = mResponseVolume

    //---------------->
    private var mResponseProduto = MutableLiveData<CodBarrasProdutoResponseModel>()
    val mResponseCheckProduto: LiveData<CodBarrasProdutoResponseModel>
        get() = mResponseProduto

    //---------------->
    private var mResponseEndereco = MutableLiveData<EnderecoModel>()
    val mResponseCheckEndereco: LiveData<EnderecoModel>
        get() = mResponseEndereco


    fun getCodBarras(codigoBarras: String) {
        viewModelScope.launch {
            try {
                val request =
                    this@ConsultaCodBarrasViewModel.mRepository.getCodBarras(codigoBarras = codigoBarras)
                if (request.isSuccessful) {
                    viewModelScope.launch(Dispatchers.Main) {
                        mSucess.postValue(request.body())
                    }
                } else {
                        val error = request.errorBody()!!.string()
                        val error2 = JSONObject(error).getString("message")
                        val messageEdit = error2.replace("NAO", "NÃO")
                        mError.postValue(messageEdit)
                }

            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mError.value = "Verifique sua conexão!"
                    }
                    else -> {
                        mError.value = "Ops! Erro inesperado..."
                    }
                }
            }
        }
    }

    fun checkBarCode(mDados: CodigodeBarrasResponse) {
        val mObjVolume = mDados.volumeCodBarras!!.descricaoEmbalagem
        val mObjProduto = mDados.produtoCodBarras!!.nome
        val mObjEndereco = mDados.enderecoCodBarras!!.enderecoVisual
        when {
            mObjVolume == null && mObjProduto == null -> {
                mResponseEndereco.postValue(mDados.enderecoCodBarras!!)
            }
            mObjVolume == null && mObjEndereco == null -> {
                mResponseProduto.postValue(mDados.produtoCodBarras!!)
            }
            mObjProduto == null && mObjEndereco == null -> {
                mResponseVolume.postValue(mDados.volumeCodBarras!!)
            }
        }
    }

    fun visibilityProgress(mProgress: ProgressBar, visibility: Boolean) {
        if (visibility) mProgress.visibility = View.VISIBLE else mProgress.visibility =
            View.INVISIBLE
    }
}