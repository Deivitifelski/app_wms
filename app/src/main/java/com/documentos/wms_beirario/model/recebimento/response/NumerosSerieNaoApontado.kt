package com.documentos.wms_beirario.model.recebimento

data class NumerosSerieNaoApontado(
    val numeroSerie: String,
    val rotulo: String,
    val sequencial: Int
)

data class NumerosSerieApontados(
    val numeroSerie: String,
    val rotulo: String,
    val sequencial: Int
)

data class RecebimentoDocTrans(
    val empresa: String,
    val filial: String,
    val idTarefaConferencia: Any,
    val idTarefaRecebimento: String,
    val mensagem: String,
    val numeroNotaFiscal: Int,
    val numeroRequisicao: Any,
    val numerosSerieApontados: List<NumerosSerieApontados>,
    val numerosSerieNaoApontados: List<NumerosSerieNaoApontado>,
    val serieNotaFiscal: String
)