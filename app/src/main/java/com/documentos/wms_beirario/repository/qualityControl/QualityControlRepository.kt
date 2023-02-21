package com.documentos.wms_beirario.repository.qualityControl

import com.documentos.wms_beirario.data.RetrofitClient

class QualityControlRepository {

    //1 - Busca as tarefas -->
    suspend fun getTaskQualityControl1(codBarrasEnd: String) =
        RetrofitClient().getClient().getTaskQualityControl1(codBarrasEnd = codBarrasEnd)
}