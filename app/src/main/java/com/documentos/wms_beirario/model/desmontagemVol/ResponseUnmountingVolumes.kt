package com.documentos.wms_beirario.model.desmontagemVol

import com.google.gson.annotations.SerializedName
import java.io.Serializable


/** response 1 ->*/
class UnmountingVolumes1 : ArrayList<UnmountingVolumes1Item>()

data class UnmountingVolumes1Item(
    @SerializedName("enderecoVisual") val enderecoVisual: String,
    @SerializedName("idArea") val idArea: Int,
    @SerializedName("idEndereco") val idEndereco: Int,
    @SerializedName("quantidadeVolumes") val quantidadeVolumes: Int,
    @SerializedName("siglaArea") val siglaArea: String
) : Serializable


class ResponseUnmonting2 : ArrayList<ResponseUnmonting2Item>()

data class ResponseUnmonting2Item(
    val idProduto: Int,
    val nome: String,
    val quantidadeVolumes: Int
) : Serializable