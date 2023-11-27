package com.documentos.wms_beirario.model.auditoriaEstoque.response.response

data class ResponseProdutoEnderecoAuditoriaEstoqueAp(
    val codigoGrade: String,
    val dataHoraUltimoApontamento: String,
    val idAuditoriaEStoque: String,
    val idEndereco: Int,
    val idProduto: Int,
    val listaQuantidade: String? = null,
    val listaTamanho: String? = null,
    val numeroContagem: Int,
    val quantidadeApontada: Int,
    val quantidadeApontamentosAtencao: Int,
    val quantidadeApontamentosErro: Int,
    val quantidadeAuditada: Int,
    val skuProduto: String,
    var tipoProduto: String
)


data class ResponseProdutoEnderecoAuditoriaEstoqueApCreate(
    val codigoGrade: String,
    val dataHoraUltimoApontamento: String,
    val idAuditoriaEStoque: String,
    val idEndereco: Int,
    val idProduto: Int,
    val listDist: DistribuicaoAp,
    val numeroContagem: Int,
    val quantidadeApontada: Int,
    val quantidadeApontamentosAtencao: Int,
    val quantidadeApontamentosErro: Int,
    val quantidadeAuditada: Int,
    val skuProduto: String,
    var tipoProduto: String
)

data class DistribuicaoAp(
    val listaQuantidade: List<String>? = null,
    val listaTamanho: List<String>? = null,
)