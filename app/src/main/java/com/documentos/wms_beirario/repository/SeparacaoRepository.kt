package com.documentos.wms_beirario.repository

import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.model.separation.SeparationEnd
import com.documentos.wms_beirario.model.separation.SeparationListCheckBox

class SeparacaoRepository(private var mRetrofitService: RetrofitService) {


    suspend fun getItemsSeparation() =
        this.mRetrofitService.getItemsSeparation()


    suspend fun postListCheckBox(
        separationItensCheck: SeparationListCheckBox
    ) = this.mRetrofitService.postListCheckBox(
        separationListCheckBox = separationItensCheck,

        )

    suspend fun postSeparationEnd(
        separationEnd: SeparationEnd
    ) = this.mRetrofitService.postSeparationEnd(separationEnd =  separationEnd)
}
