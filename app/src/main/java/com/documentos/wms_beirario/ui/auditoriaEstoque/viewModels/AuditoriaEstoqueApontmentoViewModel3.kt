package com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.auditoriaEstoque.response.request.BodyApontEndProdutoAuditoriaEstoque
import com.documentos.wms_beirario.model.auditoriaEstoque.response.request.BodyApontEndQtdAuditoriaEstoque
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListEnderecosAuditoriaEstoque3Item
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasItem
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseAuditoriaEstoqueAp
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseAuditoriaEstoqueApAdapter
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseProdutoEnderecoAuditoriaEstoqueAp
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch

class AuditoriaEstoqueApontmentoViewModel3(val repository: AuditoriaEstoqueRepository) :
    ViewModel() {


    private var errorDb = MutableLiveData<String>()
    val errorDbShow get() = errorDb

    private var errorAll = MutableLiveData<String>()
    val errorAllShow get() = errorAll

    private var progress = MutableLiveData<Boolean>()
    val progressShow get() = progress

    private var sucessGetProdutosAP =
        MutableLiveData<List<ResponseAuditoriaEstoqueAp>?>()
    val sucessGetProdutosShow get() = sucessGetProdutosAP

    private var sucessGetProdutosAPEmply = MutableLiveData<String>()
    val sucessGetProdutosEmplyShow get() = sucessGetProdutosAPEmply


    fun getProdutoAndressAP(
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

    fun apontaProdutoAP(
        token: String,
        idArmazem: Int,
        body: BodyApontEndProdutoAuditoriaEstoque,
        contagem: String,
        idEndereco: String,
        idAuditoriaEstoque: String
    ) {
        viewModelScope.launch {
            try {
                progress.postValue(true)
                val result = repository.apontaProduto(
                    idArmazem = idArmazem,
                    token = token,
                    body = body,
                    contagem = contagem,
                    idAuditoriaEstoque = idAuditoriaEstoque,
                    idEndereco = idEndereco
                )
                if (result.isSuccessful) {
                    Log.e("--->", "${result.body()}")
//                    if (result.body()?.isNotEmpty() == true) {
//                        sucessGetProdutosAP.postValue(result.body())
//                    } else {
//                        sucessGetProdutosAPEmply.postValue("Sem estantes para auditoria selecionada.")
//                    }
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


    class AuditoriaEstoqueApontmentoViewModelFactory3 constructor(private val repository: AuditoriaEstoqueRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(AuditoriaEstoqueApontmentoViewModel3::class.java)) {
                AuditoriaEstoqueApontmentoViewModel3(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}