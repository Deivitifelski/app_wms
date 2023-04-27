package com.documentos.wms_beirario.repository.qualityControl

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.qualityControl.BodyFinishQualityControl
import com.documentos.wms_beirario.model.qualityControl.BodyGenerateRequestControlQuality
import com.documentos.wms_beirario.model.qualityControl.BodySetAprovadoQuality
import com.documentos.wms_beirario.model.qualityControl.BodySetPendenceQuality

class QualityControlRepository {

    //1 - Busca as tarefas -->
    suspend fun getTaskQualityControl1(codBarrasEnd: String) =
        RetrofitClient().getClient().getTaskQualityControl1(codBarrasEnd = codBarrasEnd)


    //2 - Set aprovados -->
    suspend fun postSetAprovadosQualityControl(body: BodySetAprovadoQuality) =
        RetrofitClient().getClient().postSetAprovadosQualityControl(body = body)

    //3 - Set Rejeitados -->
    suspend fun postSetReprovadosQualityControl(body: BodySetAprovadoQuality) =
        RetrofitClient().getClient().postSetReprovadosQualityControl(body = body)

    //4 - Set Pendentes -->
    suspend fun postSetPendenteQualityControl(body: BodySetPendenceQuality) =
        RetrofitClient().getClient().postSetPendenteQualityControl(body = body)

    //5 - Gera requisição -->
    suspend fun postGenerateRequestQualityControl(body: BodyGenerateRequestControlQuality) =
        RetrofitClient().getClient().postGenerateRequestQualityControl(body = body)

    //6 - Finalizar -->
    suspend fun postFinishQualityControl(body: BodyFinishQualityControl) =
        RetrofitClient().getClient().postFinishQualityControl(body = body)


}