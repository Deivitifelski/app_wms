package com.documentos.wms_beirario.model.mountingVol

import java.io.Serializable

data class MountingTaskResponse1(
    val idProdutoKit: Int,
    val nome: String,
    val quantidade: Int
) : Serializable