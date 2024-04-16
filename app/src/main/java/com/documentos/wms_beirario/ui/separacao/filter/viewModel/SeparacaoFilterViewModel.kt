package com.documentos.wms_beirario.ui.separacao.filter.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.separation.filtros.ResponseDocTransSeparacao
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch

class SeparacaoFilterViewModel(val repository: SeparacaoRepository) : ViewModel() {

    private var sucess = MutableLiveData<List<ResponseDocTransSeparacao>>()
    val sucessShow: LiveData<List<ResponseDocTransSeparacao>>
        get() = sucess

    private var sucessTrans = MutableLiveData<List<ResponseDocTransSeparacao>>()
    val sucessTransShow: LiveData<List<ResponseDocTransSeparacao>>
        get() = sucessTrans

    //-------------------------->
    private var error = MutableLiveData<String>()
    val errorShow: LiveData<String>
        get() = error

    //--------------------------->
    private var progress = MutableLiveData<Boolean>()
    val progressShow: LiveData<Boolean>
        get() = progress


    fun getListFiles(token: String, idArmazem: Int) {
        viewModelScope.launch {
            try {
                progress.postValue(true)
                val result = repository.getListFiles(token = token, idArmazem = idArmazem)
                if (result.isSuccessful) {
                    sucess.postValue(result.body())
                } else {
                    error.postValue(validaErrorDb(result))
                }

            } catch (e: Exception) {
                error.postValue(validaErrorException(e))
            } finally {
                progress.postValue(false)
            }
        }
    }

    fun getListTrans(token: String, idArmazem: Int) {
        viewModelScope.launch {
            try {
                progress.postValue(true)
                val result = repository.getListTrans(token = token, idArmazem = idArmazem)
                if (result.isSuccessful) {
                    sucessTrans.postValue(result.body())
                } else {
                    error.postValue(validaErrorDb(result))
                }

            } catch (e: Exception) {
                error.postValue(validaErrorException(e))
            } finally {
                progress.postValue(false)
            }
        }
    }

    class SeparacaoFilterViewModelFactory(private val repository: SeparacaoRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(SeparacaoFilterViewModel::class.java)) {
                SeparacaoFilterViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}