package com.documentos.wms_beirario.model.receiptproduct


//leitura add recbimento 01 -->
data class QrCodeReceipt1 (val codigoBarras : String)

//Valida login acesso -->
data class PosLoginValidadREceipPorduct(
    val usuario: String,
    val senha: String
)