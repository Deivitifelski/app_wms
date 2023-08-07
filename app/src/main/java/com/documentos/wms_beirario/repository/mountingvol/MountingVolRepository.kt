package com.documentos.wms_beirario.repository.mountingvol

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.mountingVol.RequestMounting5
import com.documentos.wms_beirario.model.mountingVol.RequestMounting6

class MountingVolRepository() {

    //1
    suspend fun getApi(idArmazem: Int, token: String) =
        RetrofitClient().getClient().getMountingTask01(idArmazem, token)

    //1-2
    suspend fun getApiPrinterMounting(
        idOrdemMontagemVolume: String,
        idArmazem: Int,
        token: String
    ) = RetrofitClient().getClient()
        .getPrinterMounting(
            idOrdemMontagemVolume = idOrdemMontagemVolume,
            idArmazem = idArmazem,
            token = token
        )

    //2
    suspend fun getVolMounting2(kitProd: String, idArmazem: Int, token: String) =
        RetrofitClient().getClient()
            .returnNumSerieMounting2(idProdutoKit = kitProd, idArmazem = idArmazem, token = token)

    //3
    suspend fun getAndressMounting3(idOrdemMontagemVolume: String, idArmazem: Int, token: String) =
        RetrofitClient().getClient()
            .returnAndressMounting2(
                idOrdemMontagemVolume = idOrdemMontagemVolume,
                idArmazem = idArmazem,
                token = token
            )

    //4
    suspend fun getProdMounting4(
        idOrdemMontagemVolume: String,
        idEnderecoOrigem: String,
        idArmazem: Int,
        token: String
    ) =
        RetrofitClient().getClient()
            .returnProdMounting4(
                idOrdemMontagemVolume = idOrdemMontagemVolume,
                idEnderecoOrigem = idEnderecoOrigem,
                idArmazem = idArmazem, token = token
            )

    //5
    suspend fun addProdEan5(body5: RequestMounting5, idArmazem: Int, token: String) =
        RetrofitClient().getClient()
            .addProdEanMounting5(bodyMounting5 = body5, idArmazem = idArmazem, token = token)

    //6 set reimpressão unica -->
    suspend fun setImpressaoUnica(body: RequestMounting6, idArmazem: Int, token: String) =
        RetrofitClient().getClient().setImpressaoUnica(
            body = body,
            idArmazem = idArmazem, token = token
        )
}