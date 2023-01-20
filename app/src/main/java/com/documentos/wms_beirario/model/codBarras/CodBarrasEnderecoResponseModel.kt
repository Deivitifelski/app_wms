package com.documentos.wms_beirario.model.codBarras

import java.io.Serializable


data class EnderecoModel(
    val tipo: String?,
    val nomeArea: String?,
    val enderecoVisual: String?,
    val armazem: Int?,
    val volumes: List<VolumesModel?>,
    val listaNumeroSerie: List<ListaNumeroSerieModel?>,
    val ultimosMovimentos: List<UltimosMovimentosModel?>,
    val produtos: List<CodBarrasProdutoClick>
) : Serializable

data class VolumesModel(
    val nome: String?,
    val sku: String?,
    val codigoEmbalagem: Int?,
    val descricaoEmbalagem: String?,
    val codigoDistribuicao: Int?,
    val descricaoDistribuicao: String?,
    val quantidade: Int?,
    val listaNumeroSerie: List<NumSerieVolModel>
) : Serializable


data class NumSerieVolModel(
    val numeroSerie: String
) : Serializable


data class CodBarrasProdutoClick(
    val codigoMarca: Int,
    val descricaoMarca: String,
    val ean: String,
    val nome: String,
    val quantidade: Int,
    val sku: String,
    val tamanho: String
) : Serializable

data class ListaNumeroSerieModel(
    val numeroSerie: String?,
) : Serializable


data class UltimosMovimentosModel(
    val data: String?,
    val usuario: String?,
    val numeroSerie: String?,
) : Serializable

