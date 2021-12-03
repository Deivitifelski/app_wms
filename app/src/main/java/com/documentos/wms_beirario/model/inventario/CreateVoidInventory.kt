package com.documentos.wms_beirario.model.inventario

class CreateVoidInventory : ArrayList<CreateVoidInventoryItem>()

data class CreateVoidInventoryItem(
    val cabedal: Int,
    val cor: Int,
    val distribuicao: List<Distribuicao>,
    val linha: Int,
    val referencia: Int
)

data class Distribuicao(
    val tamanho: String,
    var quantidade: Int

)