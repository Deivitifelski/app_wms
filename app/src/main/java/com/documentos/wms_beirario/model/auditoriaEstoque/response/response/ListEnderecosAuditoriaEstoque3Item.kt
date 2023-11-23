package com.documentos.wms_beirario.model.auditoriaEstoque.response.response

data class ListEnderecosAuditoriaEstoque3Item(
    val codigoBarrasEndereco: String,
    val enderecoVisual: String,
    val idEndereco: Int,
    val quantidadePares: Int,
    val quantidadeVolumes: Int
)