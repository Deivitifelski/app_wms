package com.documentos.wms_beirario.model.auditoriaEstoque.response.response

import java.io.Serializable

data class ListaAuditoriasItem(
    val data: String,
    val id: String,
    val numero: Int,
    val situacao: String,
    val solicitante: String,
    val tipo: String,
    val tipoDescricao: String
):Serializable