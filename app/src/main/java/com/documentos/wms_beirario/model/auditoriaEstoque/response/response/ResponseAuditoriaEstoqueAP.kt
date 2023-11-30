package com.documentos.wms_beirario.model.auditoriaEstoque.response.response

data class ResponseAuditoriaEstoqueAP(
    val codigoGrade: String,
    val dataHoraUltimoApontamento: String? = null,
    val distribuicao: List<DistribuicaoAP>? = null,
    val idAuditoriaEStoque: String,
    val idEndereco: Int,
    val idProduto: Int,
    val numeroContagem: Any,
    val quantidadeApontada: Int? = null,
    val quantidadeApontamentosAtencao: Int,
    val quantidadeApontamentosErro: Int,
    val quantidadeAuditada: Int,
    val skuProduto: String,
    val tipoProduto: String
)