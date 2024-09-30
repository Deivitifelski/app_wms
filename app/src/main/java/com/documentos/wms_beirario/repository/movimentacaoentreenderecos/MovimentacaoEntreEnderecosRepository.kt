package com.documentos.wms_beirario.repository.movimentacaoentreenderecos

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.movementVol.BodyAddVolume
import com.documentos.wms_beirario.model.movementVol.ResponseAddVol
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.BodyCancelMov5
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.RequestAddProductMov3
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.RequestBodyFinalizarMov4
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.RequestReadingAndressMov2
import retrofit2.Response

class MovimentacaoEntreEnderecosRepository {
    /**Lista tarefas 01*/
    suspend fun movementReturnTaskMovement(idArmazem: Int, token: String) =
        RetrofitClient().getClient().movementShowMovements(idArmazem, token)

    /**Aponta endereço 02*/
    suspend fun readingAndressMov2(body: RequestReadingAndressMov2, idArmazem: Int, token: String) =
        RetrofitClient().getClient()
            .readingAndressMov2(body = body, idArmazem = idArmazem, token = token)

    /**Adiciona um produto 03*/
    suspend fun addProductMov3(body: RequestAddProductMov3, idArmazem: Int, token: String) =
        RetrofitClient().getClient()
            .addProductMov3(body = body, idArmazem = idArmazem, token = token)

    /**Finaliza tarefa -->*/
    suspend fun finishTaskMov4(body: RequestBodyFinalizarMov4, idArmazem: Int, token: String) =
        RetrofitClient().getClient()
            .finishTaskMov4(body = body, idArmazem = idArmazem, token = token)

    /**Cancelar tarefa -->*/
    suspend fun cancelMov5(body: BodyCancelMov5, idArmazem: Int, token: String) =
        RetrofitClient().getClient().cancelMov5(body = body, idArmazem = idArmazem, token = token)

    /**Adicionar volume -->*/
    suspend fun addVolume(
        body: BodyAddVolume,
        idArmazem: Int,
        token: String
    ) = RetrofitClient().getClient().addVolume(body = body, token = token, idArmazem = idArmazem)



}