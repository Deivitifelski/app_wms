package com.documentos.wms_beirario.model.recebimentoRfid

data class RecebimentoRfidEpcResponse(
    val corCdgo: Int? = null,
    val descricaoCorCdgo: String? = "-",
    val descricaoIesCodigo: String? = "-",
    val idItemTamanhoCor: Int? = null,
    val iesCodigo: Int? = null,
    var numeroSerie: String,
    val quantidade: Int? = null,
    val tamanho: String? = "-",
    var status: String? = "R",
)