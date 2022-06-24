package com.documentos.wms_beirario.model.auditoria

data class BodyAuditoriaFinish(
    val idAuditoria: Int,
    val estante: String,
    val idEndereco: String,
    val volumeInformado: String
)

