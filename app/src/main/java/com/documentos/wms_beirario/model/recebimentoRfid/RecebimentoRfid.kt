package com.documentos.wms_beirario.model.recebimentoRfid

data class RecebimentoRfid(
    val nf: String,
    val filial: String,
    val remessa: String,
    val qtdEtiquetas: Int,
    var conferida: Boolean = false,
)
