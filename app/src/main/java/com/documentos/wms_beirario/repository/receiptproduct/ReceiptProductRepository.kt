package com.documentos.wms_beirario.repository.receiptproduct

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.receiptproduct.PosLoginValidadREceipPorduct
import com.documentos.wms_beirario.model.receiptproduct.PostFinishReceiptProduct3
import com.documentos.wms_beirario.model.receiptproduct.QrCodeReceipt1
import com.documentos.wms_beirario.model.receiptproduct.PostCodScanFinish

class ReceiptProductRepository() {

    suspend fun getReceiptProduct1(
        filtrarOperador: Boolean,
        idOperador: String
    ) = RetrofitClient().getClient()
        .getReceiptProduct1(filtrarOperador = filtrarOperador, idOperador = idOperador)

    suspend fun postReceiptProduct1(codBarras: QrCodeReceipt1) =
        RetrofitClient().getClient().postReadingReceiptProduct2(qrCode = codBarras)

    suspend fun getItemProduct2(idOperador: String, filtrarOperador: Boolean, pedido: String) =
        RetrofitClient().getClient().getReceiptProduct3(
            idOperador = idOperador,
            filtrarOperador = filtrarOperador,
            pedido = pedido
        )

    suspend fun validadAcessReceiptProduct(postLoginValidadREceipPorduct: PosLoginValidadREceipPorduct) =
        RetrofitClient().getClient()
            .postValidAccesReceiptProduct(posLoginValidadREceipPorduct = postLoginValidadREceipPorduct)

    suspend fun postFinishReceiptProduct(postFinish: PostFinishReceiptProduct3) =
        RetrofitClient().getClient()
            .postFinishReceiptProduct(postFinishReceiptProduct3 = postFinish)

    //callPendenciesOperator
    suspend fun getPendenciesOperatorReceiptProduct() =
        RetrofitClient().getClient().getPendenciesOperatorReceiptProduct()

    //FINALIZA TODOS OS PEDIDOS -->
    suspend fun postFinishOrders(finishOrder: PostCodScanFinish) =
        RetrofitClient().getClient().postFinishAllOrder(finishOrder = finishOrder)


}