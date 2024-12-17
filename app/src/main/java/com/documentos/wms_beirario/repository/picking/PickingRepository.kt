package com.documentos.wms_beirario.repository.picking

import android.content.Context
import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.picking.PickingRequest1
import com.documentos.wms_beirario.model.picking.PickingRequest2
import com.documentos.wms_beirario.model.picking.SendDataPicing1

class PickingRepository(private val context: Context) {

    suspend fun getAreasPicking1(idArmazem: Int, token: String) =
        RetrofitClient(context).getClient().getReturnAreaPicking1(idArmazem, token)

    //2
    suspend fun getVolApontados(idArea: Int, idArmazem: Int, token: String) =
        RetrofitClient(context).getClient()
            .getVolApontados(idArea = idArea, idArmazem = idArmazem, token = token)

    suspend fun getVolNaoApontados(idArea: Int, idArmazem: Int, token: String) =
        RetrofitClient(context).getClient()
            .getVolNaoApontados(idArea = idArea, idArmazem = idArmazem, token = token)

    //3
    suspend fun posPickingReanding2(
        idArea: Int,
        pickingRepository: PickingRequest1,
        idArmazem: Int,
        token: String
    ) =
        RetrofitClient(context).getClient()
            .postItemLidoPicking3(
                idArea = idArea,
                picking3 = pickingRepository,
                idArmazem = idArmazem,
                token = token
            )

    //new Fluxo
    suspend fun posReandingData(sendDataPicing1: SendDataPicing1, idArmazem: Int, token: String) =
        RetrofitClient(context).getClient().postReandingDataPicking1(
            senDataPicking1 = sendDataPicing1,
            idArmazem = idArmazem,
            token = token
        )

    //2
    suspend fun getReturnGroupedProduct(idArmazem: Int, token: String) =
        RetrofitClient(context).getClient().getPickingReturnAgrounp(idArmazem = idArmazem, token = token)

    //4
    suspend fun getTaskPicking(idArmazem: Int, token: String) = RetrofitClient(context).getClient()
        .getGroupedProductAgrupadoPicking4(idArmazem = idArmazem, token = token)

    //5
    suspend fun postPickinFinish(pickingRequest2: PickingRequest2, idArmazem: Int, token: String) =
        RetrofitClient(context).getClient().postFinalizarPicking5(
            pickingRequest2 = pickingRequest2,
            idArmazem = idArmazem,
            token = token
        )


}