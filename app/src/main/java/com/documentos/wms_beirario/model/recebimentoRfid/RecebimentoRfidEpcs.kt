package com.documentos.wms_beirario.model.recebimentoRfid

import com.zebra.rfid.api3.TagData

data class RecebimentoRfidEpcs(
    val tagData: TagData,
    var state:String? = "R",
)
