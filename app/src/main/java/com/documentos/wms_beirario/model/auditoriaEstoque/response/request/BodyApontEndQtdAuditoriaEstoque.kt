package com.documentos.wms_beirario.model.auditoriaEstoque.response.request

data class BodyApontEndQtdAuditoriaEstoque(
    val quantidade: Int,
    val tipoProduto: String
)