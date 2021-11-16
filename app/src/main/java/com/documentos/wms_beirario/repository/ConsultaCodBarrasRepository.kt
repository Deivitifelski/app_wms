package com.documentos.wms_beirario.repository

import com.documentos.wms_beirario.data.RetrofitService

class ConsultaCodBarrasRepository constructor(private val mRetrofitService: RetrofitService) {

    suspend fun getCodBarras(id_armazem: Int, token: String,codBarras:String) = this.mRetrofitService.getCodBarras(codBarras,id_armazem,token)
}