package com.documentos.wms_beirario.repository

import com.documentos.wms_beirario.data.RetrofitService

class ArmazenagemRepository constructor(private val retrofitService: RetrofitService) {

    fun getArmazens(id_armazem: Int, token: String) =
        this.retrofitService.getArmazenagem(id_armazem, token)
}