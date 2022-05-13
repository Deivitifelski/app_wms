package com.documentos.wms_beirario.repository.consultacodbarras

import com.documentos.wms_beirario.data.RetrofitClient


class ConsultaCodBarrasRepository {

    suspend fun getCodBarras(codigoBarras: String) =
        RetrofitClient().getClient().getCodBarras(codigoBarras = codigoBarras)
}