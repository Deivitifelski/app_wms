package com.documentos.wms_beirario.model.auditoriaEstoque.response.response

data class ResponseAuditoriaEstoqueDetalhes(
    val dataHoraUltimoApontamento: String? = null,
    val enderecoVisualApontamento: String? = null,
    val enderecoVisualSistema: String? = null,
    val idAuditoriaEstoqueItem: Any,
    val idEnderecoApontamento: Int,
    val idEnderecoSistema: Any,
    val idProduto: Int,
    val numeroContagem: Int,
    val numeroSerie: String? = null,
    val quantidadeApontada: Int,
    val quantidadeApontamentosAtencao: Int,
    val quantidadeApontamentosErro: Int,
    val quantidadeAuditada: Int
)