package com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListEnderecosAuditoriaEstoque3Item
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch


class AuditoriaEstoqueViewModel2(val repository: AuditoriaEstoqueRepository) : ViewModel() {

    private var sucessGetAuditoriaEndereco = MutableLiveData<List<ListEnderecosAuditoriaEstoque3Item>?>()
    val sucessGetAuditoriaEnderecoShow get() = sucessGetAuditoriaEndereco

    private var sucessGetAuditoriaEmply = MutableLiveData<String>()
    val sucessGetAuditoriaEmplyShow get() = sucessGetAuditoriaEmply

    private var errorDb = MutableLiveData<String>()
    val errorDbShow get() = errorDb

    private var errorAll = MutableLiveData<String>()
    val errorAllShow get() = errorAll

    private var progress = MutableLiveData<Boolean>()
    val progressShow get() = progress


    fun getEnderecos(idArmazem: Int, token: String,idAuditoriaEstoque :String,estante:String) {
        viewModelScope.launch {
            try {
                progress.postValue(true)
                val result = repository.getListEnderecos(idArmazem = idArmazem,token = token,idAuditoriaEstoque = idAuditoriaEstoque,estante = estante)
                if (result.isSuccessful) {
                    if (result.body()?.isNotEmpty() == true) {
                        sucessGetAuditoriaEndereco.postValue(result.body())
                    } else {
                        sucessGetAuditoriaEmply.postValue("Sem estantes para auditoria selecionada.")
                    }
                } else {
                    errorDb.postValue(validaErrorDb(result))
                }
            } catch (e: Exception) {
                errorAll.postValue(validaErrorException(e))
            }finally {
                progress.postValue(false)
            }
        }
    }


    class AuditoriaEstoqueViewModel2Factory constructor(private val repository: AuditoriaEstoqueRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(AuditoriaEstoqueViewModel2::class.java)) {
                AuditoriaEstoqueViewModel2(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}