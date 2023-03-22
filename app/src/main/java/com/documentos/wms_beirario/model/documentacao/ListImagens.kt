package com.documentos.wms_beirario.model.documentacao


data class ListImagens(
    val list: List<Int>,
    val namePage: String,
    val page: Int? = 0
) : java.io.Serializable
