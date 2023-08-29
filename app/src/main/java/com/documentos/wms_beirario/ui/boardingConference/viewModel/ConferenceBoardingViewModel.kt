package com.documentos.wms_beirario.ui.boardingConference.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.conferenceBoarding.BodyChaveBoarding
import com.documentos.wms_beirario.model.conferenceBoarding.BodySetBoarding
import com.documentos.wms_beirario.model.conferenceBoarding.ResponseConferenceBoarding
import com.documentos.wms_beirario.repository.conferenceBoarding.ConferenceBoardingRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConferenceBoardingViewModel(val mRepository: ConferenceBoardingRepository) : ViewModel() {

    //BUSCA LISTA TAREFAS -->
    private var mSucess = MutableLiveData<ResponseConferenceBoarding>()
    val mSucessShow get() = mSucess

    private var mSucessSet = MutableLiveData<ResponseConferenceBoarding>()
    val mSucessSetShow get() = mSucessSet

    private var mErrorHttp = MutableLiveData<String>()
    val mErrorHttpShow get() = mErrorHttp

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow get() = mErrorAll

    private var mProgress = MutableLiveData<Boolean>()
    val mProgressShow get() = mProgress

    //seta itens aprovados -->
    private var mSucessApproved = MutableLiveData<Unit>()
    val mSucessApprovedShow get() = mSucessApproved

    private var mErrorHttpApproved = MutableLiveData<String>()
    val mErrorHttpApprovedShow get() = mErrorHttpApproved

    private var mErrorAllApproved = MutableLiveData<String>()
    val mErrorAllApprovedShow get() = mErrorAllApproved

    //seta itens reprovados -->
    private var mSucessFailed = MutableLiveData<Unit>()
    val mSucessFailedShow get() = mSucessFailed

    private var mErrorHttpFailed = MutableLiveData<String>()
    val mErrorHttpFailedShow get() = mErrorHttpFailed

    private var mErrorAllFailed = MutableLiveData<String>()
    val mErrorAllFailedShow get() = mErrorAllFailed

    private var sucessEanOk = MutableLiveData<String>()
    val sucessEanOkShow: LiveData<String>
        get() = sucessEanOk

    private var error = MutableLiveData<String>()
    val errorShow: LiveData<String>
        get() = error


    fun pushNfe(body: BodyChaveBoarding, token: String, idArmazem: Int) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val request = this@ConferenceBoardingViewModel.mRepository.postConferenceBoarding1(
                    bodyChaveBoarding = body,
                    token,
                    idArmazem
                )
                if (request.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        request.body().let { listArmazens ->
                            mSucess.postValue(listArmazens)
                        }
                    }
                } else {
                    mErrorHttp.postValue(validaErrorDb(request = request))
                }
            } catch (e: Exception) {
                mErrorHttp.postValue(validaErrorException(e))
            } finally {
                mProgress.postValue(false)
            }
        }
    }

    fun pushNfeSet(body: BodyChaveBoarding, token: String, idArmazem: Int) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val request = this@ConferenceBoardingViewModel.mRepository.postConferenceBoarding1(
                    bodyChaveBoarding = body,
                    token, idArmazem
                )
                if (request.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        request.body().let { listArmazens ->
                            mSucessSet.postValue(listArmazens)
                        }
                    }
                } else {
                    mErrorHttp.postValue(validaErrorDb(request = request))
                }
            } catch (e: Exception) {
                mErrorHttp.postValue(validaErrorException(e))
            } finally {
                mProgress.postValue(false)
            }
        }
    }

    //SETA APROVADOS -->
    fun setApproved(body: BodySetBoarding, token: String, idArmazem: Int) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val request = this@ConferenceBoardingViewModel.mRepository.postSetaApproved2(
                    bodyChaveBoarding = body,
                    token,
                    idArmazem
                )
                if (request.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        request.body().let {
                            mSucessApproved.postValue(it)
                        }
                    }
                } else {
                    mErrorHttpApproved.postValue(validaErrorDb(request = request))
                }
            } catch (e: Exception) {
                mErrorAllApproved.postValue(validaErrorException(e))
            } finally {
                mProgress.postValue(false)
            }
        }
    }

    //Busca ean corrigido -->
    fun getEanOK(codBarras: String) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val res = mRepository.getNewEan(codBarras = codBarras)
                if (res.isSuccessful) {
                    sucessEanOk.postValue(res.body())
                } else {
                    error.postValue(validaErrorDb(res))
                }

            } catch (e: Exception) {
                error.postValue(validaErrorException(e))
            } finally {
                mProgress.postValue(false)
            }
        }
    }

    //SETA PENTENDE -->
    fun setPending(body: BodySetBoarding, token: String, idArmazem: Int) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val request = this@ConferenceBoardingViewModel.mRepository.postSetaDisapproved3(
                    bodyChaveBoarding = body,
                    token = token,
                    idArmazem = idArmazem
                )
                if (request.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        request.body().let { it ->
                            mSucessFailed.postValue(it)
                        }
                    }
                } else {
                    mErrorHttpFailed.postValue(validaErrorDb(request = request))
                }
            } catch (e: Exception) {
                mErrorAllFailed.postValue(validaErrorException(e))
            } finally {
                mProgress.postValue(false)
            }
        }
    }


    class ConferenceBoardingFactory constructor(private val repository: ConferenceBoardingRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ConferenceBoardingViewModel::class.java)) {
                ConferenceBoardingViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}
