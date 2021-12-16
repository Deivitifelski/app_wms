package com.documentos.wms_beirario.repository.recebimento

import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode2
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode3
import com.documentos.wms_beirario.model.recebimento.request.PostReciptQrCode1

class ReceiptRepository(private val serviceApi: ServiceApi) {

    //01
    suspend fun receiptPost1(postDocumentoRequestRec1: PostReciptQrCode1) =
        this.serviceApi.receiptPost1(postDocumentoRequestRec1 = postDocumentoRequestRec1)

    //02
    suspend fun receiptPost2(idTarefa: String, postReceiptQrCode2: PostReceiptQrCode2) =
        this.serviceApi.receiptPointed2(
            idTarefa = idTarefa,
            postReceiptQrCode2 = postReceiptQrCode2
        )

    //03
    suspend fun receiptPost3(idTarefa: String, postReceiptQrCode3: PostReceiptQrCode3) =
        this.serviceApi.receipt3(idTarefa = idTarefa, postReceiptQrCode3 = postReceiptQrCode3)
}