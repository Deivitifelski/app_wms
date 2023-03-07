package com.documentos.wms_beirario.model.qualityControl

data class BodySetAprovadoQuality(
    val codigoBarrasEan: String,
    val idTarefa: String
)

//FINISH -->
data class BodyFinishQualityControl(
    var codigoCarrasEndDest: String,
    var idTarefa: String
)
