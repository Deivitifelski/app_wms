package com.documentos.wms_beirario.repository.inventario

import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.model.inventario.CreateVoidPrinter
import com.documentos.wms_beirario.model.inventario.RequestInventoryReadingProcess

class InventoryoRepository1(private val mServiceApi: ServiceApi) {

    suspend fun pendingTaskInventory1() = this.mServiceApi.Inventorypending1()

    suspend fun inventoryQrCode2(inventoryReadingProcess: RequestInventoryReadingProcess) =
        this.mServiceApi.inventoryQrCode2(inventoryReadingProcess = inventoryReadingProcess)

    suspend fun inventoryResponseRecyclerView(
        idEndereco: Int,
        idInventario: Int,
        numeroContagem: Int
    ) = this.mServiceApi.inventoryResponseRecyclerView(
        idInventario = idInventario,
        numeroContagem = numeroContagem,
        idEndereco = idEndereco
    )

    suspend fun getCorrugados() = this.mServiceApi.getCorrugados()

    suspend fun postInventoryCreateVoid(
        idEndereco: Int,
        idInventario: Int,
        numeroContagem: Int,
        createVoidPrinter: CreateVoidPrinter

    ) = this.mServiceApi.inventoryCreateVoidPrinter(
        idInventario = idInventario,
        numeroContagem = numeroContagem,
        idEndereco = idEndereco,
        createVoidPrinter = createVoidPrinter
    )

    suspend fun getInventoryPrinterVol(
        idVolume:String
    ) = this.mServiceApi.inventoryPrinterVol(idInventarioAbastecimentoItem = idVolume)
}