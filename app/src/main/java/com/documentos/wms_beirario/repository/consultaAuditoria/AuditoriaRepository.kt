package com.documentos.wms_beirario.repository.consultaAuditoria

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.auditoria.BodyAuditoriaFinish

class AuditoriaRepository {
   /** 1 */
    suspend fun getAuditoria1(idAuditoria: String) =
        RetrofitClient().getClient().getIdAuditoria(idAuditoria = idAuditoria)
    /** 2 */
    suspend fun getAuditoriaEstantes2(idAuditoria: String) =
        RetrofitClient().getClient().getAuditoriaEstantes(idAuditoria = idAuditoria)
    /** 3 */
    suspend fun getAuditoriaItemEstantes3(id: String, estante: String) =
        RetrofitClient().getClient().getAuditoria3(idAuditoria = id, estante = estante)
    /** 4 */
    suspend fun postAuditoriaFinish(bodyAuditoriaFinish: BodyAuditoriaFinish) =
        RetrofitClient().getClient().postAuditoria4(body = bodyAuditoriaFinish)


}