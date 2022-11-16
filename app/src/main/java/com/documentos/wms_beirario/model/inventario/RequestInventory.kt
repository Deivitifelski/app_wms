package com.documentos.wms_beirario.model.inventario

import java.io.Serializable


data class RequestInventoryReadingProcess(
    var idInventario: Int,
    var numeroContagem: Int,
    var idEndereco: Int? =null,
    var codigoBarras: String
) : Serializable