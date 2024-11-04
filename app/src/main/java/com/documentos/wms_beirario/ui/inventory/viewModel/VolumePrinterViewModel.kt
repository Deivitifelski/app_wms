package com.documentos.wms_beirario.ui.inventory.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.inventario.EtiquetaInventory
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class VolumePrinterViewModel(private val mRepository: InventoryoRepository1) : ViewModel() {

    private var mSucessVol = MutableLiveData<EtiquetaInventory>()
    val mSucessVolShow: LiveData<EtiquetaInventory>
        get() = mSucessVol

    //----------->
    private var mErrorVol = MutableLiveData<String>()
    val mErrorVolShow: LiveData<String>
        get() = mErrorVol

    //----------->
    private var mErrorAll = MutableLiveData<String>()
    val mErrorAllShow: LiveData<String>
        get() = mErrorAll


    fun printer(idVol: String) {
        viewModelScope.launch {
            try {
                val request =
                    this@VolumePrinterViewModel.mRepository.getInventoryPrinterVol(idVolume = idVol)
                if (request.isSuccessful) {
                    mSucessVol.postValue(request.body())
                } else {
                    mErrorAll.postValue("Erro ao tentar imprimir!")
                }
            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mErrorAll.postValue("Verifique sua internet!")
                    }
                    is SocketTimeoutException -> {
                        mErrorAll.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    is TimeoutException -> {
                        mErrorAll.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    else -> {
                        mErrorAll.postValue(e.toString())
                    }
                }
            }
        }
    }

    /** --------------------------------INVENTARIO ViewModelFactory------------------------------------ */
    class VolumePrinterModelFactory constructor(private val repository: InventoryoRepository1) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(VolumePrinterViewModel::class.java)) {
                VolumePrinterViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}