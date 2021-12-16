package com.documentos.wms_beirario.model.recebimento.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PostReciptQrCode1(
    val documento: String
)

data class PostReceiptQrCode2(
    val codigoBarrasNumeroSerie: String
)

data class PostReceiptQrCode3(
    @SerializedName("codigoBarrasEndereco") val codigoBarrasEndereco: String
)
