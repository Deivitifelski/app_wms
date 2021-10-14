package com.documentos.wms_beirario.model.login

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginRequest(
    @SerializedName("usuario") val usuario: String,
    @SerializedName("senha") val senha: String
) : Serializable