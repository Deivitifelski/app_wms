package com.documentos.wms_beirario.model.separation.filtros

import java.io.Serializable

data class ItemDocTrans(
    val items: List<String?>? = listOf(null)
) : Serializable
