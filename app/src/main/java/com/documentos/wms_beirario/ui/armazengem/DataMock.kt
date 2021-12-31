package com.documentos.wms_beirario.ui.armazengem

import com.documentos.wms_beirario.model.armazenagem.ArmazenagemResponse

class DataMock {

    companion object {

        fun returnArmazens(): ArrayList<ArmazenagemResponse> {
            val list = ArrayList<ArmazenagemResponse>()
            list.add(
                ArmazenagemResponse(
                    "1", 1, 1,
                    "12345678910", "arm",
                    "OAAP-A-011", 1,
                    1, "p632946349694",
                    1, "ARM0019203", 1
                )
            )
            //2 ->
            list.add(
                ArmazenagemResponse(
                    "1", 1, 1,
                    "12345678911", "arm",
                    "OAAP-B-111", 1,
                    1, "p632946349694",
                    1, "ARM0019222", 1
                )
            )
            //2 ->
            list.add(
                ArmazenagemResponse(
                    "1", 1, 1,
                    "12345678912", "arm",
                    "OAAP-A-999", 1,
                    1, "p632946349694",
                    1, "ARM0011111", 1
                )
            )
            //4 ->
            list.add(
                ArmazenagemResponse(
                    "1", 1, 1,
                    "12345678913", "arm",
                    "OAAP-A-999", 1,
                    1, "p632946349694",
                    1, "ARM00008344", 1
                )
            )
            //5 ->
            list.add(
                ArmazenagemResponse(
                    "1", 1, 1,
                    "12345678914", "arm",
                    "OAAP-A-999", 1,
                    1, "p632946349694",
                    1, "ARM00009872", 1
                )
            )
            return list
        }
    }
}