package com.documentos.wms_beirario.model.recebimentoRfid

data class RecebimentoRfidEpcResponse(
    val corCdgo: Int? = null,
    val descricaoCorCdgo: String? = null,
    val descricaoIesCodigo: String? = null,
    val idItemTamanhoCor: Int? = null,
    val iesCodigo: Int? = null,
    var numeroSerie: String,
    val quantidade: Int? = null,
    val tamanho: String? = null,
    var status: String? = "R",
)