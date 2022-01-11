package com.documentos.wms_beirario.repository.receiptproduct

import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.model.receiptproduct.PosLoginValidadREceipPorduct
import com.documentos.wms_beirario.model.receiptproduct.PostFinishReceiptProduct3
import com.documentos.wms_beirario.model.receiptproduct.QrCodeReceipt1

class ReceiptProductRepository(val service: ServiceApi) {

    suspend fun getReceiptProduct1(
        filtrarOperador: Boolean,
        idOperador: String
    ) = service.getReceiptProduct1(filtrarOperador = filtrarOperador, idOperador = idOperador)

    suspend fun postReceiptProduct1(codBarras: QrCodeReceipt1) =
        service.postReadingReceiptProduct2(qrCode = codBarras)

    suspend fun getItemProduct2(idOperador: String, filtrarOperador: Boolean, pedido: String) =
        service.getReceiptProduct3(
            idOperador = idOperador,
            filtrarOperador = filtrarOperador,
            pedido = pedido
        )

    suspend fun validadAcessReceiptProduct(postLoginValidadREceipPorduct: PosLoginValidadREceipPorduct) =
        service.postValidAccesReceiptProduct(posLoginValidadREceipPorduct = postLoginValidadREceipPorduct)

    suspend fun postFinishReceiptProduct(postFinish: PostFinishReceiptProduct3) =
        service.postFinishReceiptProduct(postFinishReceiptProduct3 = postFinish)

    //callPendenciesOperator
    suspend fun getPendenciesOperatorReceiptProduct() =
        service.getPendenciesOperatorReceiptProduct()


}