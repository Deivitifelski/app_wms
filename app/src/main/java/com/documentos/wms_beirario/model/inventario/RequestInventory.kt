package com.documentos.wms_beirario.model.inventario

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RequestInventoryReadingProcess(
    val idInventario: Int,
    val numeroContagem: Int,
    var idEndereco: Int? = null,
    var codigoBarras: String
) : Parcelable