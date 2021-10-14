package com.documentos.wms_beirario.ui.Tarefas

import com.documentos.wms_beirario.data.RetrofitService

class TipoTarefaRepository(private val mRetrofitService: RetrofitService) {

    fun getTarefas(id_armazem: Int, token: String) =
        this.mRetrofitService.getTipoTarefa( id_armazem,token)

}