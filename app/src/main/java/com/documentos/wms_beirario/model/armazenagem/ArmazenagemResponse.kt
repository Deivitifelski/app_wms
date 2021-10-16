package com.documentos.wms_beirario.model.armazenagem

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ArmazenagemResponse(
    @SerializedName("id") val id: String,
    @SerializedName("idAreaOrigem") val idAreaOrigem: Int,
    @SerializedName("idEnderecoOrigem") val idEnderecoOrigem: Int,
    @SerializedName("codigoBarrasEnderecoOrigem") val codigoBarrasEnderecoOrigem: String,
    @SerializedName("enstanteEnderecoOrigem") val enstanteEnderecoOrigem: String,
    @SerializedName("visualEnderecoOrigem") val visualEnderecoOrigem: String,
    @SerializedName("idAreaDestino") val idAreaDestino: Int,
    @SerializedName("idEnderecoDestino") val idEnderecoDestino: Int,
    @SerializedName("codigoBarrasEnderecoDestino") val codigoBarrasEnderecoDestino: String,
    @SerializedName("enstanteEnderecoDestino") val enstanteEnderecoDestino: Int,
    @SerializedName("visualEnderecoDestino") val visualEnderecoDestino: String,
    @SerializedName("quantidade") val quantidade: Int
) : Serializable

