package com.documentos.wms_beirario.model.movimentacaoentreenderecos

import java.io.Serializable

//RESPOSTA TAREFAS MOVIMENTAÇAO ->
data class MovementResponseModel1(
    val idArmazem: Int,
    val idTarefa: String,
    val documento: Int,
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
):Serializable
