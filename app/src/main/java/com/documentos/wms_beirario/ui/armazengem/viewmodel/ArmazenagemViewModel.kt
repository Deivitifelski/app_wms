package com.documentos.wms_beirario.ui.armazengem.viewmodel

import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.armazenagem.ArmazenagemResponse
import com.documentos.wms_beirario.repository.armazenagem.ArmazenagemRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class ArmazenagemViewModel constructor(private var repository: ArmazenagemRepository) :
    ViewModel() {


    var messageError = MutableLiveData<String>()
    var mSucess = MutableLiveData<List<ArmazenagemResponse>>()
    var mValidProgress = MutableLiveData<Boolean>()


    fun getArmazenagem() {
        viewModelScope.launch {
            val request = repository.getArmazens()
            try {
                mValidProgress.value = false
                if (request.isSuccessful) {
                    request.let { list ->
                        mSucess.postValue(list.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    messageError.postValue(error2)
                }

            } catch (e: Exception) {
                mValidProgress.value = false
                messageError.postValue(e.toString())
            }
        }
    }

        class ArmazenagemViewModelFactory constructor(private val repository: ArmazenagemRepository) :
            ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return if (modelClass.isAssignableFrom(ArmazenagemViewModel::class.java)) {
                    ArmazenagemViewModel(this.repository) as T
                } else {
                    throw IllegalArgumentException("ViewModel Not Found")
                }
            }
        }
    }


