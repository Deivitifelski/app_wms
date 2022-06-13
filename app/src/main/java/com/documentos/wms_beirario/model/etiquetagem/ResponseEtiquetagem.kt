package com.documentos.wms_beirario.model.etiquetagem
import java.io.Serializable


class ResponseEtiquetagemEdit1 : ArrayList<ResponseEtiquetagemEdit1Item>()

data class ResponseEtiquetagemEdit1Item(
    val codigoZpl: String,
    val descricaoEtiqueta: String,
    val ordemImpressao: Int
)

data class EtiquetagemResponse2(
    val dataEmissao: String?,
    val empresa: String,
    val filial: String,
    val numeroNotaFiscal: Int,
    val serieNotaFiscal: String,
    val quantidadeVolumes: Int,
    val quantidadeVolumesPendentes: Int,
) : Serializable


data class EtiquetagemResponse3(
    val numeroPedido: Int,
    val quantidadePendente: Int,
    val quantidadeVolumes: Int
)

/**pedidos por nf -->*/
class ResponsePendencePedidoEtiquetagem : ArrayList<ResponsePendencePedidoEtiquetagemItem>()

data class ResponsePendencePedidoEtiquetagemItem(
    val numeroNormativa: Int? = null,
    val numeroPedido: Int,
    val quantidadePendente: Int,
    val quantidadeVolumes: Int,
    val tipoPedido: String
)

/**pedidos por Onda -->*/
class ResponsePendencyOndaEtiquetagem : ArrayList<ResponsePendencyOndaEtiquetagemItem>()

data class ResponsePendencyOndaEtiquetagemItem(
    val numeroOnda: String,
    val quantidadeDocumentos: Int,
    val quantidadeVolumes: Int,
    val quantidadePendentes: Int,
)