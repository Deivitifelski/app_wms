package com.documentos.wms_beirario.repository.conferenceBoarding

import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.model.conferenceBoarding.BodyChaveBoarding
import com.documentos.wms_beirario.model.conferenceBoarding.BodySetBoarding

class ConferenceBoardingRepository {

    /** 1 - Busca as lista de tarefas, já com os itens apontados e não apontados --> */
    suspend fun postConferenceBoarding1(
        bodyChaveBoarding: BodyChaveBoarding,
        token: String,
        idArmazem: Int
    ) =
        RetrofitClient().getClient()
            .postListTaskEmbarque(body = bodyChaveBoarding, token = token, idArmazem = idArmazem)

    /** 2 - Seta itens para aprovados na listagem da tela  --> */
    suspend fun postSetaApproved2(
        bodyChaveBoarding: BodySetBoarding,
        token: String,
        idArmazem: Int
    ) =
        RetrofitClient().getClient()
            .postSetaApproved(body = bodyChaveBoarding, token = token, idArmazem = idArmazem)

    /** 3 - Seta itens para reprovados na listagem da tela  --> */
    suspend fun postSetaDisapproved3(
        bodyChaveBoarding: BodySetBoarding,
        token: String,
        idArmazem: Int
    ) =
        RetrofitClient().getClient()
            .postSetaDisapproved(body = bodyChaveBoarding, token = token, idArmazem = idArmazem)
}