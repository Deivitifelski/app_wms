package com.documentos.wms_beirario.ui.qualityControl.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.qualityControl.*
import com.documentos.wms_beirario.repository.qualityControl.QualityControlRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch

class QualityControlViewModel(private val mRep: QualityControlRepository) : ViewModel() {

    private var mProgress = MutableLiveData<Boolean>()
    val mProgressShow get() = mProgress

    //------------------------------Chamada 1 GET TASK ---------------------------------------------------->
    private var mSucess = MutableLiveData<ResponseControlQuality1>()
    val mSucessShow get() = mSucess

    private var mErrorHttp = MutableLiveData<String>()
    val mErrorHttpShow get() = mErrorHttp

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow get() = mErrorAll

    //----------------------------CHAMADA APROVADOS ---------------------------------------------->
    private var mSucessAprovado = MutableLiveData<Unit>()
    val mSucessAprovadoShow get() = mSucessAprovado

    //----------------------------CHAMADA REPROVADOS ---------------------------------------------->
    private var mSucessReprovado = MutableLiveData<Unit>()
    val mSucessReprovadodoShow get() = mSucessReprovado

    //----------------------------GERA REQUISIÇÃO ---------------------------------------------->
    private var mSucessGenerateRequest = MutableLiveData<ResponseGenerateRequestControlQuality>()
    val mSucessGenerateRequestShow get() = mSucessGenerateRequest

    private var mErrorHttpGenerateRequest = MutableLiveData<String>()
    val mErrorHttpGenerateRequestShow get() = mErrorHttpGenerateRequest

    private var mErrorAllGenerateRequest = MutableLiveData<String>()
    val mErrorAllGenerateRequestShow get() = mErrorAllGenerateRequest

    //----------------------------CHAMADA FINALIZAR ---------------------------------------------->
    private var mSucessFinish = MutableLiveData<Unit>()
    val mSucessFinishShow get() = mSucessFinish


    fun getTask1(codBarrasEnd: String) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val response =
                    this@QualityControlViewModel.mRep.getTaskQualityControl1(codBarrasEnd = codBarrasEnd)
                if (response.isSuccessful) {
                    response.body().let { list ->
                        mSucess.postValue(list)
                    }
                } else {
                    mErrorHttp.postValue(validaErrorDb(response))
                }
            } catch (e: Exception) {
                mErrorAll.postValue(validaErrorException(e))
            } finally {
                mProgress.postValue(false)
            }
        }
    }


    //Set aprovado -->
    fun setAprovado(body: BodySetAprovadoQuality) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val response =
                    this@QualityControlViewModel.mRep.postSetAprovadosQualityControl(body = body)
                if (response.isSuccessful) {
                    response.body().let { list ->
                        mSucessAprovado.postValue(list)
                    }
                } else {
                    mErrorHttp.postValue(validaErrorDb(response))
                }
            } catch (e: Exception) {
                mErrorAll.postValue(validaErrorException(e))
            } finally {
                mProgress.postValue(false)
            }
        }
    }

    fun setRejeitado(body: BodySetAprovadoQuality) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val response =
                    this@QualityControlViewModel.mRep.postSetReprovadosQualityControl(body = body)
                if (response.isSuccessful) {
                    response.body().let { list ->
                        mSucessReprovado.postValue(list)
                    }
                } else {
                    mErrorHttp.postValue(validaErrorDb(response))
                }
            } catch (e: Exception) {
                mErrorAll.postValue(validaErrorException(e))
            } finally {
                mProgress.postValue(false)
            }
        }
    }

    fun generateRequest(body: BodyGenerateRequestControlQuality) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val response =
                    this@QualityControlViewModel.mRep.postGenerateRequestQualityControl(body = body)
                if (response.isSuccessful) {
                    response.body().let { list ->
                        mSucessGenerateRequest.postValue(list)
                    }
                } else {
                    mErrorHttpGenerateRequest.postValue(validaErrorDb(response))
                }
            } catch (e: Exception) {
                mErrorAllGenerateRequest.postValue(validaErrorException(e))
            } finally {
                mProgress.postValue(false)
            }
        }
    }

    fun finish(body: BodyFinishQualityControl) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val response =
                    this@QualityControlViewModel.mRep.postFinishQualityControl(body = body)
                if (response.isSuccessful) {
                    response.body().let { list ->
                        mSucessFinish.postValue(list)
                    }
                } else {
                    mErrorHttp.postValue(validaErrorDb(response))
                }
            } catch (e: Exception) {
                mErrorAll.postValue(validaErrorException(e))
            } finally {
                mProgress.postValue(false)
            }
        }
    }


    class QualityControlViewModelFactory1 constructor(private val repository: QualityControlRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(QualityControlViewModel::class.java)) {
                QualityControlViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }

}