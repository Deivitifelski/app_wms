package com.documentos.wms_beirario.repository.reimpressao

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.logPrinter.BodySaveLogPrinter
import com.documentos.wms_beirario.model.reimpressao.RequestEtiquetasReimpressaoBody

class ReimpressaoRepository {

    suspend fun getReimpressaoNumRequest(numRequisicao: String, idArmazem: Int, token: String) =
        RetrofitClient().getClient().reimpressaoPorNumRequisicao(
            numeroRequisicao = numRequisicao,
            token = token,
            idArmazem = idArmazem
        )

    suspend fun getReimpressaoNumSerie(numserie: String, idArmazem: Int, token: String) =
        RetrofitClient().getClient()
            .reimpressaoPorNumSerie(numeroSerie = numserie, token = token, idArmazem = idArmazem)

    suspend fun getReimpressaoNf(nfNumero: String, nfSerie: String, idArmazem: Int, token: String) =
        RetrofitClient().getClient().reimpressaoPorNumNf(
            nfNumero = nfNumero,
            nfSerie = nfSerie,
            token = token,
            idArmazem = idArmazem
        )

    suspend fun getReimpressaoPedido(numeroPedido: String, idArmazem: Int, token: String) =
        RetrofitClient().getClient()
            .reimpressaoPorPedido(numeroPedido = numeroPedido, token = token, idArmazem = idArmazem)

    suspend fun getReimpressaoEtiquetas(
        body: RequestEtiquetasReimpressaoBody,
        idArmazem: Int,
        token: String
    ) =
        RetrofitClient().getClient()
            .getEtiquetasReimpressao(body = body, token = token, idArmazem = idArmazem)

    suspend fun saveLogPrinterRepository(
        bodySaveLogPrinter: BodySaveLogPrinter,
        idArmazem: Int,
        token: String
    ) =
        RetrofitClient().getClient()
            .saveLogPrinter(body = bodySaveLogPrinter, token = token, idArmazem = idArmazem)
}