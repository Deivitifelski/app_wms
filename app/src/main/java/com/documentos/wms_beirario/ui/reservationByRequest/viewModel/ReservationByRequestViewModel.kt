package com.documentos.wms_beirario.ui.reservationByRequest.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.reservationByRequest.BodyAddReservation1
import com.documentos.wms_beirario.model.reservationByRequest.BodyAddVolReservationByRequest
import com.documentos.wms_beirario.model.reservationByRequest.ResponseReservationByPedido2
import com.documentos.wms_beirario.model.reservationByRequest.ResponseReservationPed1
import com.documentos.wms_beirario.repository.reservationByRequest.ReservationByRequestRepository
import com.documentos.wms_beirario.utils.extensions.validaErrorDb
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.launch

class ReservationByRequestViewModel(val mRepository: ReservationByRequestRepository) : ViewModel() {

    //------------------------------Chamada 1 ---------------------------------------------------->
    private var mSucess = MutableLiveData<ResponseReservationPed1>()
    val mSucessShow get() = mSucess

    private var mErrorHttp = MutableLiveData<String>()
    val mErrorHttpShow get() = mErrorHttp

    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow get() = mErrorAll

    //------------------------------Chamada 2 ------------------------------------------------->

    private var mSucessAddVol = MutableLiveData<List<ResponseReservationByPedido2>>()
    val mSucessAddVolShow get() = mSucessAddVol

    private var mProgress = MutableLiveData<Boolean>()
    val mProgressShow get() = mProgress

    fun addPedido(bodyAddReservation1: BodyAddReservation1) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val response =
                    this@ReservationByRequestViewModel.mRepository.postAddPedido(bodyAddReservation1 = bodyAddReservation1)
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

    fun addVol(body: BodyAddVolReservationByRequest) {
        viewModelScope.launch {
            try {
                mProgress.postValue(true)
                val response =
                    this@ReservationByRequestViewModel.mRepository.postAddVolume(body2 = body)
                if (response.isSuccessful) {
                    response.body().let { list ->
                        mSucessAddVol.postValue(list)
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


    class ReservationViewModelFactory constructor(private val repository: ReservationByRequestRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ReservationByRequestViewModel::class.java)) {
                ReservationByRequestViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }

}