package com.documentos.wms_beirario.model.auditoria

data class BodyAuditoriaFinish(
    val estante: String,
    val idAuditoria: Int,
    val numSerie: String,
    val usuario: String
)
