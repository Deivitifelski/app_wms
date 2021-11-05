package com.documentos.wms_beirario.ui.login

import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.model.login.LoginRequest

class LoginRepository(private val mRetrofitService: RetrofitService) {

    suspend fun postLogin(loginRequest : LoginRequest) = this.mRetrofitService.postLogin(loginRequest)

}