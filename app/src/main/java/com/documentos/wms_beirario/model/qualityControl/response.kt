package com.documentos.wms_beirario.model.qualityControl

import java.io.Serializable

data class ResponseControlQuality1(
    var apontados: List<Apontado>,
    var aprovados: List<Aprovado>,
    var documentoTarefa: Long,
    var idArmazem: Int,
    var idTarefa: String,
    var naoApontados: List<NaoApontado>,
    var quantidadePares: Int,
    var quantidadeParesApontados: Int,
    var quantidadeParesNaoApontados: Int,
    var rejeitados: List<Rejeitado>,
    var situacao: String
) : Serializable

data class Rejeitado(
    var dataHoraAlteracao: String,
    var idEnderecoOrigem: Int,
    var quantidade: Int,
    var sequencial: Int,
    var situacao: String,
    var sku: String,
    var usuarioAlteracao: String
) : Serializable

data class NaoApontado(
    var dataHoraAlteracao: Any,
    var idEnderecoOrigem: Int,
    var quantidade: Int,
    var sequencial: Int,
    var situacao: String,
    var sku: String,
    var usuarioAlteracao: Any
) : Serializable

data class Aprovado(
    var dataHoraAlteracao: String,
    var idEnderecoOrigem: Int,
    var quantidade: Int,
    var sequencial: Int,
    var situacao: String,
    var sku: String,
    var usuarioAlteracao: String
) : Serializable


data class Apontado(
    var dataHoraAlteracao: String,
    var idEnderecoOrigem: Int,
    var quantidade: Int,
    var sequencial: Int,
    var situacao: String,
    var sku: String,
    var usuarioAlteracao: String
) : Serializable

//Response gera requisição
data class ResponseGenerateRequestControlQuality(
    var numeroRequisicao: Int
)