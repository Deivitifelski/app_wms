package com.documentos.wms_beirario.model.recebimentoRfid

data class ResponseDetailsEpc(
    val corCdgo: Int,
    val descricaoCor: String,
    val descricaoIesCodigo: String,
    val idItemTamanhoCor: Int,
    val idProduto: Int,
    val iesCodigo: Int,
    val nomeProduto: String,
    val numeroSerie: String,
    val quantidade: Int,
    val tamanho: String,
    val unidadeMedida: String
)