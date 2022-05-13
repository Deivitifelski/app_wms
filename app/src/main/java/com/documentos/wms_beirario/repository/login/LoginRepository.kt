package com.documentos.wms_beirario.repository.login

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.login.LoginRequest

class LoginRepository() {
    private fun getClientRetrofit() = RetrofitClient().getClient()
    suspend fun postLogin(loginRequest: LoginRequest) =
        getClientRetrofit().postLogin(loginRequest = loginRequest)


}