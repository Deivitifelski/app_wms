package com.documentos.wms_beirario.model.separation

import java.io.Serializable

/**
 * PRIMEIRO GET TRAZENDO ANDARES -->
 */
data class ResponseSeparation1(
    var andar: String,
    var quantidadeEnderecos: Int,
    var quantidadeVolumes: Int,
    var status: Boolean = false
)
/**-------------------------------------------------------------------------->*/
/**
 * POST TRAZENDO AS ESTANTES COM BASE NOS ANDARES SELECIONADOS -->
 */
class ResponseEstantes : ArrayList<ResponseEstantesItem>()

data class ResponseEstantesItem(
    val estante: String,
    var status: Boolean = false
) : Serializable

/**
 * RESULTADO DE TAREFAS ANDARES SEPARAÇÃO -->
 */
class ResponseTarefasANdaresSEparation3 : ArrayList<ResponseEstantesAndaresSeparation3Item>()

data class ResponseEstantesAndaresSeparation3Item(
    var idEndereco: Int,
    var enderecoVisual: String,
    var quantidadeProdutos: Int,
    var esvaziar: Int,
    var codBarrasEndOrigem: String
) : Serializable

/**-------------------------------------------------------------------------->*/

class SeparacaoProdAndress4 : ArrayList<SeparacaoProdAndress4Item>()

data class SeparacaoProdAndress4Item(
    val idProduto: Long,
    val sku: String,
    val codigodistribuicao: Int? = null,
    val codigoEmbalagem: Int,
    val quantidade: Int,
    val quantidadeApontada: Int,
    val pedido: String? = null,
    val numeroSerie: String? = null
)

data class SeparationEnd(
    var quantidade: Int,
    var codBarrasEndOrigem: String
)

class ResponseEtiquetarSeparar : ArrayList<ResponseEtiquetarSepararItem>()

data class ResponseEtiquetarSepararItem(
    var codigoZpl: String?,
    var descricaoEtiqueta: String?,
    var ordemImpressao: Int?
)
