package com.documentos.wms_beirario.repository.qualityControl

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.qualityControl.BodyFinishQualityControl
import com.documentos.wms_beirario.model.qualityControl.BodyGenerateRequestControlQuality
import com.documentos.wms_beirario.model.qualityControl.BodySetAprovadoQuality
import com.documentos.wms_beirario.model.qualityControl.BodySetPendenceQuality

class QualityControlRepository {

    //1 - Busca as tarefas -->
    suspend fun getTaskQualityControl1(codBarrasEnd: String, idArmazem: Int, token: String) =
        RetrofitClient().getClient().getTaskQualityControl1(
            codBarrasEnd = codBarrasEnd,
            idArmazem = idArmazem,
            token = token
        )


    //2 - Set aprovados -->
    suspend fun postSetAprovadosQualityControl(
        body: BodySetAprovadoQuality,
        idArmazem: Int,
        token: String
    ) =
        RetrofitClient().getClient()
            .postSetAprovadosQualityControl(body = body, idArmazem = idArmazem, token = token)

    //3 - Set Rejeitados -->
    suspend fun postSetReprovadosQualityControl(
        body: BodySetAprovadoQuality,
        idArmazem: Int,
        token: String
    ) =
        RetrofitClient().getClient()
            .postSetReprovadosQualityControl(body = body, idArmazem = idArmazem, token = token)

    //4 - Set Pendentes -->
    suspend fun postSetPendenteQualityControl(
        body: BodySetPendenceQuality,
        idArmazem: Int,
        token: String
    ) =
        RetrofitClient().getClient()
            .postSetPendenteQualityControl(body = body, idArmazem = idArmazem, token = token)

    //5 - Gera requisição -->
    suspend fun postGenerateRequestQualityControl(
        body: BodyGenerateRequestControlQuality,
        idArmazem: Int,
        token: String
    ) =
        RetrofitClient().getClient()
            .postGenerateRequestQualityControl(body = body, idArmazem = idArmazem, token = token)

    //6 - Finalizar -->
    suspend fun postFinishQualityControl(
        body: BodyFinishQualityControl,
        idArmazem: Int,
        token: String
    ) =
        RetrofitClient().getClient()
            .postFinishQualityControl(body = body, idArmazem = idArmazem, token = token)


}