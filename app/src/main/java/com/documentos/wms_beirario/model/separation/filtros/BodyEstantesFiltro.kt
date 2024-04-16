package com.documentos.wms_beirario.model.separation.filtros

data class BodyEstantesFiltro(
    val listaandares: List<String>?,
    val listatiposdocumentos: List<String>?,
    val listatransportadoras: List<String>?
)