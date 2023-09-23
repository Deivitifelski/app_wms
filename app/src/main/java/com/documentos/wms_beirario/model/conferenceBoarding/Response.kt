package com.documentos.wms_beirario.model.conferenceBoarding


class ResponseConferenceBoarding : ArrayList<ResponseConferenceBoardingItem>()

data class ResponseConferenceBoardingItem(
    var chaveAcesso: String,
    var filial: String,
    var idTarefa: String,
    var nfNumero: Int,
    var nfSerie: String,
    var requisicao: String,
    var pedido: String? = "",
    var listNotApointed: List<DataResponseBoarding>,
    var listApointed: List<DataResponseBoarding>
)


data class DataResponseBoarding(
    var pedido: String? = "",
    var idTarefa: String,
    var dataHoraAlteracao: String,
    var idEnderecoOrigem: Int,
    var numeroSerie: String? = null,
    var quantidade: Int,
    var ean: String,
    var sequencial: Int,
    var situacao: String,
    var sku: String,
    var usuarioAlteracao: String
)