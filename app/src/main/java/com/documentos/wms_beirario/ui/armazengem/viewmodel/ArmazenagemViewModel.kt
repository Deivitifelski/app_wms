package com.documentos.wms_beirario.ui.armazengem.viewmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.armazenagem.ArmazemRequestFinish
import com.documentos.wms_beirario.model.armazenagem.ArmazenagemResponse
import com.documentos.wms_beirario.repository.armazenagem.ArmazenagemRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class ArmazenagemViewModel constructor(private var repository: ArmazenagemRepository) :
    ViewModel() {


    var messageError = MutableLiveData<String>()
    var mSucess = MutableLiveData<List<ArmazenagemResponse>>()
    var mValidProgress = MutableLiveData<Boolean>()

    private var mSucessFinish = MutableLiveData<Unit>()
    val mSucessFinishshow: LiveData<Unit>
        get() = mSucessFinish


    /**
     * ARMAZENAGEM -> GET (Retornar tarefas de armazenamento pendentes)
     */
    fun getArmazenagem() {
        viewModelScope.launch {
            try {
                val request = repository.getArmazens()
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
                messageError.postValue("Ops! Erro inesperado...")
            }
        }
    }

    fun postFinish(armazemRequestFinish: ArmazemRequestFinish) {
        viewModelScope.launch {
            try {
                val request =
                    this@ArmazenagemViewModel.repository.finishArmazenagem(armazemRequestFinish = armazemRequestFinish)
                if (request.isSuccessful) {
                    request.let { response ->
                        mSucessFinish.postValue(response.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val errorEdit = error2.replace("NAO", "NÃO")
                    messageError.postValue(errorEdit)
                }
            } catch (e: Exception) {
                messageError.postValue("Ops!Erro inesperado...")
            } catch (http: HttpException) {
                messageError.postValue("Verifique sua internet!")
            }
        }
    }
}


