package com.documentos.wms_beirario.repository.auditoriaEstoque

import com.documentos.wms_beirario.data.RetrofitClient

class AuditoriaEstoqueRepository {

    private fun getService() = RetrofitClient().getClient()

    suspend fun getListaAuditoriasEstoque1(idArmazem: Int, token: String) =
        getService().getListaAuditoriasEstoque1(idArmazem = idArmazem, token = token)

    suspend fun getListEstantes(idArmazem: Int, token: String, idAuditoriaEstoque: String) =
        getService().getListEstantesAuditoriaEstoque2(
            idArmazem = idArmazem,
            token = token,
            idAuditoriaEstoque = idAuditoriaEstoque
        )

    suspend fun getListEnderecos(
        idArmazem: Int,
        token: String,
        idAuditoriaEstoque: String,
        estante: String
    ) =
        getService().getListEnderecosAuditoriaEstoque3(
            idArmazem = idArmazem,
            token = token,
            idAuditoriaEstoque = idAuditoriaEstoque,
            estante = estante
        )

    suspend fun getProdutoAndress(
        idArmazem: Int,
        token: String,
        idAuditoriaEstoque: String,
        idEndereco: Int
    ) = getService().getListProdutosAuditoriaEstoque4(
        idArmazem = idArmazem,
        token = token,
        idAuditoriaEstoque = idAuditoriaEstoque,
        idEndereco = idEndereco
    )


}