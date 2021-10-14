package com.documentos.wms_beirario.ui.Tarefas

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.documentos.wms_beirario.model.tipo_tarefa.TipoTarefaResponseItem
import org.json.JSONObject
import retrofit2.Callback
import retrofit2.Response

class TipoTarefaViewModel(private val mRepository: TipoTarefaRepository) : ViewModel() {

    val mResponseSucess = MutableLiveData<List<TipoTarefaResponseItem>>()
    val mResponseError = MutableLiveData<String>()

    fun getTarefas(mToken: String, mIdArmazem: Int) {
        val request = this.mRepository.getTarefas(id_armazem = mIdArmazem, token = mToken)
        request.enqueue(object : Callback<List<TipoTarefaResponseItem>> {
            override fun onResponse(
                call: retrofit2.Call<List<TipoTarefaResponseItem>>,
                response: Response<List<TipoTarefaResponseItem>>
            ) {
                if (response.isSuccessful) {
                    response.body().let {
                        mResponseSucess.value = it

                    }
                } else {
                    val error = response.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    mResponseError.value = error2
                }
            }

            override fun onFailure(
                call: retrofit2.Call<List<TipoTarefaResponseItem>>,
                t: Throwable
            ) {
                mResponseError.value = t.message
            }
        })
    }

}