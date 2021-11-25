package com.documentos.wms_beirario.repository

import com.documentos.wms_beirario.data.RetrofitService

class ConsultaCodBarrasRepository constructor(private val mRetrofitService: RetrofitService) {

    suspend fun getCodBarras(codigoBarras: String) =
        this.mRetrofitService.getCodBarras(codigoBarras = codigoBarras)
}