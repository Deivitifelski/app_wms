package com.documentos.wms_beirario.ui.armazens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.ui.login.LoginRepository
import com.documentos.wms_beirario.ui.login.LoginViewModel

class ArmazensViewModelFactory constructor(private val repository: ArmazensRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ArmazensViewModel::class.java)) {
            ArmazensViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}