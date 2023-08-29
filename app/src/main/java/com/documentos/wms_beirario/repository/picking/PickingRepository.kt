package com.documentos.wms_beirario.repository.picking

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.picking.PickingRequest1
import com.documentos.wms_beirario.model.picking.PickingRequest2
import com.documentos.wms_beirario.model.picking.SendDataPicing1

class PickingRepository() {

    suspend fun getAreasPicking1(idArmazem: Int, token: String) =
        RetrofitClient().getClient().getReturnAreaPicking1(idArmazem, token)

    //2
    suspend fun getItensPicking2(idArea: Int, idArmazem: Int, token: String) =
        RetrofitClient().getClient()
            .getReturnTarefasPicking2(idArea = idArea, idArmazem = idArmazem, token = token)

    //3
    suspend fun posPickingReanding2(
        idArea: Int,
        pickingRepository: PickingRequest1,
        idArmazem: Int,
        token: String
    ) =
        RetrofitClient().getClient()
            .postItemLidoPicking3(
                idArea = idArea,
                picking3 = pickingRepository,
                idArmazem = idArmazem,
                token = token
            )

    //new Fluxo
    suspend fun posReandingData(sendDataPicing1: SendDataPicing1, idArmazem: Int, token: String) =
        RetrofitClient().getClient().postReandingDataPicking1(
            senDataPicking1 = sendDataPicing1,
            idArmazem = idArmazem,
            token = token
        )

    //2
    suspend fun getReturnGroupedProduct(idArmazem: Int, token: String) =
        RetrofitClient().getClient().getPickingReturnAgrounp(idArmazem = idArmazem, token = token)

    //4
    suspend fun getTaskPicking(idArmazem: Int, token: String) = RetrofitClient().getClient()
        .getGroupedProductAgrupadoPicking4(idArmazem = idArmazem, token = token)

    //5
    suspend fun postPickinFinish(pickingRequest2: PickingRequest2, idArmazem: Int, token: String) =
        RetrofitClient().getClient().postFinalizarPicking5(
            pickingRequest2 = pickingRequest2,
            idArmazem = idArmazem,
            token = token
        )


}