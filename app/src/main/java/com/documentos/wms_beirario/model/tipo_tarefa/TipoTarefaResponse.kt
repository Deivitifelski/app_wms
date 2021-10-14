package com.documentos.wms_beirario.model.tipo_tarefa

data class TipoTarefaResponseItem(
    val descricao: String,
    val id: Int,
    val sigla: String
)

data class TipoTarefaResponse(
    val listTipoTarefa: ArrayList<TipoTarefaResponseItem>
)