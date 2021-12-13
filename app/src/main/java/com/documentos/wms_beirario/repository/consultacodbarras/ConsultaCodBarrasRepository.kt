package com.documentos.wms_beirario.repository.consultacodbarras

import com.documentos.wms_beirario.data.ServiceApi

class ConsultaCodBarrasRepository constructor(private val mServiceApi: ServiceApi) {

    suspend fun getCodBarras(codigoBarras: String) =
        this.mServiceApi.getCodBarras(codigoBarras = codigoBarras)
}