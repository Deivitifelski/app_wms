package com.documentos.wms_beirario.ui.boardingConference.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.armazens.ArmazensResponse
import com.documentos.wms_beirario.model.conferenceBoarding.BodyChaveBoarding
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

    private var mSucess = MutableLiveData<ResponseConferenceBoarding>()
    val mSucessShow get() = mSucess

    private var mErrorHttp = MutableLiveData<String>()
    val mErrorHttpShow get() = mErrorHttp

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow get() = mErrorAll

    private var mProgress = MutableLiveData<Boolean>()
    val mProgressShow get() = mProgress


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
