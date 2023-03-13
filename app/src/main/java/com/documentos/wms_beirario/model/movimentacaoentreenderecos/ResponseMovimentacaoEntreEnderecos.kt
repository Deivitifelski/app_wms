package com.documentos.wms_beirario.model.movimentacaoentreenderecos

import java.io.Serializable

//RESPOSTA TAREFAS MOVIMENTAÇAO ->
data class MovementResponseModel1(
    val idArmazem: Int,
    val idTarefa: String,
    val documento: Long,
    val data: String,
    val operadorColetor: String
) : Serializable

//RESPOSTA DO ITEM CLICADO ->
data class MovementReturnItemClickMov(
    val sequencial: Int,
    val numeroSerie: String,
    val enderecoVisual: String,
    val usuarioInclusao: String,
    val dataInclusao: String
) : Serializable

//NOVA TAREFA ->
data class MovementNewTask(
    val idTarefa: String
) : Serializable

//GET TAREFAS PENDENTES OPERADOR -->
data class ResponseMovParesAvulso1(
    var documentoTarefa: Long,
    var idTarefa: String,
    var itens: List<ListItens>
)

data class ListItens(
    var dataHoraInclusao: String,
    var enderecoVisual: String,
    var idEnderecoOrigem: Int,
    var numeroSerie: String? = null,
    var quantidade: Int,
    var sequencial: Int,
    var sku: String,
    var usuarioInclusao: String,
    var ean: String
)

//{
//  "idTarefa": "F668DA6410C5F4B8E0537000960AE99E",
//  "documentoTarefa": 23030838990,
//  "itens": [
//    {
//      "sequencial": 1,
//      "numeroSerie": null,
//      "idEnderecoOrigem": 8423,
//      "enderecoVisual": "0006-A-002",
//      "usuarioInclusao": "ANA_SILVA",
//      "dataHoraInclusao": "2023-03-08T15:43:30.000Z",
//      "sku": "7377.105.21736.89521.35",
//      "quantidade": 1
//    }
//  ]
//}

data class ResponseTaskOPerationItem1(
    var datainclusao: String,
    var enderecovisual: String,
    var idEnderecoOrigem: Int,
    var numeroserie: String,
    var quantidade: Int,
    var sequencial: Int,
    var sku: String,
    var usuarioinclusao: String
) : Serializable

//RESPOSTA AO LER ENDEREÇO -->
data class ResponseReadingMov2(
    var enderecoVisual: String,
    var idArea: Int,
    var idEndereco: Int,
    var nomeArea: String
) : java.io.Serializable

//RESPONSE ADICIONA PRODUTO -->
data class ResponseAddProductMov3(
    var result: String
)

//RESPOSTA CANCELAMENTO -->
data class ResponseCancelMov5(
    var result: String
)
