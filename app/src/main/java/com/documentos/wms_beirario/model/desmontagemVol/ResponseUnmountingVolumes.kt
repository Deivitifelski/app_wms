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


class ResponseUnMountingFinish : ArrayList<ResponseUnMountingFinishItem>()

data class RequestDisassamblyVol(
    val idEndereco: Int,
    val numeroSerie: String
)

data class Distribuicao(
    val idProduto: Int,
    val quantidade: Int,
    val tamanho: String
)

data class ResponseUnMountingFinishItem(
    val distribuicao: List<Distribuicao>,
    val idProdutoKit: Int,
    val nome: String,
    val quantidadeVolumes: Int
)