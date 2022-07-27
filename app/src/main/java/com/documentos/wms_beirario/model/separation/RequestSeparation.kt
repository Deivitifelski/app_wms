package com.documentos.wms_beirario.model.separation

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class bodySeparation3(
    val codigoBarras: String,
    val idEnderecoOrigem: Int
)

data class RequestSeparationArraysAndares1(
    val andares: List<String>,
) : Serializable

data class RequestSeparationArraysAndaresEstante3(
    val andares: List<String>,
    val estantes: List<String>,
) : Serializable