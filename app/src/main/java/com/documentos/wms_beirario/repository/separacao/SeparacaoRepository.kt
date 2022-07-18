package com.documentos.wms_beirario.repository.separacao

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.separation.SeparationEnd
import com.documentos.wms_beirario.model.separation.SeparationListCheckBox
import com.documentos.wms_beirario.model.separation.bodySeparation3

class SeparacaoRepository() {


    //1
    suspend fun getItemsSeparation() =
        RetrofitClient().getClient().getItemsSeparation()

    //1 -2 -> GET ANDARES
    suspend fun getItemAndares() =
        RetrofitClient().getClient().getAndaresSeparation()


    //2
    suspend fun postListCheckBox(
        separationItensCheck: SeparationListCheckBox
    ) = RetrofitClient().getClient().postListCheckBox(
        separationListCheckBox = separationItensCheck,
    )

    //3
    suspend fun postSeparationEnd(
        separationEnd: SeparationEnd
    ) = RetrofitClient().getClient().postSeparationEnd(separationEnd = separationEnd)

    //4
    suspend fun getProdAndress(
        estante: String,
        idEnderecoOrigem: String
    ) = RetrofitClient().getClient()
        .getSeparaProdAndress(estante = estante, idEnderecoOrigem = idEnderecoOrigem)

    //5
    suspend fun postSepProdAndress(
        bodySeparation3: bodySeparation3
    ) = RetrofitClient().getClient().postSepProdAndress(bodySeparation3 = bodySeparation3)
}
