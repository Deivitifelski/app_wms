package com.documentos.wms_beirario.model.movimentacaoentreenderecos

import java.io.Serializable

data class MovementFinishAndress(
    val idTarefa: String,
    val codigoBarrasEndereco: String
) : Serializable


data class MovementAddTask(
    val idTarefa: String,
    val numeroSerie: String
) : Serializable