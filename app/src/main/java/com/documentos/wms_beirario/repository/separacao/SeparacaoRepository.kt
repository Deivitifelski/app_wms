package com.documentos.wms_beirario.repository.separacao

import com.documentos.appwmsbeirario.model.separation.SeparationListCheckBox
import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.separation.SeparationEnd

class SeparacaoRepository() {


    suspend fun getItemsSeparation() =
        RetrofitClient().getClient().getItemsSeparation()


    suspend fun postListCheckBox(
        separationItensCheck: SeparationListCheckBox
    ) = RetrofitClient().getClient().postListCheckBox(
        separationListCheckBox = separationItensCheck,

        )

    suspend fun postSeparationEnd(
        separationEnd: SeparationEnd
    ) = RetrofitClient().getClient().postSeparationEnd(separationEnd = separationEnd)
}
