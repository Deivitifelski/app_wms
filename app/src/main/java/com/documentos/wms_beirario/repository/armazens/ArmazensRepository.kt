package com.documentos.wms_beirario.repository.armazens

import com.documentos.wms_beirario.data.ServiceApi

class ArmazensRepository constructor(private val serviceApi: ServiceApi) {

    suspend fun getArmazens() = this.serviceApi.getArmazens()
}