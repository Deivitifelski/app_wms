package com.documentos.wms_beirario.repository

import com.documentos.wms_beirario.data.RetrofitService

class ArmazenagemRepository constructor(private val retrofitService: RetrofitService) {

    fun getArmazens() =
        this.retrofitService.getArmazenagem()
}