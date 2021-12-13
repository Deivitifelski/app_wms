package com.documentos.wms_beirario.repository.login

import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.model.login.LoginRequest

class LoginRepository(private val mServiceApi: ServiceApi) {

    suspend fun postLogin(loginRequest: LoginRequest) = this.mServiceApi.postLogin(loginRequest)


}