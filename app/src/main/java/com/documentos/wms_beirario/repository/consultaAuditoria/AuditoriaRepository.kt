package com.documentos.wms_beirario.repository.consultaAuditoria

import com.documentos.wms_beirario.data.RetrofitClient

class AuditoriaRepository {

    suspend fun getAuditoria1(idAuditoria: String) =
        RetrofitClient().getClient().getIdAuditoria(idAuditoria = idAuditoria)

    suspend fun getAuditoriaEstantes2(idAuditoria: String) =
        RetrofitClient().getClient().getAuditoriaEstantes(idAuditoria = idAuditoria)


}