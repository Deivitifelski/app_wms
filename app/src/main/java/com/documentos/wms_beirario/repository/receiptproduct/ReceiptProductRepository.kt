package com.documentos.wms_beirario.repository.receiptproduct

import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.model.receiptproduct.PosLoginValidadREceipPorduct
import com.documentos.wms_beirario.model.receiptproduct.QrCodeReceipt1

class ReceiptProductRepository(val mData: ServiceApi) {

    suspend fun getReceiptProduct1(
        filtrarOperador: Boolean,
        idOperador: String
    ) = mData.getReceiptProduct1(filtrarOperador = filtrarOperador, idOperador = idOperador)

    suspend fun postReceiptProduct1(codBarras: QrCodeReceipt1) =
        mData.postReadingReceiptProduct2(qrCode = codBarras)

    suspend fun getItemProduct2(idOperador: String, filtrarOperador: Boolean, pedido: String) =
        mData.getReceiptProduct3(
            idOperador = idOperador,
            filtrarOperador = filtrarOperador,
            pedido = pedido
        )

    suspend fun validadAcessReceiptProduct(postLoginValidadREceipPorduct: PosLoginValidadREceipPorduct) =
        mData.postValidAccesReceiptProduct(posLoginValidadREceipPorduct = postLoginValidadREceipPorduct)

}