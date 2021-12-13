package com.documentos.wms_beirario.model.picking

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PickingRequest1(
    @SerializedName("numeroSerie") val numeroSerie: String
) : Serializable

class PickingRequest2(
    @SerializedName("idProduto") val idProduto: Int,
    @SerializedName("quantidade") val quantidade: Int,
    @SerializedName("enderecoLeitura") val enderecoLeitura: String,
) : Serializable