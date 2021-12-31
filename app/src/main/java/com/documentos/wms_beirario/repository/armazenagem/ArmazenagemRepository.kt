package com.documentos.wms_beirario.repository.armazenagem

import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.model.armazenagem.ArmazemRequestFinish

class ArmazenagemRepository constructor(private val serviceApi: ServiceApi) {

   suspend fun getArmazens() =
        this.serviceApi.Armazenagemget1()

    //FINISH ARMAZENAGEM -->
    suspend fun finishArmazenagem(armazemRequestFinish : ArmazemRequestFinish) =
        this
            .serviceApi.armazenagemPostFinish2(armazemRequestFinish = armazemRequestFinish)
}