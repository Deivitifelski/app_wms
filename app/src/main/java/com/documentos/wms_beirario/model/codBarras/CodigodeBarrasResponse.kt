package com.documentos.wms_beirario.model.codBarras

import android.os.Parcelable
import com.example.coletorwms.model.codBarras.Cod.EnderecoModel
import com.example.coletorwms.model.codBarras.CodBarrasProdutoResponseModel
import com.example.coletorwms.model.codBarras.VolumeModelCB
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable
@Parcelize
data class CodigodeBarrasResponse(
    @SerializedName("volume") val volumeCodBarras: VolumeModelCB?,
    @SerializedName("endereco") val enderecoCodBarras: EnderecoModel?,
    @SerializedName("produto") val produtoCodBarras: CodBarrasProdutoResponseModel?,
) : Parcelable
