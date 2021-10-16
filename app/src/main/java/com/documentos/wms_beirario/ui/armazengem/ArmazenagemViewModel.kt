package com.documentos.wms_beirario.ui.armazengem

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.documentos.wms_beirario.model.armazenagem.ArmazenagemResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArmazenagemViewModel constructor(private var repository: ArmazenagemRepository) :
    ViewModel() {


    var messageError = MutableLiveData<String>()
    var mSucess = MutableLiveData<List<ArmazenagemResponse>>()


    fun getArmazenagem(token: String, id_armazem: Int) {
        val request = repository.getArmazens(id_armazem, token)
        request.enqueue(object : Callback<List<ArmazenagemResponse>>{
            override fun onResponse(
                call: Call<List<ArmazenagemResponse>>,
                response: Response<List<ArmazenagemResponse>>
            ) {
                if (response.isSuccessful){
                    response.body()?.let { listArmazens ->
                        mSucess.postValue(listArmazens)
                    }
                }else{
                    messageError.postValue(response.message())
                }
            }

            override fun onFailure(call: Call<List<ArmazenagemResponse>>, t: Throwable) {
              messageError.postValue(t.message)
            }
        })

    }

}