package com.documentos.wms_beirario.model.receiptproduct


//leitura add recbimento 01 -->
data class QrCodeReceipt1 (val codigoBarras : String)

//Valida login acesso 02 -->
data class PosLoginValidadREceipPorduct(
    val usuario: String,
    val senha: String
)

//Finalizando tarefa 03 -->
data class PostFinishReceiptProduct3(
    val codigoBarrasEndereco: String,
    val idTarefa: String,
    val itens: List<ListFinishReceiptProduct3>
)

data class ListFinishReceiptProduct3(
    val numeroSerie: String,
    val sequencial: String
)