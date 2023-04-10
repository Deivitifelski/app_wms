package com.documentos.wms_beirario.model.conferenceBoarding


class ResponseConferenceBoarding : ArrayList<ResponseConferenceBoardingItem>()

data class ResponseConferenceBoardingItem(
    var chaveAcesso: String,
    var filial: String,
    var idTarefa: String,
    var listApointed: List<DataResponseBoarding>,
    var listNotApointed: List<DataResponseBoarding>,
    var nfNumero: Int,
    var nfSerie: String,
    var pedido: String? = ""
)


data class DataResponseBoarding(
    var dataHoraAlteracao: String,
    var idEnderecoOrigem: Int,
    var numeroSerie: String,
    var quantidade: Int,
    var sequencial: Int,
    var situacao: String,
    var sku: String,
    var usuarioAlteracao: String
)