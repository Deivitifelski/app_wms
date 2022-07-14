package com.documentos.wms_beirario.repository.mountingvol

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.mountingVol.RequestMounting5

class MountingVolRepository() {

    //1
    suspend fun getApi() = RetrofitClient().getClient().getMountingTask01()

    //2
    suspend fun getVolMounting2(kitProd: String) =
        RetrofitClient().getClient().returnNumSerieMounting2(idProdutoKit = kitProd)

    //3
    suspend fun getAndressMounting3(idOrdemMontagemVolume: String) =
        RetrofitClient().getClient()
            .returnAndressMounting2(idOrdemMontagemVolume = idOrdemMontagemVolume)

    //4
    suspend fun getProdMounting4(idOrdemMontagemVolume: String, idEnderecoOrigem: String) =
        RetrofitClient().getClient()
            .returnProdMounting4(
                idOrdemMontagemVolume = idOrdemMontagemVolume,
                idEnderecoOrigem = idEnderecoOrigem
            )

    //5
    suspend fun addProdEan5(body5: RequestMounting5) =
        RetrofitClient().getClient().addProdEanMounting5(bodyMounting5 = body5)
}