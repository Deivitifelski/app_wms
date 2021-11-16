package com.example.coletorwms.model.codBarras.Cod

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EnderecoModel(
    @SerializedName("tipo") val tipo: String?,
    @SerializedName("nomeArea") val nomeArea: String?,
    @SerializedName("enderecoVisual") val enderecoVisual: String?,
    @SerializedName("armazem") val armazem: Int?,
    @SerializedName("volumes") val volumes: List<VolumesModel?>,
    @SerializedName("listaNumeroSerie") val listaNumeroSerie: List<ListaNumeroSerieModel?>,
    @SerializedName("ultimosMovimentos") val ultimosMovimentos: List<UltimosMovimentosModel?>,
    @SerializedName("produtos") val produtos: List<CodBarrasProdutoClick>
) : Serializable

data class VolumesModel(
    @SerializedName("nome") val nome: String?,
    @SerializedName("sku") val sku: String?,
    @SerializedName("codigoEmbalagem") val codigoEmbalagem: Int?,
    @SerializedName("descricaoEmbalagem") val descricaoEmbalagem: String?,
    @SerializedName("codigoDistribuicao") val codigoDistribuicao: Int?,
    @SerializedName("descricaoDistribuicao") val descricaoDistribuicao: String?,
    @SerializedName("quantidade") val quantidade: Int?,
    ) : Serializable

data class CodBarrasProdutoClick(
    @SerializedName("codigoMarca") val codigoMarca: Int,
    @SerializedName("descricaoMarca") val descricaoMarca: String,
    @SerializedName("ean") val ean: String,
    @SerializedName("nome") val nome: String,
    @SerializedName("quantidade") val quantidade: Int,
    @SerializedName("sku") val sku: String,
    @SerializedName("tamanho") val tamanho: String
) : Serializable


data class ListaNumeroSerieModel(
    @SerializedName("numeroSerie") val numeroSerie: String?,
) : Serializable


data class UltimosMovimentosModel(
    @SerializedName("data") val data: String?,
    @SerializedName("usuario") val usuario: String?,
    @SerializedName("numeroSerie") val numeroSerie: String?,
) : Serializable

