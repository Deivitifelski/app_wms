package com.documentos.wms_beirario.repository.recebimento

import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.model.recebimento.request.RecRequestCodBarras

class ReceivementRepository(private val retrofitService: RetrofitService) {

    suspend fun receivementPost1(postDocumentoRequestRec1: RecRequestCodBarras) =
        this.retrofitService.Recebimento1(postDocumentoRequestRec1 = postDocumentoRequestRec1)
}