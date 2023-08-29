package com.documentos.wms_beirario.repository.armazens

import com.documentos.wms_beirario.data.RetrofitClient

class ArmazensRepository {

    private fun getService() = RetrofitClient().getClient()

    suspend fun getArmazens(token: String) = getService().getArmazens(token = token)
}