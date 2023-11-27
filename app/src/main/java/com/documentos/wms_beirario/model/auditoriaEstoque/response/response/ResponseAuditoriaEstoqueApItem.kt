package com.documentos.wms_beirario.model.auditoriaEstoque.response.response

data class ResponseAuditoriaEstoqueAp(
    val codigoGrade: String,
    val dataHoraUltimoApontamento: String? = null,
    val idAuditoriaEStoque: String,
    val idEndereco: Int,
    val idProduto: Int,
    val listaQuantidade: List<String>,
    val listaTamanho: List<String>,
    val numeroContagem: Int? = null,
    val quantidadeApontada: Int,
    val quantidadeApontamentosAtencao: Int,
    val quantidadeApontamentosErro: Int,
    val quantidadeAuditada: Int,
    val skuProduto: String,
    val tipoProduto: String
)

data class ResponseAuditoriaEstoqueApAdapter(
    val codigoGrade: String,
    val dataHoraUltimoApontamento: String? = null,
    val idAuditoriaEStoque: String,
    val idEndereco: Int,
    val idProduto: Int,
    val distribuicaoAp: List<DistribuicaoAp>,
    val numeroContagem: Int? = null,
    val quantidadeApontada: Int,
    val quantidadeApontamentosAtencao: Int,
    val quantidadeApontamentosErro: Int,
    val quantidadeAuditada: Int,
    val skuProduto: String,
    val tipoProduto: String
)

data class DistribuicaoAp(
    val qtd: String? = null,
    val tam: String? = null,
)