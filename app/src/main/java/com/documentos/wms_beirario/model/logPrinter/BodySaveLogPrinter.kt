package com.documentos.wms_beirario.model.logPrinter

data class BodySaveLogPrinter(
   var idTarefa: String, //Identificador da tarefa
   var sequencial: String, //Sequencial do item da tarefa
   var numeroSerie: String, //Número de serie do volume (Rótulo)
   var idEtiqueta: Int, //Identificador da etiqueta.
)
