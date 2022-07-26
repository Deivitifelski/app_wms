package com.documentos.wms_beirario.model.separation

import java.io.Serializable

class ResponseGet1 : ArrayList<ItensResponse1>()

data class ItensResponse1(
    val estante: String,
    val idArea: Int,
    val nomeArea: String,
    val siglaArea: String,
    var status: Boolean,
    val andares: List<ResponseGetAndaresSeparationItem>
)


/**
 * GET ANDARES ->
 */
class ResponseGetAndaresSeparation : ArrayList<ResponseGetAndaresSeparationItem>()

data class ResponseGetAndaresSeparationItem(
    var andar: String,
    val idArea: Int,
    val nomeArea: String,
    val siglaArea: String,
    var status: Boolean? = null
) : Serializable


data class ResponseItemsSeparationItem(
    val estante: String,
    val idArea: Int,
    val nomeArea: String,
    val siglaArea: String,
    var status: Boolean? = null
) : Serializable

class SeparacaoProdAndress4 : ArrayList<SeparacaoProdAndress4Item>()

data class SeparacaoProdAndress4Item(
    val idProduto: Long,
    val sku: String,
    val codigoDistribuicao: Int,
    val codigoEmbalagem: Int,
    val quantidade: Int,
    val quantidadeApontada: Int
)

/**
 * NEW POST COM ANDARES E ESTANTES -->
 */
class ResponseSeparationNew : ArrayList<ResponseEstantesAndaresSeparation2Item>()

data class ResponseEstantesAndaresSeparation2Item(
    val ANDAR_ENDERECO_ORIGEM: String,
    val BOX_ENDERECO_ORIGEM: String,
    val CODIGO_BARRAS_ENDERECO_ORIGEM: String,
    val ENDERECO_VISUAL_ORIGEM: String,
    val ESTANTE_ENDERECO_ORIGEM: String,
    val ID_AREA_DESTINO: Int,
    val ID_AREA_ORIGEM: Int,
    val ID_ENDERECO_DESTINO: Int,
    val ID_ENDERECO_ORIGEM: Int,
    val ID_PRODUTO: Int,
    val FLAG_RESTANTE_SALDO: Int,
    val NOME_AREA_DESTINO: String,
    val NOME_AREA_ORIGEM: String,
    val NUMERO_SERIE: Any,
    val QUANTIDADE: Int,
    val SIGLA_AREA_DESTINO: String,
    val SIGLA_AREA_ORIGEM: String,
    val SITUACAO: String
) : Serializable

data class SeparationEnd(
    val idEnderecoOrigem: Int,
    val idEnderecoDestino: Int,
    val idProduto: Int,
    val quantidade: Int
)

