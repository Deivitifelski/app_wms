package com.documentos.wms_beirario.model.etiquetagem.response

import java.io.Serializable

data class EtiquetagemResponse2(
    val dataEmissao: String?,
    val empresa: String,
    val filial: String,
    val numeroNotaFiscal: Int,
    val serieNotaFiscal: String,
    val quantidadeVolumes: Int,
    val quantidadeVolumesPendentes: Int,
) : Serializable


data class EtiquetagemResponse3(
    val numeroPedido: Int,
    val quantidadePendente: Int,
    val quantidadeVolumes: Int
)