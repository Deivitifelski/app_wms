package com.documentos.wms_beirario.ui.rfid_recebimento.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.recebimentoRfid.BodyGetRecebimentoRfidTagsEpcs
import com.documentos.wms_beirario.model.recebimentoRfid.BodyRecbimentoRfidPostDetalhesEpc
import com.documentos.wms_beirario.model.recebimentoRfid.BodyRecebimentoRfidPullTraffic
import com.documentos.wms_beirario.model.recebimentoRfid.RecebimentoRfidEpcResponse
import com.documentos.wms_beirario.model.recebimentoRfid.ResponseGetRecebimentoNfsPendentes
import com.documentos.wms_beirario.model.recebimentoRfid.ResponseSearchDetailsEpc
import com.documentos.wms_beirario.repository.recebimentoRfid.RecebimentoRfidRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch
import java.util.ArrayList

class RecebimentoRfidViewModel(val repository: RecebimentoRfidRepository) : ViewModel() {


    private var _progress = MutableLiveData<Boolean>()
    val progress get() = _progress

    private var _errorDb = MutableLiveData<String>()
    val errorDb get() = _errorDb


    private var _sucessReturnPercentage =
        MutableLiveData<Int>()
    val sucessReturnPercentage get() = _sucessReturnPercentage

    private var _sucessRetornaNfsPendentes =
        MutableLiveData<List<ResponseGetRecebimentoNfsPendentes>>()
    val sucessRetornaNfsPendentes get() = _sucessRetornaNfsPendentes

    private var _sucessRetornaNfsPendentesEmply =
        MutableLiveData<Boolean>()
    val sucessRetornaNfsPendentesEmply get() = _sucessRetornaNfsPendentesEmply


    private var _sucessRetornaEpc = MutableLiveData<List<RecebimentoRfidEpcResponse>>()
    val sucessRetornaEpc get() = _sucessRetornaEpc

    private var _sucessRetornaEpcEmply = MutableLiveData<String>()
    val sucessRetornaEpcEmply get() = _sucessRetornaEpcEmply


    private var _sucessReturnDetailsEpc =
        MutableLiveData<List<ResponseSearchDetailsEpc>>()
    val sucessReturnDetailsEpc get() = _sucessReturnDetailsEpc


    private var _sucessPullTraffic =
        MutableLiveData<Unit>()
    val sucessPullTraffic get() = _sucessPullTraffic


    fun getNfsPendentes(idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                _progress.postValue(true)
                val result = repository.buscaNfsPendentes(idArmazem = idArmazem, token = token)
                if (result.isSuccessful) {
                    if (result.body()?.isNotEmpty() == true) {
                        sucessRetornaNfsPendentes.postValue(result.body())
                        _sucessRetornaNfsPendentesEmply.postValue(false)
                    } else {
                        _sucessRetornaNfsPendentesEmply.postValue(true)
                    }
                } else {
                    _errorDb.postValue(validaErrorDb(result))
                }
            } catch (e: Exception) {
                _errorDb.postValue(validaErrorException(e))
            } finally {
                _progress.postValue(false)
            }
        }
    }

    /**Retorna a tag e o EPC relacionadas as Nfs selecionadas*/
    fun getTagsEpcs(
        token: String,
        idArmazem: Int,
        listIdDoc: ArrayList<ResponseGetRecebimentoNfsPendentes>
    ) {
        viewModelScope.launch {
            try {
                _progress.postValue(true)
                val result = repository.postTagsEpcs(
                    idArmazem = idArmazem,
                    token = token,
                    body = BodyGetRecebimentoRfidTagsEpcs(listIdDoc.map { it.idDocumento })
                )
                if (result.isSuccessful) {
                    if (result.body().isNullOrEmpty()){
                        val nfs = StringBuilder()
                         listIdDoc.forEach { nf ->
                            nfs.append("${nf.nfNumero}\n")
                        }
                        _sucessRetornaEpcEmply.postValue("Não foi retornado etiquetas relacionadas as Nfs:\n${nfs}Você será redirecionado a tela anterior.")
                    }else {
                        _sucessRetornaEpc.postValue(result.body())
                    }
                } else {
                    _errorDb.postValue(validaErrorDb(result))
                }
            } catch (e: Exception) {
                _errorDb.postValue(validaErrorException(e))
            } finally {
                _progress.postValue(false)
            }
        }
    }

    fun searchDetailEpc(token: String?, idArmazem: Int, body: BodyRecbimentoRfidPostDetalhesEpc) {
        viewModelScope.launch {
            try {
                _progress.postValue(true)
                val result = repository.searchDetailEpc(
                    idArmazem = idArmazem,
                    token = token,
                    body = body
                )
                if (result.isSuccessful) {
                    _sucessReturnDetailsEpc.postValue(result.body())
                } else {
                    if (result.code() == 404) {
                        _errorDb.postValue("Erro 404, não foi possível encontrar os dados")
                    } else {
                        _errorDb.postValue(validaErrorDb(result))
                    }
                }
            } catch (e: Exception) {
                _errorDb.postValue(validaErrorException(e))
            } finally {
                _progress.postValue(false)
            }
        }
    }

    fun trafficPull(
        idArmazem: Int,
        token: String,
        listIdDoc: ArrayList<ResponseGetRecebimentoNfsPendentes>
    ) {
        viewModelScope.launch {
            try {
                _progress.postValue(true)
                val body =
                    BodyRecebimentoRfidPullTraffic(listIdDoc = listIdDoc.map { it.idDocumento })
                progress.postValue(true)
                val result = repository.trafficPull(
                    idArmazem = idArmazem,
                    token = token,
                    body = body
                )
                if (result.isSuccessful) {
                    _sucessPullTraffic.postValue(result.body())
                } else {
                    if (result.code() == 404) {
                        _errorDb.postValue("Erro 404, não foi possível encontrar os dados")
                    } else {
                        _errorDb.postValue(validaErrorDb(result))
                    }
                }
            } catch (e: Exception) {
                _errorDb.postValue(validaErrorException(e))
            } finally {
                _progress.postValue(false)
            }
        }

    }

    fun calculateProximityPercentage(rssi: Int) {
        val adjustedRssi = rssi.coerceIn(-90, -30)
        _sucessReturnPercentage.postValue(((adjustedRssi + 90) * (100f / 60f)).toInt())
    }



    class RecebimentoRfidViewModelFactory constructor(private val repository: RecebimentoRfidRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(RecebimentoRfidViewModel::class.java)) {
                RecebimentoRfidViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}