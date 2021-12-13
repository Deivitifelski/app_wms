package com.documentos.wms_beirario.repository.recebimento

import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.model.recebimento.request.RecRequestCodBarras

class ReceivementRepository(private val serviceApi: ServiceApi) {

    suspend fun receivementPost1(postDocumentoRequestRec1: RecRequestCodBarras) =
        this.serviceApi.Recebimento1(postDocumentoRequestRec1 = postDocumentoRequestRec1)
}