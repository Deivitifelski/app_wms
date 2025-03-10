package com.documentos.wms_beirario.model.qualityControl

data class BodySetAprovadoQuality(
    val codigoBarrasEan: String,
    val idTarefa: String
)

data class BodySetPendenceQuality(
    val sequencial: String,
    val idTarefa: String
)

// GERA REQUISIÇÃO -->
data class BodyGenerateRequestControlQuality(
    val idTarefa: String
)

//FINISH -->
data class BodyFinishQualityControl(
    var codigoBarrasEndDest: String? = null,
    var idTarefa: String
)
