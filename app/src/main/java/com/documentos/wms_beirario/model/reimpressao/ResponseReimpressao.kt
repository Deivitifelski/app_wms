package com.documentos.wms_beirario.model.reimpressao

import java.io.Serializable

class ResultReimpressaoDefault : ArrayList<ResultReimpressaoDefaultItem>()

data class ResultReimpressaoDefaultItem(
    val descricaoTipoDocumento: String,
    val documento: String,
    val documentoTarefa: Long,
    val idTarefa: String,
    val idTipoDocumento: Int? = null,
    val numeroDocumento: String,
    val numeroSerie: String,
    val sequencialTarefa: Int,
    val tipoDocumento: String
) : Serializable

class ResponseEtiquetasReimpressao : ArrayList<ResponseEtiquetasReimpressaoItem>()

data class ResponseEtiquetasReimpressaoItem(
    val codigoZpl: String,
    val descricaoEtiqueta: String,
    val ordemImpressao: Int
) : Serializable