package com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListEnderecosAuditoriaEstoque3Item
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListEstantesAuditoriaEstoqueItem
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch


class AuditoriaEstoqueEstantesViewModel(val repository: AuditoriaEstoqueRepository) : ViewModel() {

    private var sucessGetEstantes = MutableLiveData<List<ListEstantesAuditoriaEstoqueItem>?>()
    val sucessGetEstantesShow get() = sucessGetEstantes

    private var errorDb = MutableLiveData<String>()
    val errorDbShow get() = errorDb

    private var errorAll = MutableLiveData<String>()
    val errorAllShow get() = errorAll

    private var progress = MutableLiveData<Boolean>()
    val progressShow get() = progress


    fun getEstantes(idArmazem: Int, token: String, idAuditoriaEstoque: String) {
        viewModelScope.launch {
            try {
                progress.postValue(true)
                val result = repository.getListEstantes(
                    idArmazem = idArmazem,
                    token = token,
                    idAuditoriaEstoque = idAuditoriaEstoque
                )
                if (result.isSuccessful) {
                    sucessGetEstantes.postValue(result.body())
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


    class AuditoriaEstoqueEstantesViewModelFactory constructor(private val repository: AuditoriaEstoqueRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(AuditoriaEstoqueEstantesViewModel::class.java)) {
                AuditoriaEstoqueEstantesViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}