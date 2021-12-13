package com.documentos.wms_beirario.repository.mountingvol

import com.documentos.wms_beirario.data.ServiceApi

class MountingVolRepository(private val mService: ServiceApi) {

    suspend fun getApi() = this.mService.getMountingTask01()
}