package com.documentos.wms_beirario.model.separation

import java.io.Serializable

/**
 * PRIMEIRO GET TRAZENDO ANDARES -->
 */
class ResponseAndares : ArrayList<ResponseAndaresItem>()

data class ResponseAndaresItem(
    val andar: String,
    val idArea: Int,
    val nomeArea: String,
    val siglaArea: String,
    var status: Boolean
) : Serializable
/**-------------------------------------------------------------------------->*/
/**
 * POST TRAZENDO AS ESTANTES COM BASE NOS ANDARES SELECIONADOS -->
 */
class ResponseEstantes : ArrayList<ResponseEstantesItem>()

data class ResponseEstantesItem(
    val estante: String,
    val idArea: Int,
    val nomeArea: String,
    val siglaArea: String,
    var status: Boolean
) : Serializable

/**
 * RESULTADO DE TAREFAS ANDARES SEPARAÇÃO -->
 */
class ResponseTarefasANdaresSEparation3 : ArrayList<ResponseEstantesAndaresSeparation3Item>()

data class ResponseEstantesAndaresSeparation3Item(
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

/**-------------------------------------------------------------------------->*/

class SeparacaoProdAndress4 : ArrayList<SeparacaoProdAndress4Item>()

data class SeparacaoProdAndress4Item(
    val idProduto: Long,
    val sku: String,
    val codigoDistribuicao: Int,
    val codigoEmbalagem: Int,
    val quantidade: Int,
    val quantidadeApontada: Int
)


data class SeparationEnd(
    val idEnderecoOrigem: Int,
    val idEnderecoDestino: Int,
    val idProduto: Int,
    val quantidade: Int
)

