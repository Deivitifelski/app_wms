package com.documentos.wms_beirario.model.inventario
data class CreateVoidPrinter(
    val codigoCorrugado: Int,
    val combinacoes: MutableList<Combinacoes>?
)

data class Combinacoes(
    val cabedal: Int,
    val cor: Int,
    val linha: Int,
    val referencia: Int,
    var quantidadePares: Int? = null,
    var corrugado: Int? = null,
    val distribuicao: List<Distribuicao>
)
data class Distribuicao(
    var tamanho: String,
    var quantidade: Int
)