package com.documentos.wms_beirario.repository.separacao

import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.model.separation.SeparationEnd
import com.documentos.wms_beirario.model.separation.SeparationListCheckBox

class SeparacaoRepository(private var mServiceApi: ServiceApi) {


    suspend fun getItemsSeparation() =
        this.mServiceApi.getItemsSeparation()


    suspend fun postListCheckBox(
        separationItensCheck: SeparationListCheckBox
    ) = this.mServiceApi.postListCheckBox(
        separationListCheckBox = separationItensCheck,

        )

    suspend fun postSeparationEnd(
        separationEnd: SeparationEnd
    ) = this.mServiceApi.postSeparationEnd(separationEnd =  separationEnd)
}
