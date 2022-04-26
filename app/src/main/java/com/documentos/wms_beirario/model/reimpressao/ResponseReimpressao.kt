package com.documentos.wms_beirario.model.reimpressao



/**RESPONSE REIMPRESSAO -->*/

class ResponseReimpressaoX : ArrayList<ResponseReimpressaoItem>()
data class ResponseReimpressaoItem(
    val codigoZpl: String,
    val descricaoEtiqueta: String,
    val ordemImpressao: Int
)