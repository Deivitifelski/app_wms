package com.documentos.wms_beirario.model.etiquetagem.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EtiquetagemRequest1(
    @SerializedName("numeroSerie") val numeroSerieEtiquetagem: String
) : Serializable

data class EtiquetagemRequestModel3(
    val empresa: String,
    val filial: String,
    val numeroNotaFiscal: Int,
    val serieNotaFiscal: String
)