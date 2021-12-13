package com.documentos.wms_beirario.ui.Tarefas

import com.documentos.wms_beirario.data.ServiceApi

class TipoTarefaRepository(private val mServiceApi: ServiceApi) {

    suspend fun getTarefas() =
        this.mServiceApi.getTipoTarefa()

}