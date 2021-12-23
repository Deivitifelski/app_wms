package com.documentos.wms_beirario.model.receiptproduct

import com.google.gson.annotations.SerializedName
import java.io.Serializable


    data class ReceiptProduct1(
        @SerializedName("areaDestino") val areaDestino: String,
        @SerializedName("normativaNumero") val normativaNumero: Any,
        @SerializedName("pedido") val pedido: String,
        @SerializedName("pedidoProgramado") val pedidoProgramado: String,
        @SerializedName("quantidadeVolumes") val quantidadeVolumes: Int,
    ) : Serializable


data class ReceiptProduct2(
    @SerializedName("idTarefa") val idTarefa: String,
    @SerializedName("sequencial") val sequencial: Int,
    @SerializedName("idProduto") val idProduto: Int,
    @SerializedName("endereidTarefacoVisualOrigem") val endereidTarefacoVisualOrigem: String,
    @SerializedName("numeroSerie") val numeroSerie: String,
    @SerializedName("pedido") val pedido: String,
    @SerializedName("pedidoProgramado") val pedidoProgramado: String,
    @SerializedName("sku") val sku: String,
    @SerializedName("idEnderecoDestino") val idEnderecoDestino: Any,
    @SerializedName("idEnderecoOrigem") val idEnderecoOrigem: Int,
) : Serializable

