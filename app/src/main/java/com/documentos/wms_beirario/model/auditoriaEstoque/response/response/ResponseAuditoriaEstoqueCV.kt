package com.documentos.wms_beirario.model.auditoriaEstoque.response.response

data class ResponseAuditoriaEstoqueCV(
    val codigoGrade: String,
    val dataHoraUltimoApontamento: String? = null,
    val distribuicao: List<DistribuicaoCV>? = null,
    val idAuditoriaEStoque: Any,
    val idEndereco: Int,
    val idProduto: Int,
    val numeroContagem: Int? = null,
    val quantidadeApontada: Any,
    val quantidadeApontamentosAtencao: Any,
    val quantidadeApontamentosErro: Any,
    val quantidadeAuditada: Int,
    val skuProduto: String,
    val tipoProduto: String
)