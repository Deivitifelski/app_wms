package com.documentos.wms_beirario.model.auditoriaEstoque.response.response

data class ResponseProdutoEnderecoAuditoriaEstoqueAp(
    val codigoGrade: String,
    val dataHoraUltimoApontamento: String,
    val idAuditoriaEStoque: String,
    val idEndereco: Int,
    val idProduto: Int,
    val listaQuantidade: String,
    val listaTamanho: String,
    val numeroContagem: Int,
    val quantidadeApontada: Int,
    val quantidadeApontamentosAtencao: Int,
    val quantidadeApontamentosErro: Int,
    val quantidadeAuditada: Int,
    val skuProduto: String,
    val tipoProduto: String
)