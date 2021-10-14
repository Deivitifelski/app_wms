package com.documentos.wms_beirario.model.armazens

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ArmazensResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("nome") val nome: String,
    @SerializedName("lerVolumeSeparacao") val lerVolumeSeparacao: String,
) : Serializable
