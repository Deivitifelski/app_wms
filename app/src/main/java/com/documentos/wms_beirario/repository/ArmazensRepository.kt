package com.documentos.wms_beirario.repository

import com.documentos.wms_beirario.data.RetrofitService

class ArmazensRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun getArmazens(token:String) = this.retrofitService.getArmazens(token)
}