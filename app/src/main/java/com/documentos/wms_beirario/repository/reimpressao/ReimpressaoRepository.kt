package com.documentos.wms_beirario.repository.reimpressao

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.logPrinter.BodySaveLogPrinter

class ReimpressaoRepository {

    suspend fun getReimpressaoNumRequest(numRequisicao: String) =
        RetrofitClient().getClient().reimpressaoPorNumRequisicao(numeroRequisicao = numRequisicao)

    suspend fun getReimpressaoNumSerie(numserie: String) =
        RetrofitClient().getClient().reimpressaoPorNumSerie(numeroSerie = numserie)

    suspend fun getReimpressaoNf(nfNumero: String, nfSerie: String) =
        RetrofitClient().getClient().reimpressaoPorNumNf(nfNumero = nfNumero, nfSerie = nfSerie)

    suspend fun getReimpressaoPedido(numeroPedido: String) =
        RetrofitClient().getClient().reimpressaoPorPedido(numeroPedido = numeroPedido)

    suspend fun getReimpressaoEtiquetas(idTarefa: String, sequencialTarefa: String) =
        RetrofitClient().getClient()
            .getEtiquetasReimpressao(idTarefa = idTarefa, sequencialTarefa = sequencialTarefa)

    suspend fun saveLogPrinterRepository(bodySaveLogPrinter: BodySaveLogPrinter) =
        RetrofitClient().getClient().saveLogPrinter(body = bodySaveLogPrinter)
}