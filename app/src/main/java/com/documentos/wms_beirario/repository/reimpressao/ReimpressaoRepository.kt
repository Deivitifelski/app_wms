package com.documentos.wms_beirario.repository.reimpressao

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.logPrinter.BodySaveLogPrinter
import com.documentos.wms_beirario.model.reimpressao.RequestEtiquetasReimpressaoBody

class ReimpressaoRepository {

    suspend fun getReimpressaoNumRequest(numRequisicao: String) =
        RetrofitClient().getClient().reimpressaoPorNumRequisicao(numeroRequisicao = numRequisicao)

    suspend fun getReimpressaoNumSerie(numserie: String) =
        RetrofitClient().getClient().reimpressaoPorNumSerie(numeroSerie = numserie)

    suspend fun getReimpressaoNf(nfNumero: String, nfSerie: String) =
        RetrofitClient().getClient().reimpressaoPorNumNf(nfNumero = nfNumero, nfSerie = nfSerie)

    suspend fun getReimpressaoPedido(numeroPedido: String) =
        RetrofitClient().getClient().reimpressaoPorPedido(numeroPedido = numeroPedido)

    suspend fun getReimpressaoEtiquetas(body: RequestEtiquetasReimpressaoBody) =
        RetrofitClient().getClient()
            .getEtiquetasReimpressao(body = body)

    suspend fun saveLogPrinterRepository(bodySaveLogPrinter: BodySaveLogPrinter) =
        RetrofitClient().getClient().saveLogPrinter(body = bodySaveLogPrinter)
}