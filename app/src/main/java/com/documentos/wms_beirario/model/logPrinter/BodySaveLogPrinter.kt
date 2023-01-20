package com.documentos.wms_beirario.model.logPrinter

data class BodySaveLogPrinter(
    var idTarefa: String?, //Identificador da tarefa
    var sequencial: Int?, //Sequencial do item da tarefa
    var numeroSerie: String?, //Número de serie do volume (Rótulo)
    var idEtiqueta: Int, //Identificador da etiqueta.
    var idInventarioAbastecimentoItem: String?, //Identificador do inventario de abastecimento item.
    var idOrdemMontagemVolume: String?, //Identificador da ordem de montagem volume
)
