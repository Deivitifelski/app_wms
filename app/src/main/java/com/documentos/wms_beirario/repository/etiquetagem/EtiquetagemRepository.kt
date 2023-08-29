package com.documentos.wms_beirario.repository.etiquetagem

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequest1
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequestModel3

class EtiquetagemRepository() {

    suspend fun labelingPost1(
        etiquetagemRequest1: EtiquetagemRequest1,
        idArmazem: Int,
        token: String
    ) =
        RetrofitClient().getClient().postEtiquetagem1(
            etiquetagempost1 = etiquetagemRequest1,
            idArmazem = idArmazem,
            token = token
        )

    suspend fun labelingGet2() = RetrofitClient().getClient().etiquetagemGet2()

    suspend fun labelingget3(etiquetagemRequestModel3: EtiquetagemRequestModel3) =
        RetrofitClient().getClient()
            .postEtiquetagem3(etiquetagemRequestModel3 = etiquetagemRequestModel3)

    suspend fun labelinggetNf() = RetrofitClient().getClient().getetiquetagempedNf()

    suspend fun labelinggetOnda() = RetrofitClient().getClient().getPendenciaOnda()

    suspend fun labelinggetRequisicao() = RetrofitClient().getClient().getPendenciaRequisicao()
}