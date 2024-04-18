package com.documentos.wms_beirario.model.separation.filtros

data class BodyProdutoSeparacao(
    val codbarrasendereco: String,
    val listatiposdocumentos: List<String?>,
    val listatransportadoras: List<String?>,
)

