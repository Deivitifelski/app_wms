package com.documentos.wms_beirario.model.receiptproduct

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable


data class ReceiptProduct1(
    @SerializedName("areaDestino") val areaDestino: String?,
    @SerializedName("normativaNumero") val normativaNumero: Any? = null,
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

@Parcelize
data class ReceiptIdOperador(
    @SerializedName("idOperadorColetor") val idOperadorColetor: Int,
    @SerializedName("minData") val minData: String,
    @SerializedName("usuario") val usuario: String
) : Parcelable

@Parcelize
data class ListReceiptIdOperador(
    val list : List<ReceiptIdOperador>
): Parcelable



