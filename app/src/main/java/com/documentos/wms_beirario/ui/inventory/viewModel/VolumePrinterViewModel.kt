package com.documentos.wms_beirario.ui.inventory.viewModel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.inventario.EtiquetaInventory
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import kotlinx.coroutines.launch

class VolumePrinterViewModel(private val mRepository: InventoryoRepository1) : ViewModel() {

    private var mSucessVol = MutableLiveData<EtiquetaInventory>()
    val mSucessVolShow: LiveData<EtiquetaInventory>
        get() = mSucessVol

    //----------->
    private var mErrorVol = MutableLiveData<String>()
    val mErrorVolShow: LiveData<String>
        get() = mErrorVol


    fun printer(idVol: String) {
        viewModelScope.launch {
            try {
                val request = this@VolumePrinterViewModel.mRepository.getInventoryPrinterVol(idVolume = idVol)
                if (request.isSuccessful) {
                    mSucessVol.postValue(request.body())
                }
            } catch (e: Exception) {
                mErrorVol.postValue("Ops! Erro inesperado...")
            }
        }
    }
}