package com.documentos.wms_beirario.repository.inventario

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.inventario.CreateVoidPrinter
import com.documentos.wms_beirario.model.inventario.RequestInventoryReadingProcess

class InventoryoRepository1() {

    suspend fun pendingTaskInventory1() = RetrofitClient().getClient().Inventorypending1()

    suspend fun inventoryQrCode2(inventoryReadingProcess: RequestInventoryReadingProcess) =
        RetrofitClient().getClient()
            .inventoryQrCode2(inventoryReadingProcess = inventoryReadingProcess)

    suspend fun inventoryResponseRecyclerView(
        idEndereco: Int,
        idInventario: Int,
        numeroContagem: Int
    ) = RetrofitClient().getClient().inventoryResponseRecyclerView(
        idInventario = idInventario,
        numeroContagem = numeroContagem,
        idEndereco = idEndereco
    )

    suspend fun getCorrugados() = RetrofitClient().getClient().getCorrugados()

    suspend fun postInventoryCreateVoid(
        idEndereco: Int,
        idInventario: Int,
        numeroContagem: Int,
        createVoidPrinter: CreateVoidPrinter

    ) = RetrofitClient().getClient().inventoryCreateVoidPrinter(
        idInventario = idInventario,
        numeroContagem = numeroContagem,
        idEndereco = idEndereco,
        createVoidPrinter = createVoidPrinter
    )

    suspend fun getInventoryPrinterVol(
        idVolume: String
    ) = RetrofitClient().getClient().inventoryPrinterVol(idInventarioAbastecimentoItem = idVolume)
}