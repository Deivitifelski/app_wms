package com.documentos.wms_beirario.repository.inventario

import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.model.inventario.CreateVoidPrinter
import com.documentos.wms_beirario.model.inventario.RequestInventoryReadingProcess

class InventoryoRepository1(private val mRetrofitService: RetrofitService) {

    suspend fun pendingTaskInventory1() = this.mRetrofitService.Inventorypending1()

    suspend fun inventoryQrCode2(inventoryReadingProcess: RequestInventoryReadingProcess) =
        this.mRetrofitService.inventoryQrCode2(inventoryReadingProcess = inventoryReadingProcess)

    suspend fun inventoryResponseRecyclerView(
        idEndereco: Int,
        idInventario: Int,
        numeroContagem: Int
    ) = this.mRetrofitService.inventoryResponseRecyclerView(
        idInventario = idInventario,
        numeroContagem = numeroContagem,
        idEndereco = idEndereco
    )

    suspend fun getCorrugados() = this.mRetrofitService.getCorrugados()

    suspend fun postInventoryCreateVoid(
        idEndereco: Int,
        idInventario: Int,
        numeroContagem: Int,
        createVoidPrinter: CreateVoidPrinter

    ) = this.mRetrofitService.inventoryCreateVoidPrinter(
        idInventario = idInventario,
        numeroContagem = numeroContagem,
        idEndereco = idEndereco,
        createVoidPrinter = createVoidPrinter
    )
}