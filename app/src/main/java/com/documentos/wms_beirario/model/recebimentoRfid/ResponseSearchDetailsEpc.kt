package com.documentos.wms_beirario.model.recebimentoRfid

data class ResponseSearchDetailsEpc(
    val corCdgo: Int,
    val descricaoCor: String,
    val descricaoIesCodigo: String,
    val filialDestino: String,
    val filialEmitente: String,
    val idItemTamanhoCor: Int,
    val idProduto: String,
    val iesCodigo: Int,
    val nomeProduto: String,
    val notaFiscal: String,
    val numeroSerie: String,
    val quantidade: Double,
    val tamanho: String,
    val unidadeMedida: String,
    val dataEmissao: String,

)