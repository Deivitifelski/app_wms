package com.documentos.wms_beirario.model.recebimentoRfid

data class RecebimentoRfidEpcResponse(
    val corCdgo: Int,
    val descricaoCorCdgo: String,
    val descricaoIesCodigo: String,
    val idItemTamanhoCor: Int,
    val iesCodigo: Int,
    var numeroSerie: String,
    val quantidade: Int,
    val tamanho: String,
    var status: String? = "R",
)