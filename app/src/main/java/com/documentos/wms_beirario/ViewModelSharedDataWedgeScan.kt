package com.documentos.wms_beirario

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.documentos.wms_beirario.utils.SingleLiveEvent

class ViewModelSharedDataWedgeScan : ViewModel() {


    fun returnScan(newScan: String) {
        _mGetScan.value = newScan
    }

    var _mGetScan = SingleLiveEvent<String>()
    val mObserveScan: LiveData<String>
        get() = _mGetScan


}