package com.documentos.wms_beirario.repository.consultaAuditoria

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.auditoria.BodyAuditoriaFinish

class AuditoriaRepository {

    suspend fun getAuditoria1(idAuditoria: String) =
        RetrofitClient().getClient().getIdAuditoria(idAuditoria = idAuditoria)

    suspend fun getAuditoriaEstantes2(idAuditoria: String) =
        RetrofitClient().getClient().getAuditoriaEstantes(idAuditoria = idAuditoria)

    suspend fun getAuditoriaItemEstantes3(id: String, estante: String) =
        RetrofitClient().getClient().getAuditoria3(idAuditoria = id, estante = estante)

    suspend fun postAuditoriaFinish(bodyAuditoriaFinish: BodyAuditoriaFinish) =
        RetrofitClient().getClient().postAuditoria4(body = bodyAuditoriaFinish)


}