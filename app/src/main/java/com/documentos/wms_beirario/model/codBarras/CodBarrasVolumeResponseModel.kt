package com.documentos.wms_beirario.model.codBarras

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class VolumeModelCB(

    @SerializedName("numeroSerie") val numeroSerie: String?,
    @SerializedName("armazemCriacao") val armazemCriacao: Int?,
    @SerializedName("dataCriacao") val dataCriacao: String?,
    @SerializedName("armazem") val armazem: Int?,
    @SerializedName("codigoEmbalagem") val codigoEmbalagem: Int?,
    @SerializedName("descricaoEmbalagem") val descricaoEmbalagem: String?,
    @SerializedName("codigoDistribuicao") val codigoDistribuicao: Int?,
    @SerializedName("descricaoDistribuicao") val descricaoDistribuicao: String?,
    @SerializedName("enderecoVisual") val enderecoVisual: String?,
    @SerializedName("nomeArea") val nomeArea: String?,
    @SerializedName("distribuicao") val distribuicao: List<DistribuicaoModel?>,
) : Serializable

data class DistribuicaoModel(
    @SerializedName("ean") val ean: String?,
    @SerializedName("sku") val sku: String?,
    @SerializedName("tamanho") val tamanho: String?, //ou INT?
    @SerializedName("quantidade") val quantidade: Int?,
) : Serializable


