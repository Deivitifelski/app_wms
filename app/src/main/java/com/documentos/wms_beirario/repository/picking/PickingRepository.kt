package com.documentos.wms_beirario.repository.picking

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.picking.PickingRequest1
import com.documentos.wms_beirario.model.picking.PickingRequest2
import com.documentos.wms_beirario.model.picking.SendDataPicing1

class PickingRepository() {

    suspend fun getAreasPicking1() = RetrofitClient().getClient().getReturnAreaPicking1()

    //2
    suspend fun getItensPicking2(idArea: Int) =
        RetrofitClient().getClient().getReturnTarefasPicking2(idArea = idArea)

    //3
    suspend fun posPickingReanding2(idArea: Int, pickingRepository: PickingRequest1) =
        RetrofitClient().getClient()
            .postItemLidoPicking3(idArea = idArea, picking3 = pickingRepository)

    //new Fluxo
    suspend fun posReandingData(sendDataPicing1: SendDataPicing1) =
        RetrofitClient().getClient().postReandingDataPicking1(senDataPicking1 = sendDataPicing1)

    //2
    suspend fun getReturnGroupedProduct() = RetrofitClient().getClient().getPickingReturnAgrounp()

    //4
    suspend fun getTaskPicking() = RetrofitClient().getClient().getGroupedProductAgrupadoPicking4()

    //5
    suspend fun postPickinFinish(pickingRequest2: PickingRequest2) =
        RetrofitClient().getClient().postFinalizarPicking5(pickingRequest2 = pickingRequest2)


}