package com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseAuditoriaEstoqueDetalhes
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch


class AuditoriaEstoqueDetalhesViewModel(val repository: AuditoriaEstoqueRepository) : ViewModel() {

    private var sucessGetDetalhes = MutableLiveData<List<ResponseAuditoriaEstoqueDetalhes>?>()
    val sucessGetDetalhesShow get() = sucessGetDetalhes


    private var errorDb = MutableLiveData<String>()
    val errorDbShow get() = errorDb

    private var errorAll = MutableLiveData<String>()
    val errorAllShow get() = errorAll

    private var progress = MutableLiveData<Boolean>()
    val progressShow get() = progress


    fun getDetalhes(
        idArmazem: Int,
        token: String,
        idAuditoriaEstoque: String,
        idProduto: String,
        idEndereco: String
    ) {
        viewModelScope.launch {
            try {
                progress.postValue(true)
                val result = repository.getDetalhes(
                    idArmazem = idArmazem,
                    token = token,
                    idAuditoriaEstoque = idAuditoriaEstoque,
                    idEndereco = idEndereco,
                    idProduto = idProduto
                )
                if (result.isSuccessful) {
                    sucessGetDetalhes.postValue(result.body())
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


    class AuditoriaEstoqueDetalhesViewModelFactory constructor(private val repository: AuditoriaEstoqueRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(AuditoriaEstoqueDetalhesViewModel::class.java)) {
                AuditoriaEstoqueDetalhesViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}