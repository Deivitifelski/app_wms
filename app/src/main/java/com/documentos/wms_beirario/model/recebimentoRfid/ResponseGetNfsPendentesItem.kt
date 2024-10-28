package com.documentos.wms_beirario.model.recebimentoRfid

import java.io.Serializable

data class ResponseGetRecebimentoNfsPendentes(
    val filial: Int,
    val idArmazem: Int,
    val idDocumento: String,
    val nfDataEmissao: String,
    val nfNumero: Int,
    val nfSerie: Int,
    val quantidadeNumeroSerie: Int,
    val nfChaveAcesso: String,
) : Serializable