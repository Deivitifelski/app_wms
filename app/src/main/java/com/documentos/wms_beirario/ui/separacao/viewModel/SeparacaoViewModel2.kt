package com.documentos.wms_beirario.ui.separacao.viewModel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.separation.RequestSeparationArraysAndares1
import com.documentos.wms_beirario.model.separation.ResponseEstantes
import com.documentos.wms_beirario.model.separation.filtros.BodyEstantesFiltro
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch
import org.json.JSONObject

class SeparacaoViewModel2(private val mRepository: SeparacaoRepository) : ViewModel() {

    //-------------------------->
    private var mSucess = MutableLiveData<ResponseEstantes>()
    val mShowShow: LiveData<ResponseEstantes>
        get() = mSucess

    //-------------------------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //--------------------------->
    private var mValidaProgress = MutableLiveData<Boolean>()
    val mValidaProgressShow: LiveData<Boolean>
        get() = mValidaProgress


    /**---------------------CHAMADA 01 BUSCA DAS ESTANTES ----------------------------------------*/
    fun postItensEstantes(
        body: BodyEstantesFiltro,
        ideArmazem: Int,
        token: String
    ) {
        viewModelScope.launch {
            try {
                mValidaProgress.postValue(true)
                val request =
                    this@SeparacaoViewModel2.mRepository.posBuscaEstantesSeparation(
                        separationItensCheck,
                        ideArmazem,
                        token
                    )
                mValidaProgress.value = false
                if (request.isSuccessful) {
                    mSucess.postValue(request.body())
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val messageEdit = error2.replace("NAO", "NÃO")
                    mError.postValue(messageEdit)
                }
            } catch (e: Exception) {
                mError.postValue(validaErrorException(e))
            } finally {
                mValidaProgress.postValue(false)
            }
        }
    }

    class SeparacaoItensViewModelFactory2 constructor(private val repository: SeparacaoRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(SeparacaoViewModel2::class.java)) {
                SeparacaoViewModel2(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}

