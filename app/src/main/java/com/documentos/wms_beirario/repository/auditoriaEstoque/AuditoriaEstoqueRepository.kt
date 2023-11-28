package com.documentos.wms_beirario.repository.auditoriaEstoque

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.auditoriaEstoque.response.request.BodyApontEndProdutoAuditoriaEstoque
import com.documentos.wms_beirario.model.auditoriaEstoque.response.request.BodyApontEndQtdAuditoriaEstoque

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

    suspend fun getProdutoAndressAP(
        idArmazem: Int,
        token: String,
        idAuditoriaEstoque: String,
        idEndereco: Int
    ) = getService().getListProdutosAuditoriaEstoqueAP(
        idArmazem = idArmazem,
        token = token,
        idAuditoriaEstoque = idAuditoriaEstoque,
        idEndereco = idEndereco
    )


    suspend fun apontaProduto(
        idArmazem: Int,
        token: String,
        contagem: String,
        idAuditoriaEstoque: String,
        idEndereco: String,
        body: BodyApontEndProdutoAuditoriaEstoque
    ) = getService().postApontEnderecoProdutoAp(
        idArmazem = idArmazem,
        token = token,
        contagem = contagem,
        idAuditoriaEstoque = idAuditoriaEstoque,
        idEndereco = idEndereco,
        body = body
    )

    suspend fun validaContagem(
        idArmazem: Int,
        token: String,
        contagem: Int,
        idAuditoriaEstoque: String,
        idEndereco: Int
    ) = getService().postValidaContagem(
        idArmazem = idArmazem,
        token = token,
        contagem = contagem.toString(),
        idAuditoriaEstoque = idAuditoriaEstoque,
        idEndereco = idEndereco.toString(),
    )

    suspend fun saveEnderecoQtd(
        idEndereco: Int,
        token: String,
        contagem: String,
        idAuditoriaEstoque: String,
        idArmazem: Int,
        body: BodyApontEndQtdAuditoriaEstoque
    ) = getService().postApontEnderecoQtd(
        idArmazem = idArmazem,
        token = token,
        contagem = contagem,
        idAuditoriaEstoque = idAuditoriaEstoque,
        idEndereco = idEndereco.toString(),
        body = body
    )


}