package com.documentos.wms_beirario.ui.armazengem

import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.model.armazenagem.ArmazenagemResponse
import com.documentos.wms_beirario.repository.ArmazenagemRepository
import com.documentos.wms_beirario.ui.Tarefas.TipoTarefaRepository
import com.documentos.wms_beirario.ui.Tarefas.TipoTarefaViewModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArmazenagemViewModel constructor(private var repository: ArmazenagemRepository) :
    ViewModel() {


    var messageError = MutableLiveData<String>()
    var mSucess = MutableLiveData<List<ArmazenagemResponse>>()
    var mListVazia = MutableLiveData<Boolean>()


     fun getArmazenagem() {
        val request = repository.getArmazens()
        request.enqueue(object : Callback<List<ArmazenagemResponse>> {
            override fun onResponse(
                call: Call<List<ArmazenagemResponse>>,
                response: Response<List<ArmazenagemResponse>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { listArmazens ->
                        if (listArmazens.isEmpty()) {
                            mListVazia.postValue(true)
                        } else {
                            mListVazia.postValue(false)
                            mSucess.postValue(listArmazens)
                        }
                    }
                } else {
                    val error = response.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    messageError.postValue(error2)
                }
            }

            override fun onFailure(call: Call<List<ArmazenagemResponse>>, t: Throwable) {
                messageError.postValue(t.message)
            }
        })

    }

    fun visibilityProgress(mProgress: ProgressBar, visibility: Boolean) {
        if (visibility) mProgress.visibility = View.VISIBLE else mProgress.visibility =
            View.INVISIBLE
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