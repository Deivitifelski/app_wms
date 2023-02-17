package com.documentos.wms_beirario.model.reimpressao

data class RequestEtiquetasReimpressaoBody(
    var idTarefa: String? = null,
    var sequencialTarefa: Int? = null,
    var idInventarioAbastecimentoItem: String? = null,
    var idOrdemMontagemVolume: String? = null,
)
