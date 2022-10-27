package com.documentos.wms_beirario.repository.separacao

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.separation.*

class SeparacaoRepository() {
    //1
    suspend fun getItemsSeparation() =
        RetrofitClient().getClient().getAndaresSeparation()

    //2
    suspend fun postArrayAndaresSelect(
        separationItensCheck: RequestSeparationArraysAndares1
    ) = RetrofitClient().getClient()
        .postSendArrayAndares(bodyArrayAndarEstantes = separationItensCheck)

    //3

    suspend fun postArrayAndaresEstantes(
        bodySendArrays: RequestSeparationArraysAndaresEstante3
    ) = RetrofitClient().getClient()
        .postArrayAndaresEstantes(bodyArrayAndarEstantes = bodySendArrays)

    suspend fun postSeparationEnd(
        separationEnd: SeparationEnd
    ) = RetrofitClient().getClient().postSeparationEnd(separationEnd = separationEnd)

    //4
    suspend fun getProdAndress(
        idEnderecoOrigem: String
    ) = RetrofitClient().getClient()
        .getSeparaProdAndress(idEnderecoOrigem = idEnderecoOrigem)

    //5
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
