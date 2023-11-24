package com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListEnderecosAuditoriaEstoque3Item
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasItem
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseProdutoEnderecoAuditoriaEstoqueCv
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

    private var sucessGetProdutos =
        MutableLiveData<List<ResponseProdutoEnderecoAuditoriaEstoqueCv>?>()
    val sucessGetProdutosShow get() = sucessGetProdutos

    private var sucessGetProdutosEmply = MutableLiveData<String>()
    val sucessGetProdutosEmplyShow get() = sucessGetProdutosEmply


    fun getProdutoAndress(
        endereco: ListEnderecosAuditoriaEstoque3Item,
        auditoria: ListaAuditoriasItem,
        token: String,
        idArmazem: Int,
    ) {
        viewModelScope.launch {
            try {
                progress.postValue(true)
                val result = repository.getProdutoAndress(
                    idEndereco = endereco.idEndereco,
                    token = token,
                    idAuditoriaEstoque = auditoria.id,
                    idArmazem = idArmazem
                )
                if (result.isSuccessful) {
                    if (result.body()?.isNotEmpty() == true) {
                        sucessGetProdutos.postValue(result.body())
                    } else {
                        sucessGetProdutosEmply.postValue("Sem estantes para auditoria selecionada.")
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