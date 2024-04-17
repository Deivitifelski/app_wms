package com.documentos.wms_beirario.model.separation.filtros

data class BodyEnderecosFiltro(
    val listaandares: List<String>,
    val listaestantes: List<String>,
    val listatiposdocumentos: List<String>,
    val listatransportadoras: List<String>
)