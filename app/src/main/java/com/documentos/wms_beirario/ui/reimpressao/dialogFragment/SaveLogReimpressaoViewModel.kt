package com.documentos.wms_beirario.ui.reimpressao.dialogFragment

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.logPrinter.BodySaveLogPrinter
import com.documentos.wms_beirario.repository.reimpressao.ReimpressaoRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch

class SaveLogReimpressaoViewModel(val mRepository: ReimpressaoRepository) : ViewModel() {


    private var mErrorHttp = MutableLiveData<String>()
    val mErrorHttpShow get() = mErrorHttp

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow get() = mErrorAll

    private var mSucessSaveLog = MutableLiveData<Unit>()
    val mSucessSaveLogShow: LiveData<Unit>
        get() = mSucessSaveLog


    fun saveLog(bodySaveLogPrinter: BodySaveLogPrinter, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                val request =
                    this@SaveLogReimpressaoViewModel.mRepository.saveLogPrinterRepository(
                        bodySaveLogPrinter = bodySaveLogPrinter,
                        idArmazem,
                        token
                    )
                if (request.isSuccessful) {
                    mSucessSaveLog.postValue(request.body())
                } else {
                    mErrorHttp.postValue(validaErrorDb(request))
                }
            } catch (e: Exception) {
                mErrorAll.postValue(validaErrorException(e))
            }
        }
    }

    /** --------------------------------REIMPRESSAO ViewModelFactory------------------------------------ */
    class SaveLogReimpressaoViewModelFactory constructor(private val repository: ReimpressaoRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(SaveLogReimpressaoViewModel::class.java)) {
                SaveLogReimpressaoViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}