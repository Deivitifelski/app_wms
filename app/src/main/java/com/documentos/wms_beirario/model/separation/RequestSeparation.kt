package com.documentos.wms_beirario.model.separation

import java.io.Serializable

data class BodySeparationDefault4(
    val codigoBarras: String,
    val idEnderecoOrigem: Int
)

data class BodySepararEtiquetar(
    val numeroSerie: String,
)

data class RequestSeparationArraysAndares1(
    var andares: List<String>,
) : Serializable

data class RequestSeparationArraysAndaresEstante3(
    val andares: List<String>,
    val estantes: List<String>,
) : Serializable

data class RequestBuscaProdSeparation(
    val idProduto: Int
) : Serializable