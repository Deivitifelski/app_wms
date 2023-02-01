package com.documentos.wms_beirario.model.movimentacaoentreenderecos

import java.io.Serializable

data class MovementFinishAndress(
    val idTarefa: String,
    val codigoBarrasEndereco: String
) : Serializable


data class MovementAddProduct(
    val p_id_end_origem: Long,
    val p_id_tarefa: String,
    val p_codigo_barras: String
) : Serializable

data class BodyMov1(
    val filtrarOperador: Boolean
) : Serializable

//BODY LER ENDEREÇO -->
data class RequestReadingAndressMov2(
    var codEndOrigem: String
)

data class RequestAddProductMov3(
    var codBarras: String,
    var idEndOrigem: Int,
    var idTarefa: String
)