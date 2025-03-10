package com.documentos.wms_beirario.ui.qualityControl.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.qualityControl.BodyFinishQualityControl
import com.documentos.wms_beirario.model.qualityControl.BodyGenerateRequestControlQuality
import com.documentos.wms_beirario.model.qualityControl.BodySetAprovadoQuality
import com.documentos.wms_beirario.model.qualityControl.BodySetPendenceQuality
import com.documentos.wms_beirario.model.qualityControl.ResponseControlQuality1
import com.documentos.wms_beirario.model.qualityControl.ResponseGenerateRequestControlQuality
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

    private var mErrorFinish = MutableLiveData<String>()
    val mErrorFinishShow get() = mErrorFinish

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow get() = mErrorAll

    //----------------------------CHAMADA APROVADOS ---------------------------------------------->
    private var mSucessAprovado = MutableLiveData<Unit>()
    val mSucessAprovadoShow get() = mSucessAprovado

    //----------------------------CHAMADA REPROVADOS ---------------------------------------------->
    private var mSucessReprovado = MutableLiveData<Unit>()
    val mSucessReprovadodoShow get() = mSucessReprovado

    //----------------------------CHAMADA PENDENTES ---------------------------------------------->
    private var mSucessPendentes = MutableLiveData<Unit>()
    val mSucessPendentesShow get() = mSucessPendentes

    //----------------------------GERA REQUISIÇÃO ---------------------------------------------->
    private var mSucessGenerateRequest =
        MutableLiveData<List<ResponseGenerateRequestControlQuality>>()
    val mSucessGenerateRequestShow get() = mSucessGenerateRequest

    private var mErrorHttpGenerateRequest = MutableLiveData<String>()
    val mErrorHttpGenerateRequestShow get() = mErrorHttpGenerateRequest

    private var mErrorAllGenerateRequest = MutableLiveData<String>()
    val mErrorAllGenerateRequestShow get() = mErrorAllGenerateRequest

    //----------------------------CHAMADA FINALIZAR ---------------------------------------------->
    private var mSucessFinish = MutableLiveData<Unit>()
    val mSucessFinishShow get() = mSucessFinish


    //validationCall para validar o campo a ser mostrada
    fun getTask1(codBarrasEnd: String, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val response =
                    this@QualityControlViewModel.mRep.getTaskQualityControl1(
                        codBarrasEnd = codBarrasEnd,
                        idArmazem,
                        token
                    )
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
    fun setAprovado(body: BodySetAprovadoQuality, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val response =
                    this@QualityControlViewModel.mRep.postSetAprovadosQualityControl(
                        body = body,
                        idArmazem = idArmazem,
                        token = token
                    )
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

    fun setRejeitado(body: BodySetAprovadoQuality, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val response =
                    this@QualityControlViewModel.mRep.postSetReprovadosQualityControl(
                        body = body,
                        idArmazem = idArmazem,
                        token = token
                    )
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

    fun generateRequest(body: BodyGenerateRequestControlQuality, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val response =
                    this@QualityControlViewModel.mRep.postGenerateRequestQualityControl(
                        body = body,
                        idArmazem = idArmazem,
                        token = token
                    )
                if (response.isSuccessful) {
                    response.body().let { list ->
                        mSucessGenerateRequest.postValue(list)
                    }
                } else {
                    mErrorAllGenerateRequest.postValue(validaErrorDb(response))
                }
            } catch (e: Exception) {
                mErrorHttpGenerateRequestShow.postValue(validaErrorException(e))
            } finally {
                mProgress.postValue(false)
            }
        }
    }

    fun finish(body: BodyFinishQualityControl, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val response =
                    this@QualityControlViewModel.mRep.postFinishQualityControl(
                        body = body,
                        idArmazem = idArmazem,
                        token = token
                    )
                if (response.isSuccessful) {
                    response.body().let { list ->
                        mSucessFinish.postValue(list)
                    }
                } else {
                    mErrorFinish.postValue(validaErrorDb(response))
                }
            } catch (e: Exception) {
                mErrorFinish.postValue(validaErrorException(e))
            } finally {
                mProgress.postValue(false)
            }
        }
    }

    fun setPendente(body: BodySetPendenceQuality, idArmazem: Int, token: String) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val response =
                    this@QualityControlViewModel.mRep.postSetPendenteQualityControl(
                        body = body,
                        idArmazem = idArmazem,
                        token = token
                    )
                if (response.isSuccessful) {
                    response.body().let { list ->
                        mSucessPendentes.postValue(list)
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