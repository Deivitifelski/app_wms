package com.documentos.wms_beirario.repository.separacao

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.separation.*
import com.documentos.wms_beirario.model.separation.filtros.BodyAndaresFiltro
import com.documentos.wms_beirario.model.separation.filtros.BodyEnderecosFiltro
import com.documentos.wms_beirario.model.separation.filtros.BodyEstantesFiltro
import com.documentos.wms_beirario.model.separation.filtros.BodyProdutoSeparacao

class SeparacaoRepository() {
    //1 - busca andares ->
    suspend fun getBuscaAndaresSeparation(ideArmazem: Int, token: String) =
        RetrofitClient().getClient().getAndaresSeparation(ideArmazem, token)

    //2 - BUSCA ESTANTES
    suspend fun posBuscaEstantesSeparation(
        body: BodyEstantesFiltro,
        ideArmazem: Int,
        token: String
    ) = RetrofitClient().getClient()
        .postBuscaEstantesSeparation(
            body = body,
            idarmazem = ideArmazem,
            token = token
        )

    //3 - BUSCA ENDEREÇOS -->
    suspend fun postBuscaEnderecosSeparation(
        body: BodyEnderecosFiltro,
        idArmazem: Int?,
        token: String
    ) = RetrofitClient().getClient()
        .postBuscaEnderecosSeparation(
            body = body,
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
        body: BodyProdutoSeparacao,
        idArmazem: Int,
        token: String
    ) = RetrofitClient().getClient().postBuscaProdutosSeparation(
        body = body,
        idarmazem = idArmazem,
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
