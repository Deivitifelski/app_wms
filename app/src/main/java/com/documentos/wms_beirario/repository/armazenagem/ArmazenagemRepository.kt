package com.documentos.wms_beirario.repository.armazenagem

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.armazenagem.ArmazemRequestFinish

class ArmazenagemRepository() {
    private fun getServ() = RetrofitClient().getClient()
    suspend fun getArmazens() = getServ().Armazenagemget1()

    //FINISH ARMAZENAGEM -->
    suspend fun finishArmazenagem(armazemRequestFinish: ArmazemRequestFinish) =
        getServ().armazenagemPostFinish2(armazemRequestFinish = armazemRequestFinish)
}