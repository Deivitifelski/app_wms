package com.documentos.wms_beirario.repository.movimentacaoentreenderecos

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementAddProduct
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementFinishAndress
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.RequestAddProductMov3
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.RequestReadingAndressMov2

class MovimentacaoEntreEnderecosRepository {
    /**Lista tarefas 01*/
    suspend fun movementReturnTaskMovement() =
        RetrofitClient().getClient().movementShowMovements()

    /**Aponta endereço 02*/
    suspend fun readingAndressMov2(body: RequestReadingAndressMov2) =
        RetrofitClient().getClient().readingAndressMov2(body = body)

    /**Adiciona um produto 03*/
    suspend fun addProductMov3(body: RequestAddProductMov3) =
        RetrofitClient().getClient().addProductMov3(body = body)

    /**Lista tarefas apos click na mesma 02*/
    suspend fun returnTaskItemClick(id_tarefa: String) =
        RetrofitClient().getClient().movementgetRetornaItensMov2(idTarefa = id_tarefa)

    /**NOVA TAREFA CLIK BUTTON*/
    suspend fun movementNewTask() =
        RetrofitClient().getClient().movementAddNewTask()

    /**ADICIONAR TAREFA EDIT TEXT*/
    suspend fun movementAddTask(movementAddProduct: MovementAddProduct) =
        RetrofitClient().getClient().movementAddItemMov(movementAddProduct = movementAddProduct)

    /**finalizar Tarefas*/
    suspend fun movementFinishMovement(
        postRequestModelFinish: MovementFinishAndress
    ) = RetrofitClient().getClient()
        .movementFinishMov(postRequestModelFinish = postRequestModelFinish)


}