package com.documentos.wms_beirario.model.reimpressao

data class RequestEtiquetasReimpressaoBody(
    var idTarefa: String?,
    var sequencial: String?,
    var idInventarioAbastecimentoItem: String?,
    var idOrdemMontagemVolume: String?,
)
