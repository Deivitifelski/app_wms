package com.documentos.wms_beirario.model.reservationByRequest

//Adicionar pedido 1 -->
data class BodyAddReservation1(
    var codPedido: String
)


//Adiciona volume 2 -->
data class BodyAddVolReservationByRequest(
    var codPedido: String,
    var numSerie: String
)