package com.documentos.wms_beirario.model.mountingVol

import java.io.Serializable

data class MountingTaskResponse1(
    val idProdutoKit: Int,
    val nome: String,
    val quantidade: Int
) : Serializable


//2
class ResponseMounting2 : ArrayList<ResponseMounting2Item>()

data class ResponseMounting2Item(
    val descricaoEmbalagem: String,
    val distribuicao: Int,
    val idOrdemMontagemVolume: String,
    val numeroSerie: String
) : Serializable

//3
class ResponseAndressMonting3 : ArrayList<ResponseAndressMonting3Item>()

data class ResponseAndressMonting3Item(
    val codigoBarras: String,
    val enderecoVisual: String,
    val idEnderecoOrigem: Int,
    val quantidadeProdutos: Int
)