package com.documentos.wms_beirario.repository.inventario

import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.model.inventario.RequestInventoryReadingProcess

class InventoryoRepository1(private val mRetrofitService: RetrofitService) {

    suspend fun pendingTaskInventory1() = this.mRetrofitService.Inventorypending1()

    suspend fun inventoryQrCode2(inventoryReadingProcess: RequestInventoryReadingProcess) =
        this.mRetrofitService.inventoryQrCode2(inventoryReadingProcess = inventoryReadingProcess)
}