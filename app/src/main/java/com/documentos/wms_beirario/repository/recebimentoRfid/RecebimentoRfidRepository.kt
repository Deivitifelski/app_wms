package com.documentos.wms_beirario.repository.recebimentoRfid

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.recebimentoRfid.BodyGetRecebimentoRfidTagsEpcs
import com.documentos.wms_beirario.model.recebimentoRfid.BodyRecbimentoRfidPostDetalhesEpc

class RecebimentoRfidRepository() {

    /**Busca as Nf pendentes */
    suspend fun buscaNfsPendentes(token: String, idArmazem: Int) =
        RetrofitClient().getClient()
            .getRecebimentoBuscaNfsPendentes(token = token, idArmazem = idArmazem)

    /**Retorna as tags relacionadas as NFs */
    suspend fun postTagsEpcs(idArmazem: Int, token: String, body: BodyGetRecebimentoRfidTagsEpcs) =
        RetrofitClient().getClient()
            .getRecebimentoRfidTagsEpcs(idArmazem = idArmazem, token = token, body = body)

    /**Retorna detalhes da TAG selecionada */
    suspend fun searchDetailEpc(
        idArmazem: Int,
        token: String?,
        body: BodyRecbimentoRfidPostDetalhesEpc
    ) =
        RetrofitClient().getClient()
            .postRecebimentoRfidDetalhesReturnDetalhesEpc(
                idArmazem = idArmazem,
                token = token!!,
                body = body
            )
}