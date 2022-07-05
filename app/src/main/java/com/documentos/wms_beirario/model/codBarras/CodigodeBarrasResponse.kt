package com.documentos.wms_beirario.model.codBarras

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CodigodeBarrasResponse(
    @SerializedName("volume") val volumeCodBarras: VolumeModelCB?,
    @SerializedName("endereco") val enderecoCodBarras: EnderecoModel?,
    @SerializedName("produto") val produtoCodBarras: CodBarrasProdutoResponseModel?,
) : Parcelable
