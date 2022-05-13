

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class EnderecoModel(
    val tipo: String?,
    val nomeArea: String?,
    val enderecoVisual: String?,
    val armazem: Int?,
    val volumes: List<VolumesModel?>,
    val listaNumeroSerie: List<ListaNumeroSerieModel?>,
    val ultimosMovimentos: List<UltimosMovimentosModel?>,
    val produtos: List<CodBarrasProdutoClick>
) : Parcelable, Serializable

@Parcelize
data class VolumesModel(
    val nome: String?,
    val sku: String?,
    val codigoEmbalagem: Int?,
    val descricaoEmbalagem: String?,
    val codigoDistribuicao: Int?,
    val descricaoDistribuicao: String?,
    val quantidade: Int?,
): Parcelable
@Parcelize
data class CodBarrasProdutoClick(
    val codigoMarca: Int,
    val descricaoMarca: String,
    val ean: String,
    val nome: String,
    val quantidade: Int,
    val sku: String,
    val tamanho: String
): Parcelable

@Parcelize
data class ListaNumeroSerieModel(
    val numeroSerie: String?,
): Parcelable

@Parcelize
data class UltimosMovimentosModel(
    val data: String?,
    val usuario: String?,
    val numeroSerie: String?,
): Parcelable

