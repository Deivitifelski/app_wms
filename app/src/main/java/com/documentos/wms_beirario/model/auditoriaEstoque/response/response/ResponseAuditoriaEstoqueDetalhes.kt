package com.documentos.wms_beirario.model.auditoriaEstoque.response.response

data class ResponseAuditoriaEstoqueDetalhes(
    val dataHoraUltimoApontamento: String,
    val enderecoVisualApontamento: String,
    val enderecoVisualSistema: Any,
    val idAuditoriaEstoqueItem: Any,
    val idEnderecoApontamento: Int,
    val idEnderecoSistema: Any,
    val idProduto: Int,
    val numeroContagem: Int,
    val numeroSerie: String,
    val quantidadeApontada: Int,
    val quantidadeApontamentosAtencao: Int,
    val quantidadeApontamentosErro: Int,
    val quantidadeAuditada: Int
)