package com.documentos.wms_beirario.repository.etiquetagem

import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequest1
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequestModel3

class EtiquetagemRepository(private val serviceApi: ServiceApi) {

    suspend fun labelingPost1(etiquetagemRequest1: EtiquetagemRequest1) =
        this.serviceApi.postEtiquetagem1(etiquetagempost1 = etiquetagemRequest1)

    suspend fun labelingGet2() = this.serviceApi.etiquetagemGet2()

    suspend fun labelingget3(etiquetagemRequestModel3: EtiquetagemRequestModel3) =
        this.serviceApi.postEtiquetagem3(etiquetagemRequestModel3 = etiquetagemRequestModel3)

    suspend fun labelinggetNf() = this.serviceApi.getetiquetagempedNf()
}