package com.documentos.wms_beirario.repository.picking

import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.model.picking.PickingRequest1
import com.documentos.wms_beirario.model.picking.PickingRequest2

class PickingRepository(private val mService: ServiceApi) {

    //1
    suspend fun getAreaPicking1() = this.mService.getAreaPicking1()

    //2
    suspend fun getItensPicking2(idArea: Int) =
        this.mService.getReturnTarefasPicking2(idArea = idArea)

    //3
    suspend fun posPicking(idArea: Int, pickingRepository: PickingRequest1) =
        this.mService.postItemLidoPicking3(idArea = idArea, picking3 = pickingRepository)

    //4
    suspend fun getTaskPicking() = this.mService.getGroupedProductAgrupadoPicking4()

    //5
    suspend fun postPickinFinish(pickingRequest2: PickingRequest2) =
        this.mService.postFinalizarPicking5(pickingRequest2 = pickingRequest2)


}