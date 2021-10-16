package com.documentos.wms_beirario.ui.armazengem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.ui.login.LoginRepository
import com.documentos.wms_beirario.ui.login.LoginViewModel

class ArmazenagemViewModelFactory constructor(private val repository: ArmazenagemRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ArmazenagemViewModel::class.java)) {
            ArmazenagemViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}