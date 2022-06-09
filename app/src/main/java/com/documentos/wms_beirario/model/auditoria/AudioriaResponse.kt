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

data class ResponseAuditoria3(
    val andar: String,
    val box: String,
    val enderecoVisual: String,
    val estante: String,
    val id: Int,
    val idEndereco: Int,
    val idTarefa: String,
    val numeroSerie: String,
    val quantidade: Int,
    val quantidadeApontada: Int,
    val sequencial: Int,
    val sku: String
)