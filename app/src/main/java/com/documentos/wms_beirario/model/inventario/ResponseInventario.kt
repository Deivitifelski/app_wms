package com.documentos.wms_beirario.model.inventario

import com.google.gson.annotations.SerializedName
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
    val codigoBarras: String,
    val idEndereco: Int,
    val enderecoVisual: String,
    val idProduto: Int,
    val EAN: Any,
    val sku: Any,
    val numeroSerie: Int,
    val layoutEtiqueta: Any,
    val idInventarioAbastecimentoItem: String,
    val produtoPronto: String,
    val produtoVolume: Int,
) : Serializable

data class LeituraEndInventario2List(
    val codigoBarras: String,
    val criadoEm: String,
    val nomeUsuario: String,
    val sku: String
) : Serializable


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