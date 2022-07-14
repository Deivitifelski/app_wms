package com.documentos.wms_beirario.repository.mountingvol

import com.documentos.wms_beirario.data.RetrofitClient

class MountingVolRepository() {

    //1
    suspend fun getApi() = RetrofitClient().getClient().getMountingTask01()

    //2
    suspend fun getVolMounting2(kitProd: String) =
        RetrofitClient().getClient().returnNumSerieMounting2(idProdutoKit = kitProd)

    //3
    suspend fun getAndressMounting2(idOrdemMontagemVolume: String) =
        RetrofitClient().getClient()
            .returnAndressMounting2(idOrdemMontagemVolume = idOrdemMontagemVolume)
}