package com.documentos.wms_beirario.repository.etiquetagem

import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequest1
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequestModel3

class EtiquetagemRepository(private val retrofitService: RetrofitService) {

    suspend fun labelingPost1(etiquetagemRequest1: EtiquetagemRequest1) =
        this.retrofitService.postEtiquetagem1(etiquetagempost1 = etiquetagemRequest1)

    suspend fun labelingGet2() = this.retrofitService.etiquetagemGet2()

    suspend fun labelingget3(etiquetagemRequestModel3: EtiquetagemRequestModel3) =
        this.retrofitService.postEtiquetagem3(etiquetagemRequestModel3 = etiquetagemRequestModel3)
}