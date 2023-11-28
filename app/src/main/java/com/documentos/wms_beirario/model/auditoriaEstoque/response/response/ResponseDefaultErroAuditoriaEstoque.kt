package com.documentos.wms_beirario.model.auditoriaEstoque.response.response

data class ResponseDefaultErroAuditoriaEstoque(
    val codigoErro: String,
    val erro: String,
    val mensagemErro: String
)