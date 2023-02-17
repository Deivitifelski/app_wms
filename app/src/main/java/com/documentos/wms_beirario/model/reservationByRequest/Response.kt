package com.documentos.wms_beirario.model.reservationByRequest

//Resposta reserva por pedido adicionar -->
data class ResponseReservationPed1(
    var dataInclusao: String? = null,
    var idArmazem: Int? = null,
    var nomeCliente: String? = null,
    var nomeProduto: String? = null,
    var numeroNormativa: Int? = null,
    var numeroPedido: Int? = null,
    var quantidade: Int? = null,
    var situacaoReserva: String? = null
)

//Responta ao adicionar um volume -->
data class ResponseReservationByPedido2(
    var dataInclusao: String,
    var enderecoReserva: String,
    var idProduto: Int,
    var numeroPedido: Int,
    var numeroSerie: String,
    var sku: String
)