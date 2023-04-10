package com.documentos.wms_beirario.model.picking

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PickingResponseNewFluxo(
    val idArea: Int,
    val nomeArea: String,
    val siglaArea: String,
    val quantidade: Int,
) : Serializable


data class PickingResponse2(
    val pedido: String,
    val enderecoVisualOrigem: String,
    val numeroSerie: String,
) : Serializable


data class PickingResponseTest2(
    val pedido: String,
    val enderecoVisualOrigem: String,
    val list: List<PickingResponseTestList2>
) : Serializable

data class PickingResponseTestList2(
    val numeroSerie: String,
) : Serializable


data class PickingResponse3(
    @SerializedName("descricaoDistribuicao") val descricaoDistribuicao: String,
    @SerializedName("descricaoEmbalagem") val descricaoEmbalagem: String,
    @SerializedName("sku") val sku: String,
    @SerializedName("idProduto") val idProduto: Int,
    @SerializedName("quantidade") val quantidade: Int
) : Serializable

class ResponsePickingReturnGrouped : ArrayList<ResponsePickingReturnGroupedItem>()

data class ResponsePickingReturnGroupedItem(
    val codigoDistribuicao: String,
    val codigoEmbalagem: String,
    val descricaoDistribuicao: String,
    val descricaoEmbalagem: String,
    val idProduto: Int,
    val nomeProduto: String,
    val quantidade: Int,
    val sku: String
) : Serializable

data class PickingResponseModel1(
    @SerializedName("idArea") val idArea: Int,
    @SerializedName("nomeArea") val nomeArea: String,
    @SerializedName("siglaArea") val siglaArea: String,
    @SerializedName("quantidade") val quantidade: Int,
) : Serializable

