package com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.auditoriaEstoque.response.request.BodyApontEndQtdAuditoriaEstoque
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListEnderecosAuditoriaEstoque3Item
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseDefaultErroAuditoriaEstoque
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseAuditoriaEstoqueAP
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch

class AuditoriaEstoqueApontmentoViewModelCv(val repository: AuditoriaEstoqueRepository) :
    ViewModel() {


    private var errorDb = MutableLiveData<String>()
    val errorDbShow get() = errorDb

    private var errorParDb = MutableLiveData<String>()
    val errorParDbShow get() = errorParDb

    private var errorVolDb = MutableLiveData<String>()
    val errorVolDbShow get() = errorVolDb

    private var errorDbApont = MutableLiveData<String>()
    val errorDbApontShow get() = errorDbApont

    private var errorAll = MutableLiveData<String>()
    val errorAllShow get() = errorAll


    private var progress = MutableLiveData<Boolean>()
    val progressShow get() = progress

    private var sucessGetProdutosAP =
        MutableLiveData<List<ResponseAuditoriaEstoqueAP>?>()
    val sucessGetProdutosShow get() = sucessGetProdutosAP

    private var sucessSaveEndQtdPar =
        MutableLiveData<ResponseDefaultErroAuditoriaEstoque>()
    val sucessSaveEndQtdShow get() = sucessSaveEndQtdPar

    private var sucessSaveEndQtdVol =
        MutableLiveData<ResponseDefaultErroAuditoriaEstoque>()
    val sucessSaveEndQtdVolShow get() = sucessSaveEndQtdVol

    private var sucessValidaContagem =
        MutableLiveData<ResponseDefaultErroAuditoriaEstoque>()
    val sucessValidaContagemShow get() = sucessValidaContagem

    private var sucessGetProdutosAPEmply = MutableLiveData<String>()
    val sucessGetProdutosEmplyShow get() = sucessGetProdutosAPEmply


    fun getProdutoAndressCv(
        endereco: ListEnderecosAuditoriaEstoque3Item,
        idAuditoria: String,
        token: String,
        idArmazem: Int,
    ) {
        viewModelScope.launch {
            try {
                progress.postValue(true)
                val result = repository.getProdutoAndressAP(
                    idEndereco = endereco.idEndereco,
                    token = token,
                    idAuditoriaEstoque = idAuditoria,
                    idArmazem = idArmazem
                )
                if (result.isSuccessful) {
                    if (result.body()?.isNotEmpty() == true) {
                        sucessGetProdutosAP.postValue(result.body())
                    } else {
                        sucessGetProdutosAPEmply.postValue("Sem estantes para auditoria selecionada.")
                    }
                } else {
                    errorDb.postValue(validaErrorDb(result))
                }
            } catch (e: Exception) {
                errorAll.postValue(validaErrorException(e))
            } finally {
                progress.postValue(false)
            }
        }
    }


    fun validaContagem(
        idAuditoria: String,
        token: String,
        idArmazem: Int,
        idEndereco: Int,
        contagem: Int
    ) {
        viewModelScope.launch {
            try {
                progress.postValue(true)
                val result = repository.validaContagem(
                    idArmazem = idArmazem,
                    token = token,
                    contagem = contagem,
                    idAuditoriaEstoque = idAuditoria,
                    idEndereco = idEndereco
                )
                if (result.isSuccessful) {
                    sucessValidaContagem.postValue(result.body())
                } else {
                    errorDbApont.postValue(validaErrorDb(result))
                }
            } catch (e: Exception) {
                errorAll.postValue(validaErrorException(e))
            } finally {
                progress.postValue(false)
            }
        }
    }

    fun saveParEndQtd(
        idAuditoria: String,
        token: String,
        idArmazem: Int,
        contagem: String,
        idEndereco: Int,
        body: BodyApontEndQtdAuditoriaEstoque
    ) {
        viewModelScope.launch {
            try {
                progress.postValue(true)
                val result = repository.saveEnderecoQtd(
                    idEndereco = idEndereco,
                    token = token,
                    contagem = contagem,
                    idAuditoriaEstoque = idAuditoria,
                    idArmazem = idArmazem,
                    body = body
                )
                if (result.isSuccessful) {
                    sucessSaveEndQtdPar.postValue(result.body())
                } else {
                    errorParDb.postValue(validaErrorDb(result))
                }
            } catch (e: Exception) {
                errorAll.postValue(validaErrorException(e))
            } finally {
                progress.postValue(false)
            }
        }
    }

    fun saveVolEndQtd(
        token: String,
        idEndereco: Int,
        idArmazem: Int,
        contagem: String,
        idAuditoria: String,
        body: BodyApontEndQtdAuditoriaEstoque
    ) {
        viewModelScope.launch {
            try {
                progress.postValue(true)
                val result = repository.saveEnderecoQtd(
                    idEndereco = idEndereco,
                    token = token,
                    contagem = contagem,
                    idAuditoriaEstoque = idAuditoria,
                    idArmazem = idArmazem,
                    body = body
                )
                if (result.isSuccessful) {
                    sucessSaveEndQtdVol.postValue(result.body())
                } else {
                    errorVolDb.postValue(validaErrorDb(result))
                }
            } catch (e: Exception) {
                errorAll.postValue(validaErrorException(e))
            } finally {
                progress.postValue(false)
            }
        }

    }


    class AuditoriaEstoqueApontmentoViewModelCvFactory constructor(private val repository: AuditoriaEstoqueRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(AuditoriaEstoqueApontmentoViewModelCv::class.java)) {
                AuditoriaEstoqueApontmentoViewModelCv(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}