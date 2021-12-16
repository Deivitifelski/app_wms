package com.documentos.wms_beirario.model.recebimento

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NumberSerieNoPonted(
    @SerializedName("sequencial") val sequencial: Int = 0,
    @SerializedName("numeroSerie") val numeroSerie: String = "",
    @SerializedName("rotulo") val rotulo: String = "",
) :Serializable

data class NumberSeriePonted(
    val numeroSerie: String,
    val rotulo: String,
    val sequencial: Int
) :Serializable

data class ReceiptDoc1(
    val empresa: String,
    val filial: String,
    val idTarefaConferencia: String? = null,
    val idTarefaRecebimento: String? = null,
    val mensagem: String,
    val numeroNotaFiscal: Int,
    val numeroRequisicao: Any,
    val numerosSerieApontados: List<NumberSeriePonted>,
    val numerosSerieNaoApontados: List<NumberSerieNoPonted>,
    val serieNotaFiscal: String
) :Serializable


data class ReceiptMessageFinish(
    @SerializedName("mensagem") val mensagemFinal: String
) : Serializable
