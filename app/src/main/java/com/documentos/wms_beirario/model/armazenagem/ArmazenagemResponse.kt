

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class ArmazenagemResponse(
    @SerializedName("id") val id: String,
    @SerializedName("idAreaOrigem") val idAreaOrigem: Int,
    @SerializedName("idEnderecoOrigem") val idEnderecoOrigem: Int,
    @SerializedName("codigoBarrasEnderecoOrigem") val codigoBarrasEnderecoOrigem: String,
    @SerializedName("enstanteEnderecoOrigem") val enstanteEnderecoOrigem: String,
    @SerializedName("visualEnderecoOrigem") val visualEnderecoOrigem: String,
    @SerializedName("idAreaDestino") val idAreaDestino: Int,
    @SerializedName("idEnderecoDestino") val idEnderecoDestino: Int,
    @SerializedName("codigoBarrasEnderecoDestino") val codigoBarrasEnderecoDestino: String,
    @SerializedName("enstanteEnderecoDestino") val enstanteEnderecoDestino: String,
    @SerializedName("visualEnderecoDestino") val visualEnderecoDestino: String,
    @SerializedName("quantidade") val quantidade: Int
) : Parcelable

