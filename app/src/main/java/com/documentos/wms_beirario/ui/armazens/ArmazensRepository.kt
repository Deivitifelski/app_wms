package com.documentos.wms_beirario.ui.armazens

import com.documentos.wms_beirario.data.RetrofitService

class ArmazensRepository constructor(private val retrofitService: RetrofitService) {

    fun getArmazens(token:String) = this.retrofitService.getArmazens(token)
}