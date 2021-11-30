package com.documentos.wms_beirario.model.inventario

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


data class RequestInventoryReadingProcess(
    var idInventario: Int,
    var numeroContagem: Int,
    var idEndereco: Int? =null,
    var codigoBarras: String
) : Serializable