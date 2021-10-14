package com.documentos.wms_beirario.ui.armazens

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.documentos.wms_beirario.model.armazens.ArmazensResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ArmazensViewModel constructor(private val armazensRepository: ArmazensRepository) :
    ViewModel() {

    val mShowErrorUser = MutableLiveData<String>()
    val mShowSucess = MutableLiveData<List<ArmazensResponse>>()
    val mShowErrorSer = MutableLiveData<String>()

    fun getArmazens(token: String) {
        val request = this.armazensRepository.getArmazens(token = token)
        request.enqueue(object : Callback<List<ArmazensResponse>> {
            override fun onResponse(
                call: Call<List<ArmazensResponse>>,
                response: Response<List<ArmazensResponse>>
            ) {
                if (response.isSuccessful) {
                    response.body().let { token ->
                        mShowSucess.value = token
                        getResponseOk(token)
                    }
                } else {
                    val error = response.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    mShowErrorUser.value = error2
                }
            }

            override fun onFailure(call: Call<List<ArmazensResponse>>, t: Throwable) {
                mShowErrorSer.postValue("")
            }
        })
    }

    //RETORNA ARMAZENS -->
    fun getResponseOk(armazensResponse: List<ArmazensResponse>?) = armazensResponse
}