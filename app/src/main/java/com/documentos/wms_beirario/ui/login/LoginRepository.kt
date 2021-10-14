package com.documentos.wms_beirario.ui.login

import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.model1.login.LoginRequest

class LoginRepository(private val mRetrofitService: RetrofitService) {

    fun postLogin(loginRequest : LoginRequest) = this.mRetrofitService.postLogin(loginRequest)

}