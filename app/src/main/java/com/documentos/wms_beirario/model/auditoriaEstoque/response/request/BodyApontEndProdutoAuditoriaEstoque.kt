package com.documentos.wms_beirario.model.auditoriaEstoque.response.request

data class BodyApontEndProdutoAuditoriaEstoque(
    val codigoBarras: String,
    val forcarApontamento: String
)