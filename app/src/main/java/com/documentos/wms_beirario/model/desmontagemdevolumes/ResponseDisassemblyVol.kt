package com.documentos.wms_beirario.model.desmontagemdevolumes

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DisassemblyResponse1(
    @SerializedName("enderecoVisual") val enderecoVisual: String,
    @SerializedName("idArea") val idArea: Int,
    @SerializedName("idEndereco") val idEndereco: Int,
    @SerializedName("quantidadeVolumes") val quantidadeVolumes: Int,
    @SerializedName("siglaArea") val siglaArea: String
) : Serializable