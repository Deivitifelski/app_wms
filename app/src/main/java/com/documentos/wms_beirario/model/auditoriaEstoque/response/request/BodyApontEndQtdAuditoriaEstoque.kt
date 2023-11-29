package com.documentos.wms_beirario.model.auditoriaEstoque.response.request

data class BodyApontEndQtdAuditoriaEstoque(
    val quantidadePar: Int,
    val tipoProdutoPar: String,
    val quantidadeVol: Int,
    val tipoProdutoVol: String,
)