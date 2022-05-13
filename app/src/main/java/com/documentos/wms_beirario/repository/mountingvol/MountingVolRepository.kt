package com.documentos.wms_beirario.repository.mountingvol

import com.documentos.wms_beirario.data.RetrofitClient

class MountingVolRepository() {

    suspend fun getApi() = RetrofitClient().getClient().getMountingTask01()
}