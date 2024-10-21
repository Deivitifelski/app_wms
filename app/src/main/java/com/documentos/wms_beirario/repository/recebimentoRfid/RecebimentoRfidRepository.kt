package com.documentos.wms_beirario.repository.recebimentoRfid

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.conferenceBoarding.BodyChaveBoarding

class RecebimentoRfidRepository() {

    suspend fun buscaNfsPendentes(idArmazem: Int) =
        RetrofitClient().getClient()
            .getRecebimentoBuscaNfsPendentes(idArmazem = idArmazem)
}