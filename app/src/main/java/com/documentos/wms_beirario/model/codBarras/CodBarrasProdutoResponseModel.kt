package com.example.coletorwms.model.codBarras

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CodBarrasProdutoResponseModel(
    @SerializedName("nome") val nome: String?,
    @SerializedName("tamanho") val tamanho: String?,
    @SerializedName("sku") val sku: String?,
    @SerializedName("quantidade") val quantidade: Int?,
    @SerializedName("localizacoes") val Produtolocalizacoes: List<Produtolocalizacoes>,
):Serializable

data class Produtolocalizacoes(
    @SerializedName("area") val area: String,
    @SerializedName("enderecoVisual") val enderecoVisual: String,
    @SerializedName("quantidade") val quantidade: Int,
): Serializable