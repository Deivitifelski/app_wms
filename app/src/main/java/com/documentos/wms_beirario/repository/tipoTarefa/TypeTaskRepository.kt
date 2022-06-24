package com.documentos.wms_beirario.repository.tipoTarefa

import com.documentos.wms_beirario.data.RetrofitClient

class TypeTaskRepository() {

    private fun getServiceApi() = RetrofitClient().getClient()
    suspend fun getTarefas(idArmazem: Int, token: String) =
        getServiceApi().getTipoTarefa(id_armazem = idArmazem, token = token)

}