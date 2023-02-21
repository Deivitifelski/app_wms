package com.documentos.wms_beirario.model.qualityControl

data class ResponseQualityResponse1(
    var apontados: List<Apontado>,
    var aprovados: List<Aprovado>,
    var detalhes: Detalhes,
    var naoApontados: List<NaoApontado>,
    var naoAprovados: List<NaoAprovado>
)

data class Detalhes(
    var documentoTarefa: Long,
    var idArmazem: Int,
    var idTarefa: String,
    var quantidadePares: Int,
    var quantidadeParesApontados: Int,
    var quantidadeParesNaoApontados: Int,
    var situacao: String
)

data class Aprovado(
    var dataHoraAlteracao: String,
    var idEnderecoOrigem: Int,
    var quantidade: Int,
    var sequencial: Int,
    var situacao: String,
    var sku: String,
    var usuarioAlteracao: String
)

data class Apontado(
    var dataHoraAlteracao: String,
    var idEnderecoOrigem: Int,
    var quantidade: Int,
    var sequencial: Int,
    var situacao: String,
    var sku: String,
    var usuarioAlteracao: String
)

data class NaoAprovado(
    var dataHoraAlteracao: String,
    var idEnderecoOrigem: Int,
    var quantidade: Int,
    var sequencial: Int,
    var situacao: String,
    var sku: String,
    var usuarioAlteracao: String
)

data class NaoApontado(
    var dataHoraAlteracao: String,
    var idEnderecoOrigem: Int,
    var quantidade: Int,
    var sequencial: Int,
    var situacao: String,
    var sku: String,
    var usuarioAlteracao: Any
)