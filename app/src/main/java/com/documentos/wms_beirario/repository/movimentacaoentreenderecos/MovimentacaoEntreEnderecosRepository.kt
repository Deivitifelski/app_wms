package com.documentos.wms_beirario.repository.movimentacaoentreenderecos

import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementAddTask
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementFinishAndress

class MovimentacaoEntreEnderecosRepository(private val serviceApi: ServiceApi) {
    /**Lista tarefas 01*/
    suspend fun movementReturnTaskMovement() =
        this.serviceApi.MovementShowMovements()

    /**NOVA TAREFA EDIT TEXT*/
    suspend fun movementNewTask() =
        this.serviceApi.movementAddNewTask()

    /**Lista tarefas apos click na mesma 02*/
    suspend fun returnTaskItemClick(id_tarefa: String) =
        this.serviceApi.movementgetRetornaItensMov2(idTarefa = id_tarefa)

    /**finalizar Tarefas*/
    suspend fun movementFinishMovement(
        postRequestModelFinish: MovementFinishAndress
    ) = this.serviceApi.movementFinishMov(postRequestModelFinish = postRequestModelFinish)

    /**ADICIONAR TAREFA EDIT TEXT*/
    suspend fun movementAddTask(movementAddTask: MovementAddTask) =
        this.serviceApi.movementAddItemMov(movementAddTask = movementAddTask)


}