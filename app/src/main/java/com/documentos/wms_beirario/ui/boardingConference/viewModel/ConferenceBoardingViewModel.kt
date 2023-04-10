package com.documentos.wms_beirario.ui.boardingConference.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.armazens.ArmazensResponse
import com.documentos.wms_beirario.model.conferenceBoarding.BodyChaveBoarding
import com.documentos.wms_beirario.model.conferenceBoarding.BodySetBoarding
import com.documentos.wms_beirario.model.conferenceBoarding.DataResponseBoarding
import com.documentos.wms_beirario.model.conferenceBoarding.ResponseConferenceBoarding
import com.documentos.wms_beirario.repository.armazens.ArmazensRepository
import com.documentos.wms_beirario.repository.conferenceBoarding.ConferenceBoardingRepository
import com.documentos.wms_beirario.ui.armazens.ArmazemViewModel
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class ConferenceBoardingViewModel(val mRepository: ConferenceBoardingRepository) : ViewModel() {

    //BUSCA LISTA TAREFAS -->
    private var mSucess = MutableLiveData<ResponseConferenceBoarding>()
    val mSucessShow get() = mSucess

    private var mErrorHttp = MutableLiveData<String>()
    val mErrorHttpShow get() = mErrorHttp

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow get() = mErrorAll

    private var mProgress = MutableLiveData<Boolean>()
    val mProgressShow get() = mProgress

    //seta itens aprovados -->
    private var mSucessApproved = MutableLiveData<List<DataResponseBoarding>>()
    val mSucessApprovedShow get() = mSucessApproved

    private var mErrorHttpApproved = MutableLiveData<String>()
    val mErrorHttpApprovedShow get() = mErrorHttpApproved

    private var mErrorAllApproved = MutableLiveData<String>()
    val mErrorAllApprovedShow get() = mErrorAllApproved

    //seta itens reprovados -->
    private var mSucessFailed = MutableLiveData<List<DataResponseBoarding>>()
    val mSucessFailedShow get() = mSucessFailed

    private var mErrorHttpFailed = MutableLiveData<String>()
    val mErrorHttpFailedShow get() = mErrorHttpFailed

    private var mErrorAllFailed = MutableLiveData<String>()
    val mErrorAllFailedShow get() = mErrorAllFailed


    fun pushNfe(body: BodyChaveBoarding) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val request = this@ConferenceBoardingViewModel.mRepository.postConferenceBoarding1(
                    bodyChaveBoarding = body
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

    //SETA APROVADOS -->
    fun setApproved(body: BodySetBoarding) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val request = this@ConferenceBoardingViewModel.mRepository.postSetaApproved2(
                    bodyChaveBoarding = body
                )
                if (request.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        request.body().let { listArmazens ->
                            mSucessApproved.postValue(listArmazens)
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

    //SETA REPROVADOS -->
    fun setFailed(body: BodySetBoarding) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val request = this@ConferenceBoardingViewModel.mRepository.postSetaDisapproved3(
                    bodyChaveBoarding = body
                )
                if (request.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        request.body().let { listArmazens ->
                            mSucessFailed.postValue(listArmazens)
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
