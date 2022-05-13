package com.documentos.wms_beirario.model.armazenagem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

//data class ArmazenagemRequest(
//    @SerializedName("ok") val ok: String
//) : Serializable

data class ArmazemRequestFinish(
    val idTarefa: String,
    val enderecoLeitura: String
)
