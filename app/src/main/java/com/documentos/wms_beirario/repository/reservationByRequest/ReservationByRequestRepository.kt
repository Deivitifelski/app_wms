package com.documentos.wms_beirario.repository.reservationByRequest

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.reservationByRequest.BodyAddReservation1
import com.documentos.wms_beirario.model.reservationByRequest.BodyAddVolReservationByRequest

class ReservationByRequestRepository {

    suspend fun postAddPedido(bodyAddReservation1: BodyAddReservation1) =
        RetrofitClient().getClient().postAddPedido(body = bodyAddReservation1)


    suspend fun postAddVolume(body2: BodyAddVolReservationByRequest) =
        RetrofitClient().getClient().postAddVolume(body = body2)

}