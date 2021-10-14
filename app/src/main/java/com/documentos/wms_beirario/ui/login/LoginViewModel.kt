package com.documentos.wms_beirario.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.documentos.wms_beirario.model.login.LoginRequest
import com.documentos.wms_beirario.model.login.LoginResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel constructor(private val repository: LoginRepository) : ViewModel() {

    val mLoginSucess = MutableLiveData<String>()
    val mLoginErrorUser = MutableLiveData<String>()
    val mLoginErrorServ = MutableLiveData<String>()
    val mValidaLogin = MutableLiveData<Boolean>()


    fun registerUser(usuario: String, senha: String) {
        if (usuario.isEmpty() || usuario.isBlank()) {
            mValidaLogin.postValue(true)
        } else if (senha.isEmpty() || senha.isBlank()) {
            mValidaLogin.postValue(true)
        } else {
            val request = this.repository.postLogin(LoginRequest(usuario, senha))
            request.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body().let { response ->
                            mLoginSucess.postValue(response!!.token)
                        }
                    } else {
                        val error = response.errorBody()!!.string()
                        val error2 = JSONObject(error).getString("message")
                        mLoginErrorUser.postValue(error2)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    mLoginErrorServ.postValue(t.message)
                }
            })
        }
    }

    fun getToken(usuario: String, senha: String): String{
        registerUser(usuario, senha)
        return mLoginSucess.value.toString()
    }


}