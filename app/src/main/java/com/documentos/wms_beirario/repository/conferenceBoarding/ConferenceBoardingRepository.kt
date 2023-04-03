package com.documentos.wms_beirario.repository.conferenceBoarding

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.conferenceBoarding.BodyChaveBoarding

class ConferenceBoardingRepository {

    /** 1 - Busca as lista de tarefas, já com os itens apontados e não apontados --> */
    suspend fun postConferenceBoarding1(bodyChaveBoarding: BodyChaveBoarding) =
        RetrofitClient().getClient().postListTaskEmbarque(body = bodyChaveBoarding)
}