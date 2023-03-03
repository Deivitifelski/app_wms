package com.documentos.wms_beirario.repository.separacao

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.separation.*

class SeparacaoRepository() {
    //1 - busca andares ->
    suspend fun getBuscaAndaresSeparation() =
        RetrofitClient().getClient().getAndaresSeparation()

    //2 - BUSCA ESTANTES
    suspend fun posBuscaEstantesSeparation(
        separationItensCheck: RequestSeparationArraysAndares1
    ) = RetrofitClient().getClient()
        .postBuscaEstantesSeparation(bodyArrayAndarEstantes = separationItensCheck)

    //3 - BUSCA ENDEREÇOS -->
    suspend fun postBuscaEnderecosSeparation(
        bodySendArrays: RequestSeparationArraysAndaresEstante3
    ) = RetrofitClient().getClient()
        .postBuscaEnderecosSeparation(bodyArrayAndarEstantes = bodySendArrays)

    //4 - SEPARAR FINALIZAR SEPARAÇÃO -->
    suspend fun postSeparationEnd(
        separationEnd: SeparationEnd
    ) = RetrofitClient().getClient().postSeparationEnd(separationEnd = separationEnd)

    //5 - BUSCA PRODUTOS -->
    suspend fun getProdAndress(
        idEndereco: String
    ) = RetrofitClient().getClient().postBuscaProdutos(idEndereco = idEndereco)


    suspend fun postSepProdAndress(
        bodySeparationDefault4: BodySeparationDefault4
    ) = RetrofitClient().getClient()
        .postSepProdAndress(bodySeparationDefault4 = bodySeparationDefault4)

    //6 BETA - ETIQUETAR E SEPARAR -->
    suspend fun postSepararEtiquetar(
        bodySeparationEtiquetar: BodySepararEtiquetar,
        idEnderecoOrigem: String
    ) = RetrofitClient().getClient().postSepEtiquetarProdAndress(
        bodySepararEtiquetar = bodySeparationEtiquetar,
        idEnderecoOrigem = idEnderecoOrigem
    )
}
