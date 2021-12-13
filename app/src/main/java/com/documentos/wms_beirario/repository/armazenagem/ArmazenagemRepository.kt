package com.documentos.wms_beirario.repository.armazenagem

import com.documentos.wms_beirario.data.ServiceApi

class ArmazenagemRepository constructor(private val serviceApi: ServiceApi) {

   suspend fun getArmazens() =
        this.serviceApi.getArmazenagem()
}