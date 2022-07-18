package com.documentos.wms_beirario.model.separation

import java.io.Serializable


data class ResponseItemsSeparationItem(
    val estante: String,
    val idArea: Int,
    val nomeArea: String,
    val siglaArea: String,
    var status: Boolean
) : Serializable


/**
 * GET ANDARES ->
 */
class ResponseGetAndaresSeparation : ArrayList<ResponseGetAndaresSeparationItem>()

data class ResponseGetAndaresSeparationItem(
    var andar: String,
    val idArea: Int,
    val nomeArea: String,
    val siglaArea: String,
    var status: Boolean
) : Serializable

data class ResponseListCheckBoxItem(
    val ANDAR_ENDERECO_ORIGEM: String,
    val BOX_ENDERECO_ORIGEM: String,
    val codigoBarrasEnderecoOrigem: String,
    val enderecoVisualOrigem: String,
    val estanteEnderecoOrigem: String,
    val flagRestanteSaldo: Int,
    val idAreaDestino: Int,
    val idAreaOrigem: Int,
    val idEnderecoDestino: Int,
    val idEnderecoOrigem: Int,
    val idProduto: Int,
    val nomeAreaDestino: String,
    val nomeAreaOrigem: String,
    val produtosDistintos: Int,
    val quantidadeSeparar: Int,
    val siglaAreaDestino: String,
    val siglaAreaOrigem: String
) : Serializable

data class SeparationEnd(
    val idEnderecoOrigem: Int,
    val idEnderecoDestino: Int,
    val idProduto: Int,
    val quantidade: Int
)

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
class ResponseSeparationNew : ArrayList<ResponseSeparationNewItem>()

data class ResponseSeparationNewItem(
    val andarEnderecoOrigem: String,
    val codigoBarrasEnderecoOrigem: String,
    val enderecoVisualOrigem: String,
    val estanteEnderecoOrigem: String,
    val flagRestanteSaldo: Int,
    val idAreaDestino: Int,
    val idAreaOrigem: Int,
    val idEnderecoDestino: Int,
    val idEnderecoOrigem: Int,
    val idProduto: Int,
    val nomeAreaDestino: String,
    val nomeAreaOrigem: String,
    val produtosDistintos: Int,
    val quantidadeSeparar: Int,
    val siglaAreaDestino: String,
    val siglaAreaOrigem: String
)

