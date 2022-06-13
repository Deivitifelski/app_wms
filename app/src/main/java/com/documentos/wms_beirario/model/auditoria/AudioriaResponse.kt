package com.documentos.wms_beirario.model.auditoria

class ResponseAuditoria1 : ArrayList<ResponseAuditoria1Item>()

data class ResponseAuditoria1Item(
    val dataAlteracao: Any,
    val dataInclusao: String,
    val id: Int,
    val idArmazem: Int,
    val situacao: String,
    val usuarioAlteracao: Any,
    val usuarioInclusao: String
)

data class ResponseAuditoriaEstantes2(
    val estante: String
)


class ResponseAuditoria3 : ArrayList<ResponseAuditoriaItem3>()

data class ResponseAuditoriaItem3(
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