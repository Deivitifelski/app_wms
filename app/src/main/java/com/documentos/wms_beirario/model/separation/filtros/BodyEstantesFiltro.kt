package com.documentos.wms_beirario.model.separation.filtros

data class BodyEstantesFiltro(
    val listaandares: List<String>?,
    val listatiposdocumentos: List<String?>? = listOf(null),
    val listatransportadoras: List<String?>? = listOf(null)
)