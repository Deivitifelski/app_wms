package com.documentos.wms_beirario.repository.desmontagemvolumes

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.desmontagemVol.RequestDisassamblyVol


class DisassemblyRepository() {

    suspend fun getDisassembly1() = RetrofitClient().getClient().getReturnTaskUnmountingVol1()

    suspend fun getDisassembly2(idEndereco: Int) =
        RetrofitClient().getClient().getReturnVolQntsUnmountingVol2(idEndereco = idEndereco)

    suspend fun postDisassembly3(body: RequestDisassamblyVol) =
        RetrofitClient().getClient()
            .postDisassembleVol3(requestDisassamblyVol = body)
}
