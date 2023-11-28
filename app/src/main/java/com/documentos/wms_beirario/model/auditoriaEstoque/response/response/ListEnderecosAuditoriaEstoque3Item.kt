package com.documentos.wms_beirario.model.auditoriaEstoque.response.response

import java.io.Serializable

data class ListEnderecosAuditoriaEstoque3Item(
    val codigoBarrasEndereco: String,
    val enderecoVisual: String,
    val idEndereco: Int,
    val quantidadePares: Int,
    val quantidadeVolumes: Int,
    val contagem: Int,
) : Serializable

