package com.documentos.wms_beirario.repository.separacao

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.separation.*
import com.documentos.wms_beirario.model.separation.filtros.BodyAndaresFiltro

class SeparacaoRepository() {
    //1 - busca andares ->
    suspend fun getBuscaAndaresSeparation(ideArmazem: Int, token: String) =
        RetrofitClient().getClient().getAndaresSeparation(ideArmazem, token)

    //2 - BUSCA ESTANTES
    suspend fun posBuscaEstantesSeparation(
        separationItensCheck: RequestSeparationArraysAndares1,
        ideArmazem: Int,
        token: String
    ) = RetrofitClient().getClient()
        .postBuscaEstantesSeparation(
            bodyArrayAndarEstantes = separationItensCheck,
            idarmazem = ideArmazem,
            token = token
        )

    //3 - BUSCA ENDEREÇOS -->
    suspend fun postBuscaEnderecosSeparation(
        bodySendArrays: RequestSeparationArraysAndaresEstante3,
        idArmazem: Int?,
        token: String
    ) = RetrofitClient().getClient()
        .postBuscaEnderecosSeparation(
            bodyArrayAndarEstantes = bodySendArrays,
            idarmazem = idArmazem!!,
            token = token
        )

    //4 - SEPARAR FINALIZAR SEPARAÇÃO -->
    suspend fun postSeparationEnd(
        separationEnd: SeparationEnd,
        idArmazem: Int,
        token: String
    ) = RetrofitClient().getClient()
        .postSeparationEnd(separationEnd = separationEnd, idArmazem = idArmazem, token = token)

    //5 - BUSCA PRODUTOS -->
    suspend fun getProdAndress(
        codBarrasEndOrigem: String,
        idArmazem: Int,
        token: String
    ) = RetrofitClient().getClient().postBuscaProdutos(
        codBarrasEndOrigem = codBarrasEndOrigem,
        idArmazem = idArmazem,
        token = token
    )

    //6 BETA - ETIQUETAR E SEPARAR -->
    suspend fun postSepararEtiquetar(
        bodySeparationEtiquetar: BodySepararEtiquetar,
        idEnderecoOrigem: Int,
        idArmazem: Int,
        token: String
    ) = RetrofitClient().getClient().postSepEtiquetarProdAndress(
        bodySepararEtiquetar = bodySeparationEtiquetar,
        idEnderecoOrigem = idEnderecoOrigem,
        idArmazem = idArmazem,
        token = token
    )

    suspend fun getListFiles(token: String, idArmazem: Int) =
        RetrofitClient().getClient().getDocumentosSeparacoes(
            idArmazem = idArmazem,
            token = token
        )

    suspend fun getListTrans(token: String, idArmazem: Int) =
        RetrofitClient().getClient().getTransportadorasSeparacoes(
            idArmazem = idArmazem,
            token = token
        )

    suspend fun getAndaresFiltro(
        token: String,
        idArmazem: Int,
        body: BodyAndaresFiltro
    ) =
        RetrofitClient().getClient().getAndaresFiltro(
            token = token,
            idArmazem = idArmazem,
            body = body
        )
}
