package com.documentos.wms_beirario.ui.TaskType

import com.documentos.wms_beirario.data.ServiceApi

class TypeTaskRepository(private val mServiceApi: ServiceApi) {

    suspend fun getTarefas() =
        this.mServiceApi.getTipoTarefa()

}