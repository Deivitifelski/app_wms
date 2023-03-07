package com.documentos.wms_beirario.model.reservationByRequest

//Resposta reserva por pedido adicionar pedido 1 -->

data class ReservationRequetsResponse1(
    var armazem: Int,
    var dataHoraInclusao: String,
    var itens: List<VolumesReservedRequest>,
    var nomeCliente: String,
    var quantidade: Int,
    var quantidadeApontada: Int,
    var normativa: Int,
    var pedido: Int
)

data class VolumesReservedRequest(
    var armazem: Int,
    var dataHoraInclusao: String,
    var endereco: String,
    var numeroSerie: String,
    var pedido: Int,
    var sku: String
)

data class ListVolumerServPed1(
    var dataInclusaoVolume: String,
    var endereco: String,
    var numeroserie: String,
    var situacao: String? = null,
    var sku: String
)

//Responta ao adicionar um volume 2 -->
data class ResponseReservationByRequest2(
    var cliente: String,
    var dataInclusao: String,
    var normativa: Int,
    var pedido: Int,
    var quantidade: Int,
    var situacao: String,
    var volumes: List<ListVolumerServPed2>
)

data class ListVolumerServPed2(
    var dataInclusaoVolume: String,
    var endereco: String,
    var numeroserie: String,
    var sku: String
)