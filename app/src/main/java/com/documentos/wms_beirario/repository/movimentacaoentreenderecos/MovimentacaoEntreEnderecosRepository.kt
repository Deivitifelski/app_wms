package com.documentos.wms_beirario.repository.movimentacaoentreenderecos

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.BodyMov1
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementAddTask
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementFinishAndress

class MovimentacaoEntreEnderecosRepository() {

    /**Lista tarefas 01*/
    suspend fun movementReturnTaskMovement(filterUser:Boolean) =
        RetrofitClient().getClient().MovementShowMovements(filtrarOperador = filterUser)

    /**Lista tarefas apos click na mesma 02*/
    suspend fun returnTaskItemClick(id_tarefa: String) =
        RetrofitClient().getClient().movementgetRetornaItensMov2(idTarefa = id_tarefa)

    /**NOVA TAREFA CLIK BUTTON*/
    suspend fun movementNewTask() =
        RetrofitClient().getClient().movementAddNewTask()

    /**ADICIONAR TAREFA EDIT TEXT*/
    suspend fun movementAddTask(movementAddTask: MovementAddTask) =
        RetrofitClient().getClient().movementAddItemMov(movementAddTask = movementAddTask)

    /**finalizar Tarefas*/
    suspend fun movementFinishMovement(
        postRequestModelFinish: MovementFinishAndress
    ) = RetrofitClient().getClient()
        .movementFinishMov(postRequestModelFinish = postRequestModelFinish)


}