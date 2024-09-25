package com.documentos.wms_beirario.model.recebimentoRfid

data class LeituraRfidEpc(
    val tag: String,
    val descricao: String,
    var tipoLeitura: String,
)
