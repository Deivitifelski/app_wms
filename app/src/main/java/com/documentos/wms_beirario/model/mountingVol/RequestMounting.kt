package com.documentos.wms_beirario.model.mountingVol

data class RequestMounting5(
    val idEnderecoOrigem: Int,
    val idOrdemMontagemVolume: String,
    val idProduto: Int
)

data class RequestMounting6(
    val statusImpressao: String = "S",
    val idOrdemMontagemVolume: String,
)