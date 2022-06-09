package com.documentos.wms_beirario.model.auditoria

data class ResponseAuditoria1(
    val dataAlteracao: String? = null,
    val dataInclusao: String,
    val id: Int,
    val idArmazem: Int,
    val situacao: String,
    val usuarioAlteracao: String? = null,
    val usuarioInclusao: String
)

data class ResponseAuditoriaEstantes2(
    val estante: String
)
