package com.documentos.wms_beirario.ui.consultacodbarras

import EnderecoModel
import androidx.lifecycle.*
import com.documentos.wms_beirario.model.codBarras.CodBarrasProdutoResponseModel
import com.documentos.wms_beirario.model.codBarras.CodigodeBarrasResponse
import com.documentos.wms_beirario.model.codBarras.VolumeModelCB
import com.documentos.wms_beirario.repository.consultacodbarras.ConsultaCodBarrasRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

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

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow: LiveData<String>
        get() = mErrorAll

    private var mProgress = MutableLiveData<Boolean>()
    val mProgressShow: LiveData<Boolean>
        get() = mProgress


    fun getCodBarras(codigoBarras: String) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val request =
                    this@ConsultaCodBarrasViewModel.mRepository.getCodBarras(codigoBarras = codigoBarras)
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

    fun checkBarCode(mDados: CodigodeBarrasResponse) {
        val mObjVolume = mDados.volumeCodBarras!!.descricaoEmbalagem
        val mObjProduto = mDados.produtoCodBarras!!.nome
        val mObjEndereco = mDados.enderecoCodBarras!!.enderecoVisual
        when {
            mObjVolume == null && mObjProduto == null -> {
                mResponseEndereco.postValue(mDados.enderecoCodBarras)
            }
            mObjVolume == null && mObjEndereco == null -> {
                mResponseProduto.postValue(mDados.produtoCodBarras)
            }
            mObjProduto == null && mObjEndereco == null -> {
                mResponseVolume.postValue(mDados.volumeCodBarras)
            }
        }
    }

    /** -----------------------CONSULTA COD DE BARRAS ViewModelFactory--------------------------->*/
    class ConsultaCodBarrasVmfACTORY constructor(private val repository: ConsultaCodBarrasRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ConsultaCodBarrasViewModel::class.java)) {
                ConsultaCodBarrasViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}
