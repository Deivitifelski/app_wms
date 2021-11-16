package com.documentos.wms_beirario.model.codBarras

import com.example.coletorwms.model.codBarras.Cod.EnderecoModel
import com.example.coletorwms.model.codBarras.CodBarrasProdutoResponseModel
import com.example.coletorwms.model.codBarras.VolumeModelCB
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CodigodeBarrasResponse(
    @SerializedName("volume") val volume: VolumeModelCB?,
    @SerializedName("endereco") val endereco: EnderecoModel?,
    @SerializedName("produto") val produto: CodBarrasProdutoResponseModel?,
) : Serializable
