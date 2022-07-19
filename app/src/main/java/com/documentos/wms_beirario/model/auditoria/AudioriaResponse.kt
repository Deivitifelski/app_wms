package com.documentos.wms_beirario.model.auditoria

import java.io.Serializable

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

class ResponseFinishAuditoria : ArrayList<ResponseFinishAuditoriaItem>()

data class Distribuicao(
    val idItem: Int,
    val quantidade: Int,
    val tamanho: String
)

data class ResponseFinishAuditoriaItem(
    val andar: String,
    val auditado: Boolean,
    val box: String,
    val codBarrasEndereco: String,
    val codigoGrade: String,
    val distribuicao: List<Distribuicao>,
    val enderecoVisual: String,
    val estante: String,
    val id: Int,
    val idEndereco: Int,
    val idTarefa: String,
    var quantidade: Int,
    val quantidadeApontada: Int,
    val sequencial: Int,
    val sku: String
) : Serializable