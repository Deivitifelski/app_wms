package com.documentos.wms_beirario.repository.desmontagemvolumes

import com.documentos.wms_beirario.data.ServiceApi

class DisassemblyRepository(private val mService:ServiceApi) {


   suspend fun getDisassembly1() = this.mService.getDisassembly1()
}