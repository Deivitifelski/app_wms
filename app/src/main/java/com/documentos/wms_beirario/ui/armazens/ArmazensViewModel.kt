package com.documentos.wms_beirario.ui.armazens

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.armazens.ArmazensResponse
import com.documentos.wms_beirario.repository.armazens.ArmazensRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class ArmazensViewModel constructor(private val armazensRepository: ArmazensRepository) :
    ViewModel() {

    val mShowErrorUser = MutableLiveData<String>()
    val mShowSucess = MutableLiveData<List<ArmazensResponse>>()
    val mShowErrorSer = MutableLiveData<String>()
    private var TAG = "ArmazensViewModel----->"

    fun getArmazens() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = this@ArmazensViewModel.armazensRepository.getArmazens()
                if (request.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        request.body().let { token ->
                            mShowSucess.value = token
                            getResponseOk(token)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        val error = request.errorBody()!!.string()
                        val error2 = JSONObject(error).getString("message")
                        mShowErrorUser.value = error2
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mShowErrorSer.value = "Ops! Erro inesperado..."
                }
            }
        }
    }

    //RETORNA ARMAZENS -->
    fun getResponseOk(armazensResponse: List<ArmazensResponse>?) = armazensResponse

}
