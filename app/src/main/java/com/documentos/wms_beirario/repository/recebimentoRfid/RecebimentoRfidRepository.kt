package com.documentos.wms_beirario.repository.recebimentoRfid

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.recebimentoRfid.BodyGetRecebimentoRfidTagsEpcs

class RecebimentoRfidRepository() {

    suspend fun buscaNfsPendentes(token: String, idArmazem: Int) =
        RetrofitClient().getClient()
            .getRecebimentoBuscaNfsPendentes(token = token, idArmazem = idArmazem)

   suspend fun postTagsEpcs(idArmazem: Int, token: String, body: BodyGetRecebimentoRfidTagsEpcs) =
        RetrofitClient().getClient()
            .getRecebimentoRfidTagsEpcs(idArmazem = idArmazem, token = token, body = body)
}