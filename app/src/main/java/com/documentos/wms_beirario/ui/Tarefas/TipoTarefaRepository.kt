package com.documentos.wms_beirario.ui.Tarefas

import com.documentos.wms_beirario.data.RetrofitService

class TipoTarefaRepository(private val mRetrofitService: RetrofitService) {

    suspend fun getTarefas() =
        this.mRetrofitService.getTipoTarefa()

}