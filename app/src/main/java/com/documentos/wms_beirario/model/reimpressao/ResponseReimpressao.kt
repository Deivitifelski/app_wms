package com.documentos.wms_beirario.model.reimpressao

import java.io.Serializable

class ResultReimpressaoDefault : ArrayList<ResultReimpressaoDefaultItem>()

data class ResultReimpressaoDefaultItem(
    var descricaoTipoDocumento: String,
    var documento: String,
    var documentoTarefa: Long?,
    var idInventarioAbastecimentoItem: String? = null,
    var idOrdemMontagemVolume: String? = null,
    var idTarefa: String? = null,
    var idTipoDocumento: Int?,
    var numeroDocumento: String?,
    var numeroSerie: String? = null,
    var sequencialTarefa: Int? = null,
    var tipoDocumento: String
)

class ResponseEtiquetasReimpressao : ArrayList<ResponseEtiquetasReimpressaoItem>()

data class ResponseEtiquetasReimpressaoItem(
    val codigoZpl: String,
    val descricaoEtiqueta: String,
    val ordemImpressao: Int,
    val idEtiqueta: Int
) : Serializable