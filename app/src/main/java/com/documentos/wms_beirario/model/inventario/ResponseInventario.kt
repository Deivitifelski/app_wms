package com.documentos.wms_beirario.model.inventario

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class ResponseInventoryPending1(
    @SerializedName("id") val id: Int,
    @SerializedName("idArmazem") val idArmazem: Int,
    @SerializedName("documento") val documento: Int,
    @SerializedName("dataHora") val dataHora: String,
    @SerializedName("numeroContagem") val numeroContagem: Int,
    @SerializedName("solicitante") val solicitante: String,
    @SerializedName("situacao") val situacao: String,
    ) : Serializable

//------------------------------------------------>
@Parcelize
data class ResponseQrCode2(
    @SerializedName("result") val result: ProcessaLeituraResponseInventario2,
    @SerializedName("leituraEndereco") val leituraEndereco: List<LeituraEndInventario2List>
) : Parcelable

data class ProcessaLeituraResponseInventario2(
    @SerializedName("codigoBarras") val codigoBarras: String,
    @SerializedName("idEndereco") val idEndereco: Int,
    @SerializedName("enderecoVisual") val enderecoVisual: String,
    @SerializedName("idProduto") val idProduto: Int,
    @SerializedName("EAN") val EAN: Any,
    @SerializedName("sku") val sku: Any,
    @SerializedName("numeroSerie") val numeroSerie: Int,
    @SerializedName("layoutEtiqueta") val layoutEtiqueta: Any,
    @SerializedName("idInventarioAbastecimentoItem") val idInventarioAbastecimentoItem: String,
    @SerializedName("produtoPronto") val produtoPronto: String,
    @SerializedName("produtoVolume") val produtoVolume: Int,
) : Serializable

data class LeituraEndInventario2List(
    @SerializedName("codigoBarras") val codigoBarras: String,
    @SerializedName("criadoEm") val criadoEm: String,
    @SerializedName("nomeUsuario") val nomeUsuario: String,
    @SerializedName("sku") val sku: String
) : Serializable

//------------------------------------------------>