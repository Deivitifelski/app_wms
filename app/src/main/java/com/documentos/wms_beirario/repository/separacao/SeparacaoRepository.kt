package com.documentos.wms_beirario.repository.separacao

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.separation.SeparationEnd
import com.documentos.wms_beirario.model.separation.SeparationListCheckBox

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
