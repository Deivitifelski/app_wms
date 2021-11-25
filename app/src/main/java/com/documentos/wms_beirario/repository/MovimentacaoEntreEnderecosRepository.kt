package com.documentos.wms_beirario.repository

import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementAddTask
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementFinishAndress

class MovimentacaoEntreEnderecosRepository(private val retrofitService: RetrofitService) {
    /**Lista tarefas 01*/
    suspend fun movementReturnTaskMovement() =
        this.retrofitService.MovementShowMovements()

    /**NOVA TAREFA EDIT TEXT*/
    suspend fun movementNewTask() =
        this.retrofitService.movementAddNewTask()

    /**Lista tarefas apos click na mesma 02*/
    suspend fun returnTaskItemClick(id_tarefa: String) =
        this.retrofitService.movementgetRetornaItensMov2(idTarefa = id_tarefa)

    /**finalizar Tarefas*/
    suspend fun movementFinishMovement(
        postRequestModelFinish: MovementFinishAndress
    ) = this.retrofitService.movementFinishMov(postRequestModelFinish = postRequestModelFinish)

    /**ADICIONAR TAREFA EDIT TEXT*/
    suspend fun movementAddTask(movementAddTask: MovementAddTask) =
        this.retrofitService.movementAddItemMov(movementAddTask = movementAddTask)


}