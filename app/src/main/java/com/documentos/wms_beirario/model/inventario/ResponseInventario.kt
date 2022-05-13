package com.documentos.wms_beirario.model.inventario
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class ResponseInventoryPending1(
    val id: Int,
    val idArmazem: Int,
    val documento: Long,
    val dataHora: String,
    val numeroContagem: Int,
    val solicitante: String,
    val situacao: String,
) : Serializable

//------------------------------------------------>

data class ResponseQrCode2(
    @SerializedName("result") val result: ProcessaLeituraResponseInventario2,
    @SerializedName("leituraEndereco") val leituraEnderecoCreateRvFrag2: List<LeituraEndInventario2List>
) : Serializable

data class ProcessaLeituraResponseInventario2(
    val codigoBarras: String? = null,
    val idEndereco: Int? = null,
    val enderecoVisual: String? = null,
    val idProduto: Int? = null,
    val EAN: String? = null,
    val sku: String? = null,
    val numeroSerie: String? = null,
    val layoutEtiqueta: String? = null,
    val idInventarioAbastecimentoItem: String? = null,
    val produtoPronto: Int? = null,
    val produtoVolume: Int? = null,
) : Serializable

@Parcelize
data class LeituraEndInventario2List(
    val codigoBarras: String,
    val criadoEm: String,
    val nomeUsuario: String,
    val sku: String
) : Parcelable

data class ResponseListRecyclerView(
    val produtos: List<ProdutoResponseInventarioItem>,
    val volumes: List<VolumesResponseInventarioItem>,
) : Serializable

data class ProdutoResponseInventarioItem(
    @SerializedName("codigoBarras") val codigoBarras: String,
    @SerializedName("codigoCorrugado") val codigoCorrugado: Any,
    @SerializedName("codigoGrade") val codigoGrade: Any,
    @SerializedName("dataUltimaLeitura") val dataUltimaLeitura: String,
    @SerializedName("enderecoVisual") val enderecoVisual: String,
    @SerializedName("id") val id: String,
    @SerializedName("idEndereco") val idEndereco: Int,
    @SerializedName("idOperadorColetor") val idOperadorColetor: Int,
    @SerializedName("itemPedido") val itemPedido: Any,
    @SerializedName("numeroContagem") val numeroContagem: Int,
    @SerializedName("numeroPedido") val numeroPedido: Any,
    @SerializedName("numeroSerie") val numeroSerie: Any,
    @SerializedName("quantidade") val quantidade: Int,
    @SerializedName("sku") val sku: String,
    @SerializedName("usuario") val usuario: String
) : Serializable


data class VolumesResponseInventarioItem(
    val codigoBarras: String,
    val codigoCorrugado: Int,
    val codigoGrade: Int,
    val dataUltimaLeitura: String,
    val enderecoVisual: String,
    val id: String,
    val idEndereco: Int,
    val idOperadorColetor: Int,
    val itemPedido: Int,
    val numeroContagem: Int,
    val numeroPedido: Int,
    val numeroSerie: String,
    val quantidade: Int,
    val sku: String,
    val usuario: String,
    val imprime: Int? = null
) : Serializable

//------------------------------------------------>

class InventoryResponseCorrugados : ArrayList<InventoryResponseCorrugadosItem>()
data class InventoryResponseCorrugadosItem(
    val descricao: String,
    val id: Int,
    val quantidadePares: Int
)

data class EtiquetaInventory(
    @SerializedName("etiqueta") val etiqueta: String
) : Serializable