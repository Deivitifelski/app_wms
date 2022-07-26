package com.documentos.wms_beirario.ui.separacao.activity

import com.documentos.wms_beirario.model.separation.ItensResponse1
import com.documentos.wms_beirario.model.separation.ResponseGetAndaresSeparationItem

class DataMock {

    companion object {
        fun returnData(): List<ItensResponse1> {
            val list = mutableListOf<ItensResponse1>()
            val andares = mutableListOf<ResponseGetAndaresSeparationItem>()
            andares.add(ResponseGetAndaresSeparationItem("A", 2, "ARM", "A"))
            andares.add(ResponseGetAndaresSeparationItem("B", 2, "ARM", "B"))
            andares.add(ResponseGetAndaresSeparationItem("C", 2, "ARM", "C"))
            andares.add(ResponseGetAndaresSeparationItem("D", 2, "ARM", "D"))
            andares.add(ResponseGetAndaresSeparationItem("E", 2, "ARM", "E"))


            list.add(
                ItensResponse1(
                    "001", 1, "ARM", "B", false,
                    andares as ArrayList<ResponseGetAndaresSeparationItem>
                )
            )
            list.add(
                ItensResponse1(
                    "001", 1, "ARM", "C", false,
                    andares as ArrayList<ResponseGetAndaresSeparationItem>
                )
            )
            list.add(
                ItensResponse1(
                    "001", 1, "ARM", "D", false,
                    andares as ArrayList<ResponseGetAndaresSeparationItem>
                )
            )
            list.add(
                ItensResponse1(
                    "001", 1, "ARM", "E", false,
                    andares as ArrayList<ResponseGetAndaresSeparationItem>
                )
            )
            list.add(
                ItensResponse1(
                    "001", 1, "ARM", "E", false,
                    andares as ArrayList<ResponseGetAndaresSeparationItem>
                )
            )
            list.add(
                ItensResponse1(
                    "001", 1, "ARM", "E", false,
                    andares as ArrayList<ResponseGetAndaresSeparationItem>
                )
            )
            list.add(
                ItensResponse1(
                    "001", 1, "ARM", "E", false,
                    andares as ArrayList<ResponseGetAndaresSeparationItem>
                )
            )

            return list
        }

    }
}
