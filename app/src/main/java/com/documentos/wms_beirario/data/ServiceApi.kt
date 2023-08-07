package com.documentos.wms_beirario.data

import ArmazenagemResponse
import com.documentos.wms_beirario.model.armazenagem.ArmazemRequestFinish
import com.documentos.wms_beirario.model.armazens.ArmazensResponse
import com.documentos.wms_beirario.model.auditoria.BodyAuditoriaFinish
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoria1
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoriaEstantes2
import com.documentos.wms_beirario.model.auditoria.ResponseFinishAuditoria
import com.documentos.wms_beirario.model.codBarras.CodigodeBarrasResponse
import com.documentos.wms_beirario.model.conferenceBoarding.BodyChaveBoarding
import com.documentos.wms_beirario.model.conferenceBoarding.BodySetBoarding
import com.documentos.wms_beirario.model.conferenceBoarding.ResponseConferenceBoarding
import com.documentos.wms_beirario.model.desmontagemVol.RequestDisassamblyVol
import com.documentos.wms_beirario.model.desmontagemVol.ResponseUnMountingFinish
import com.documentos.wms_beirario.model.desmontagemVol.UnmountingVolumes1
import com.documentos.wms_beirario.model.etiquetagem.*
import com.documentos.wms_beirario.model.inventario.*
import com.documentos.wms_beirario.model.logPrinter.BodySaveLogPrinter
import com.documentos.wms_beirario.model.login.LoginRequest
import com.documentos.wms_beirario.model.login.LoginResponse
import com.documentos.wms_beirario.model.mountingVol.*
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.*
import com.documentos.wms_beirario.model.picking.*
import com.documentos.wms_beirario.model.qualityControl.*
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode2
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode3
import com.documentos.wms_beirario.model.recebimento.request.PostReciptQrCode1
import com.documentos.wms_beirario.model.recebimento.response.ReceiptDoc1
import com.documentos.wms_beirario.model.recebimento.response.ReceiptMessageFinish
import com.documentos.wms_beirario.model.receiptproduct.*
import com.documentos.wms_beirario.model.reimpressao.RequestEtiquetasReimpressaoBody
import com.documentos.wms_beirario.model.reimpressao.ResponseEtiquetasReimpressao
import com.documentos.wms_beirario.model.reimpressao.ResultReimpressaoDefault
import com.documentos.wms_beirario.model.reservationByRequest.*
import com.documentos.wms_beirario.model.separation.*
import com.documentos.wms_beirario.model.tipo_tarefa.TipoTarefaResponseItem
import retrofit2.Response
import retrofit2.http.*

interface ServiceApi {

    /**Controle de Acesso - Login -->*/
    @Headers("Content-Type: application/json")
    @POST("v1/auth/login")
    suspend fun postLogin(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    /**Armazém - Retornar armazens do operador -->*/
    @Headers("Content-Type: application/json")
    @GET("v1/armazem")
    suspend fun getArmazens(@Header("Authorization") token: String): Response<List<ArmazensResponse>>

    @GET("v1/armazem/{id_armazem}/tipoTarefa")
    @Headers("Content-Type: application/json")
    suspend fun getTipoTarefa(
        @Path("id_armazem") id_armazem: Int,
        @Header("Authorization") token: String
    ): Response<List<TipoTarefaResponseItem>>

    /**---------------------------------ARMAZENAGEM-----------------------------------------------*/
    //ARMAZENAGEM -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/armazenagem/tarefa/pendente")
    suspend fun Armazenagemget1(
        @Path("idArmazem") idarmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN
    ): Response<List<ArmazenagemResponse>>

    //ARMAZENAGEM FINALIZAR -->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/armazenagem/tarefa/finalizar")
    suspend fun armazenagemPostFinish2(
        @Path("idArmazem") idarmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body armazemRequestFinish: ArmazemRequestFinish
    ): Response<Unit>

    /**---------------------------------CONSULTA COD.BARRAS---------------------------------------*/
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/consulta/{codigoBarras}")
    suspend fun getCodBarras(
        @Path("idArmazem") idarmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Path("codigoBarras") codigoBarras: String
    ): Response<CodigodeBarrasResponse>

    /**---------------------------------SEPARAÇAO-----------------------------------------------*/
//1 -> NOVA -PRIMEIRO GET TRAZENDO OS ANDARES -->
    @Headers("Content-Type: application/json")
    @GET("v2/armazem/{idArmazem}/tarefa/separacao/andaresPendentes")
    suspend fun getAndaresSeparation(
        @Path("idArmazem") idarmazem: Int,
        @Header("Authorization") token: String,
    ): Response<List<ResponseSeparation1>>

    //2 -> NOVA - SEGUNDO POST ENVIANDO ARRAY DOS ANDAR SELECIONADOS TRÁS AS ESTANTES->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/separacao/estantesPendentes")
    suspend fun postBuscaEstantesSeparation(
        @Path("idArmazem") idarmazem: Int,
        @Header("Authorization") token: String,
        @Body bodyArrayAndarEstantes: RequestSeparationArraysAndares1
    ): Response<ResponseEstantes>

    //3 -> NOVA - ENVIANDO O ARRAY DE ESTANTES E ANDARES BUSCA ENDEREÇOS A SEPARAR -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/separacao/listaEnderecos")
    suspend fun postBuscaEnderecosSeparation(
        @Path("idArmazem") idarmazem: Int,
        @Header("Authorization") token: String,
        @Body bodyArrayAndarEstantes: RequestSeparationArraysAndaresEstante3
    ): Response<ResponseTarefasANdaresSEparation3>

    //4 - NOVA - FINALIZA SEPARAÇÃO SE O ARMAZEM FOR 100 -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/separacao/finalizaSeparacao")
    suspend fun postSeparationEnd(
        @Path("idArmazem") idArmazem: Int,
        @Header("Authorization") token: String,
        @Body separationEnd: SeparationEnd
    ): Response<Unit>

    //5 - NOVA - BUSCA PRODUTOS -->
    @Headers("Content-Type: application/json")
    @GET("v2/armazem/{idArmazem}/{codBarrasEndOrigem}/tarefa/separacao/produtosPendentes")
    suspend fun postBuscaProdutos(
        @Path("idArmazem") idArmazem: Int,
        @Header("Authorization") token: String,
        @Path("codBarrasEndOrigem") codBarrasEndOrigem: String,
    ): Response<SeparacaoProdAndress4>

    // 6 - SEPARA E ETIQUETA -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/{idEnderecoOrigem}/tarefa/separacao/etiquetaSepara")
    suspend fun postSepEtiquetarProdAndress(
        @Path("idArmazem") idArmazem: Int,
        @Path("idEnderecoOrigem") idEnderecoOrigem: Int,
        @Body bodySepararEtiquetar: BodySepararEtiquetar,
        @Header("Authorization") token: String,
    ): Response<ResponseEtiquetarSeparar>

    //VERSÃO ANTIGA BUSCA PRODUTOS -->
//    @Headers("Content-Type: application/json")
//    @GET("v1/armazem/{idArmazem}/tarefa/separacao/endereco/{idEnderecoOrigem}/produtos")
//    suspend fun getSeparaProdAndress(
//        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
//        @Path("idEnderecoOrigem") idProduto: String,
//        @Header("Authorization") token: String = TOKEN,
//    ): Response<SeparacaoProdAndress4>

    //versão BETA - Função de separar e etiquetar o volume ao mesmo tempo -->
//    @Headers("Content-Type: application/json")
//    @POST("v1/armazem/{idArmazem}/tarefa/separacao/etiquetagem/endereco/{idEnderecoOrigem}")
//    suspend fun postSepEtiquetarProdAndress(
//        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
//        @Path("idEnderecoOrigem") idEnderecoOrigem: String,
//        @Body bodySepararEtiquetar: BodySepararEtiquetar,
//        @Header("Authorization") token: String = TOKEN,
//    ): Response<ResponseEtiquetarSeparar>


    /**---------------------------------MOVIMENTAÇAO-------------------------------------------->*/
    //Faz Get das tarefas pendentes do operador ---->
    @Headers("Content-Type: application/json")
    @GET("v2/armazem/{idArmazem}/tarefa/movimentacao/pendente/operador")
    suspend fun movementShowMovements(
        @Path("idArmazem") idArmazem: Int,
        @Header("Authorization") token: String,
    ): Response<ResponseMovParesAvulso1>

    //CRIAR NOVA TAREFA ----->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/movimentacao/criar")
    suspend fun movementAddNewTask(
        @Path("idArmazem") idArmazem: Int,
        @Header("Authorization") token: String,
    ): Response<MovementNewTask>

    //LEITURA ENDEREÇO -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/movimentacao/apontarEndereco")
    suspend fun readingAndressMov2(
        @Path("idArmazem") idArmazem: Int,
        @Header("Authorization") token: String,
        @Body body: RequestReadingAndressMov2
    ): Response<ResponseReadingMov2>

    //ADICIONA PRODUTO -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/movimentacao/adicionarProduto")
    suspend fun addProductMov3(
        @Path("idArmazem") idArmazem: Int,
        @Header("Authorization") token: String,
        @Body body: RequestAddProductMov3
    ): Response<ResponseAddProductMov3>

    //FINALIZAR -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/movimentacao/finalizar")
    suspend fun finishTaskMov4(
        @Path("idArmazem") idArmazem: Int,
        @Header("Authorization") token: String,
        @Body body: RequestBodyFinalizarMov4
    ): Response<Unit>

    //CANCELAR TAREFA -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/movimentacao/cancelar")
    suspend fun cancelMov5(
        @Path("idArmazem") idArmazem: Int,
        @Header("Authorization") token: String,
        @Body body: BodyCancelMov5
    ): Response<ResponseCancelMov5>

    //MOVIMENTAÇAO  Retorna Itens Proxima Tela -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/movimentacao/{idTarefa}/itens/pendente")
    suspend fun movementgetRetornaItensMov2(
        @Path("idArmazem") idArmazem: Int,
        @Header("Authorization") token: String,
        @Path("idTarefa") idTarefa: String
    ): Response<List<MovementReturnItemClickMov>>

    //Adiciona Item a tarefa de movimentação -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/movimentacao/adicionar/produto")
    suspend fun movementAddItemMov(
        @Path("idArmazem") idArmazem: Int,
        @Header("Authorization") token: String,
        @Body movementAddProduct: MovementAddProduct
    ): Response<Unit>

    //Finish -->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/movimentacao/finalizar")
    suspend fun movementFinishMov(
        @Path("idArmazem") idArmazem: Int,
        @Header("Authorization") token: String,
        @Body postRequestModelFinish: MovementFinishAndress
    ): Response<Unit>


    /**-------------------------------INVENTARIO-------------------------------------------------*/
    //Inventario 1 - Retornar tarefas de inventario pendentes -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/inventario/abastecimento/pendente")
    suspend fun Inventorypending1(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<List<ResponseInventoryPending1>>

    //Inventario 2 - Processar leitura código de barras -->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/inventario/abastecimento/processarCodigoBarras")
    suspend fun inventoryQrCode2(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body inventoryReadingProcess: RequestInventoryReadingProcess
    ): Response<ResponseQrCode2>

    //Inventário 3 - Retornar lista dos produtos e volumes do endereço
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/inventario/abastecimento/{idInventario}/contagem/{numeroContagem}/endereco/{idEndereco}")
    suspend fun inventoryResponseRecyclerView(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idInventario") idInventario: Int,
        @Path("numeroContagem") numeroContagem: Int,
        @Path("idEndereco") idEndereco: Int,
    ): Response<ResponseListRecyclerView>

    //Retorna Corrugados -->
    @Headers("Content-Type: application/json")
    @GET("v1/corrugado")
    suspend fun getCorrugados(
        @Header("Authorization") token: String = TOKEN,
    ): Response<InventoryResponseCorrugados>

    //CRIAR VOLUME A VULSO -->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/inventario/abastecimento/{idInventario}/contagem/{numeroContagem}/endereco/{idEndereco}/volume/avulso")
    suspend fun inventoryCreateVoidPrinter(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idInventario") idInventario: Int,
        @Path("numeroContagem") numeroContagem: Int,
        @Path("idEndereco") idEndereco: Int,
        @Body createVoidPrinter: CreateVoidPrinter
    ): Response<EtiquetaInventory>

    /**Etiqueta - Processa tarefa de etiquetagem do volume -->*/
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/inventario/abastecimento/{idInventarioAbastecimentoItem}")
    suspend fun inventoryPrinterVol(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idInventarioAbastecimentoItem") idInventarioAbastecimentoItem: String
    ): Response<EtiquetaInventory>

    /**-------------------RECEBIMENTO----------------------------------->*/
    //Recebimento : Transferencia - Receber documento de transferencia -->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/transferencia/documento/receber")
    suspend fun receiptPost1(
        @Path("idArmazem") idArmazem: Int,
        @Header("Authorization") token: String,
        @Body postDocumentoRequestRec1: PostReciptQrCode1
    ): Response<ReceiptDoc1>

    //Recebimento : Transferencia - Apontar volume recebidoa -->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/transferencia/documento/recebimento/tarefa/{idTarefa}/apontarItem")
    suspend fun receiptPointed2(
        @Path("idArmazem") idArmazem: Int,
        @Path("idTarefa") idTarefa: String,
        @Header("Authorization") token: String,
        @Body postReceiptQrCode2: PostReceiptQrCode2
    ): Response<ReceiptDoc1>

    //Recebimento/Transferencia - Finalizar recebimento/conferencia -->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/transferencia/documento/conferencia/tarefa/{idTarefa}/finalizar")
    suspend fun receipt3(
        @Path("idArmazem") idArmazem: Int,
        @Path("idTarefa") idTarefa: String,
        @Header("Authorization") token: String,
        @Body postReceiptQrCode3: PostReceiptQrCode3
    ): Response<ReceiptMessageFinish>

    /**------------------------------ETIQUETAGEM------------------------------------------------->*/
    //Etiquetagem 1 - Processa tarefa de etiquetagem do volume -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/etiquetagem/processar")
    suspend fun postEtiquetagem1(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body etiquetagempost1: EtiquetagemRequest1
    ): Response<ResponseEtiquetagemEdit1>

    //Etiquetagem 2 - Consulta Etiquetagem Pendente - Pendências por nota fiscal -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/consulta/tarefa/etiquetagem/notasFiscais/pendente")
    suspend fun etiquetagemGet2(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<List<EtiquetagemResponse2>>

    //
    //ETIQUETAGEM 3 -> Pendências por pedidos da nota fiscal -->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/consulta/tarefa/etiquetagem/notasFiscais/pedidos/pendente")
    suspend fun postEtiquetagem3(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body etiquetagemRequestModel3: EtiquetagemRequestModel3
    ): Response<List<EtiquetagemResponse3>>

    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/consulta/tarefa/etiquetagem/pedidos/pendente")
    suspend fun getetiquetagempedNf(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<ResponsePendencePedidoEtiquetagem>


    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/consulta/tarefa/etiquetagem/pedidos/pendente/onda")
    suspend fun getPendenciaOnda(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<ResponsePendencyOndaEtiquetagem>

    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/tarefa/etiquetagem/pendentes")
    suspend fun getPendenciaRequisicao(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<ResponseEtiquetagemRequisicao>


    /**-----------------------------PICKING------------------------------------------------------>*/
    //Picking 1 - Retornar area que possuem tarefas de picking -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/tarefa/picking/area")
    suspend fun getReturnAreaPicking1(
        @Path("idArmazem") idArmazem: Int,
        @Header("Authorization") token: String,
    ): Response<List<PickingResponseModel1>>

    //Picking 2 - Retornar tarefas de picking da area -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/tarefa/picking/area/{idArea}")
    suspend fun getReturnTarefasPicking2(
        @Header("Authorization") token: String,
        @Path("idArmazem") idArmazem: Int,
        @Path("idArea") idArea: Int,
    ): Response<List<PickingResponse2>>

    //Picking 3 - Apontar item coletatado-->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/tarefa/picking/area/{idArea}/aponta")
    suspend fun postItemLidoPicking3(
        @Path("idArmazem") idArmazem: Int,
        @Path("idArea") idArea: Int,
        @Header("Authorization") token: String,
        @Body picking3: PickingRequest1
    ): Response<Unit>

    //Picking 1 new fluxo - Leitura de dados do Picking -->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/tarefa/picking/volume")
    suspend fun postReandingDataPicking1(
        @Path("idArmazem") idArmazem: Int,
        @Header("Authorization") token: String,
        @Body senDataPicking1: SendDataPicing1
    ): Response<Unit>

    //PICKING - Retorna agrupado por produto -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/tarefa/picking/agrupadoProduto")
    suspend fun getPickingReturnAgrounp(
        @Path("idArmazem") idArmazem: Int,
        @Header("Authorization") token: String,
    ): Response<ResponsePickingReturnGrouped>


    //Picking 4 - Retorna agrupado por produto -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/tarefa/picking/agrupadoProduto")
    suspend fun getGroupedProductAgrupadoPicking4(
        @Path("idArmazem") idArmazem: Int,
        @Header("Authorization") token: String,
    ): Response<List<PickingResponse3>>

    //Picking 5 - Finalizar produto do agrupamento -->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/tarefa/picking/produto/finaliza")
    suspend fun postFinalizarPicking5(
        @Path("idArmazem") idArmazem: Int,
        @Header("Authorization") token: String,
        @Body pickingRequest2: PickingRequest2
    ): Response<Unit>

    /**-----------------------------------MONTAGEM DE VOLUMES------------------------------------>*/
    // 1 -> Montagem de Volumes - Retornar tarefas de montagem de volumes -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/montagem/montar/ordem")
    suspend fun getMountingTask01(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<List<MountingTaskResponse1>>

    //1 - 2 ->Printer
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/montagem/montar/volume/impressao/{idOrdemMontagemVolume}")
    suspend fun getPrinterMounting(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idOrdemMontagemVolume") idOrdemMontagemVolume: String,
        @Header("Authorization") token: String = TOKEN,
    ): Response<ResponsePrinterMountingVol>


    // 2 --> Montagem de Volumes | Retornar numeros de series dos volumes para montar -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/montagem/montar/produto/{idProdutoKit}/volume")
    suspend fun returnNumSerieMounting2(
        @Path("idProdutoKit") idProdutoKit: String,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<ResponseMounting2>

    // 3 --> Montagem de Volumes | Retornar os endereços para leitura dos produtos individuais -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/montagem/montar/volume/{idOrdemMontagemVolume}/endereco")
    suspend fun returnAndressMounting2(
        @Path("idOrdemMontagemVolume") idOrdemMontagemVolume: String,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<ResponseAndressMonting3>

    // 4 --> Montagem de Volumes | Retornar os produtos do endereco para adicionar ao volume -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/montagem/montar/volume/{idOrdemMontagemVolume}/endereco/{idEnderecoOrigem}/produto")
    suspend fun returnProdMounting4(
        @Path("idOrdemMontagemVolume") idOrdemMontagemVolume: String,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idEnderecoOrigem") idEnderecoOrigem: String,
        @Header("Authorization") token: String = TOKEN,
    ): Response<ResponseMounting4>

    // 5 --> Montagem de Volumes | Adicionar produto EAN ao volume -->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/montagem/montar/volume/produto/adicionar")
    suspend fun addProdEanMounting5(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body bodyMounting5: RequestMounting5,
        @Header("Authorization") token: String = TOKEN,
    ): Response<Unit>

    // 6 --> Montagem de Volumes | Reimpressao unicq -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/montagem/montar/volume/setImpressao")
    suspend fun setImpressaoUnica(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body body: RequestMounting6,
        @Header("Authorization") token: String = TOKEN,
    ): Response<Unit>


    /**----------------------------RECEBIMENTO DE PRODUÇAO------------------------------------------*/
    //BUSCA IDS OPERADOR COM PENDENCIAS -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/armazenagem/pedido/pendente/{filtrarOperador}/operador/{idOperador}")
    suspend fun getReceiptProduct1(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("filtrarOperador") filtrarOperador: Boolean,
        @Path("idOperador") idOperador: String
    ): Response<List<ReceiptProduct1>>

    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/operador/armazenagem/pendente")
    suspend fun getPendenciesOperatorReceiptProduct(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
    ): Response<List<ReceiptIdOperadorSeriazable>>

    //RECEBIMENTO DE PRODUÇAO - Confere o recebimento dos volumes da produção -->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/conferencia/recebimentoProducao")
    suspend fun postReadingReceiptProduct2(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body qrCode: QrCodeReceipt1
    ): Response<Unit>

    //FINALIZA TODOS OS PEDIDOS CLICK BUTTON -->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/armazenagem/pedidos/tarefa/item/operador/finalizar")
    suspend fun postFinishAllOrder(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body finishOrder: PostCodScanFinish
    ): Response<Unit>

    //RECEBIMENTO DE PRODUÇÃO - Retornar pedidos itens pendentes de armazenagem -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/armazenagem/pedido/{pedido}/itens/pendente/{filtrarOperador}/operador/{idOperador}")
    suspend fun getReceiptProduct3(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("pedido") pedido: String,
        @Path("filtrarOperador") filtrarOperador: Boolean,
        @Path("idOperador") idOperador: String
    ): Response<List<ReceiptProduct2>>

    //Controle de Acesso - Validar permissão de supervisor -->
    @Headers("Content-Type: application/json")
    @POST("v1/auth/login/validar/supervisor/armazem/{idArmazem}")
    suspend fun postValidAccesReceiptProduct(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body posLoginValidadREceipPorduct: PosLoginValidadREceipPorduct
    ): Response<Unit>

    //Finish ARMAZENA ITEM RECEBIMENTO -->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/armazenagem/pedido/tarefa/item/finalizar")
    suspend fun postFinishReceiptProduct(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body postFinishReceiptProduct3: PostFinishReceiptProduct3
    ): Response<Unit>

    /**REIMPRESSAO -------------------------------------------------------------------->*/

    //Etiquetagem | Tarefas de reimpressão por número da requisição -->
    @Headers("Content-Type: application/json")
    @GET("v2/armazem/{idArmazem}/tarefa/etiquetagem/reimpressao/numeroRequisicao/{numeroRequisicao}")
    suspend fun reimpressaoPorNumRequisicao(
        @Header("Authorization") token: String,
        @Path("idArmazem") idArmazem: Int,
        @Path("numeroRequisicao") numeroRequisicao: String
    ): Response<ResultReimpressaoDefault>

    //Etiquetagem | Tarefas de reimpressao por número de série -->
    @Headers("Content-Type: application/json")
    @GET("v2/armazem/{idArmazem}/tarefa/etiquetagem/reimpressao/numeroSerie/{numeroSerie}")
    suspend fun reimpressaoPorNumSerie(
        @Header("Authorization") token: String,
        @Path("idArmazem") idArmazem: Int,
        @Path("numeroSerie") numeroSerie: String
    ): Response<ResultReimpressaoDefault>

    //Etiquetagem | Tarefas de reimpressão por nota fiscal -->
    @Headers("Content-Type: application/json")
    @GET("v2/armazem/{idArmazem}/tarefa/etiquetagem/reimpressao/nfNumero/{nfNumero}/nfSerie/{nfSerie}")
    suspend fun reimpressaoPorNumNf(
        @Header("Authorization") token: String,
        @Path("idArmazem") idArmazem: Int,
        @Path("nfNumero") nfNumero: String,
        @Path("nfSerie") nfSerie: String
    ): Response<ResultReimpressaoDefault>

    //Etiquetagem | Tarefas de reimpressao por número do pedido -->
    @Headers("Content-Type: application/json")
    @GET("v2/armazem/{idArmazem}/tarefa/etiquetagem/reimpressao/numeroPedido/{numeroPedido}")
    suspend fun reimpressaoPorPedido(
        @Header("Authorization") token: String,
        @Path("idArmazem") idArmazem: Int,
        @Path("numeroPedido") numeroPedido: String,
    ): Response<ResultReimpressaoDefault>


    //Etiquetagem | Etiquetas para reimpressao -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/etiquetagem/reimpressao")
    suspend fun getEtiquetasReimpressao(
        @Header("Authorization") token: String,
        @Path("idArmazem") idArmazem: Int,
        @Body body: RequestEtiquetasReimpressaoBody
    ): Response<ResponseEtiquetasReimpressao>

    //SALVAR LOG DE IMPRESSÃO (ETIQUETAGEM)-------------------------------->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/etiquetagem/reimpressao/gravarlog")
    suspend fun saveLogPrinter(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body body: BodySaveLogPrinter
    ): Response<Unit>


    /**--------------------------------DESMONTAGEM DE VOLUMES----------------------------------->*/
    // 1 - Desmontagem de Volumes | Retornar tarefas de desmontagem de volumes -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/montagem/desmontar/ordem")
    suspend fun getReturnTaskUnmountingVol1(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
    ): Response<UnmountingVolumes1>


    // 2 - Desmontagem de Volumes | Retornar volumes e quantidades a desmontar -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/montagem/desmontar/endereco/{idEndereco}/volume")
    suspend fun getReturnVolQntsUnmountingVol2(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idEndereco") idEndereco: Int
    ): Response<ResponseUnMountingFinish>

    // 3 - Desmontagem de Volumes | Desmonta o volume pelo numero de serie -->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/montagem/desmontar")
    suspend fun postDisassembleVol3(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body requestDisassamblyVol: RequestDisassamblyVol
    ): Response<Unit>

    /**------------------------AUDITORIA----------------------------------->*/

    // 1 - AUDITORIA - Busca ID da auditoria -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/tarefa/auditoria/{idAuditoria}")
    suspend fun getIdAuditoria(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idAuditoria") idAuditoria: String,
    ): Response<ResponseAuditoria1>


    // 2 - AUDITORIA - Busca ID da auditoria -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/tarefa/auditoria/estantes/{idAuditoria}")
    suspend fun getAuditoriaEstantes(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idAuditoria") idAuditoria: String,
    ): Response<List<ResponseAuditoriaEstantes2>>

    // 3 - AUDITORIA - RETORNA OS ITENS DENTRO DA ESTANTES -->
    @Headers("Content-Type: application/json")
    @GET("v1/armazem/{idArmazem}/tarefa/auditoria/item/{idAuditoria}/{estante}")
    suspend fun getAuditoria3(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idAuditoria") idAuditoria: String,
        @Path("estante") estante: String,
    ): Response<ResponseFinishAuditoria>


    // 3 - AUDITORIA - RETORNA OS ITENS DENTRO DA ESTANTES -->
    @Headers("Content-Type: application/json")
    @POST("v1/armazem/{idArmazem}/tarefa/auditoria/apontarItem")
    suspend fun postAuditoria4(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body body: BodyAuditoriaFinish
    ): Response<ResponseFinishAuditoria>

    /**RESERVA POR PEDIDO ---------------------------------------------------->*/
    //Adicionar pedido -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/reservarPedido/adicionar")
    suspend fun postAddPedido(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body body: BodyAddReservation1
    ): Response<ReservationRequetsResponse1>

    //Adicionar volume -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/reservarVolume/adicionar")
    suspend fun postAddVolume(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body body: BodyAddVolReservationByRequest
    ): Response<List<VolumesReservedRequest>>

    /**CONTROLE DE QUALIDADE ------------------------------------------------------------------->*/
    //busca tarefas e trás todos os resultados -->
    @Headers("Content-Type: application/json")
    @GET("v2/armazem/{idArmazem}/{codBarrasEnd}/tarefa/controleQualidade/buscaTarefas")
    suspend fun getTaskQualityControl1(
        @Header("Authorization") token: String,
        @Path("idArmazem") idArmazem: Int,
        @Path("codBarrasEnd") codBarrasEnd: String,
    ): Response<ResponseControlQuality1>

    //Set itens aprovados -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/controleQualidade/setAprovados")
    suspend fun postSetAprovadosQualityControl(
        @Header("Authorization") token: String,
        @Path("idArmazem") idArmazem: Int,
        @Body body: BodySetAprovadoQuality
    ): Response<Unit>

    //Set itens aprovados -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/controleQualidade/setReprovados")
    suspend fun postSetReprovadosQualityControl(
        @Header("Authorization") token: String,
        @Path("idArmazem") idArmazem: Int,
        @Body body: BodySetAprovadoQuality
    ): Response<Unit>

    //Set itens pendente -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/controleQualidade/setPendente")
    suspend fun postSetPendenteQualityControl(
        @Header("Authorization") token: String,
        @Path("idArmazem") idArmazem: Int,
        @Body body: BodySetPendenceQuality
    ): Response<Unit>

    //Gera requisição -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/controleQualidade/geraRequisicao")
    suspend fun postGenerateRequestQualityControl(
        @Header("Authorization") token: String,
        @Path("idArmazem") idArmazem: Int,
        @Body body: BodyGenerateRequestControlQuality
    ): Response<List<ResponseGenerateRequestControlQuality>>

    //Finalizar -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/controleQualidade/finalizar")
    suspend fun postFinishQualityControl(
        @Header("Authorization") token: String,
        @Path("idArmazem") idArmazem: Int,
        @Body body: BodyFinishQualityControl
    ): Response<Unit>

    /**CONFERENCIA DE EMBARQUE ------------------------------------------------------------------>*/
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/conferencia/tarefasEmbarque")
    suspend fun postListTaskEmbarque(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body body: BodyChaveBoarding
    ): Response<ResponseConferenceBoarding>

    //SETA ITENS APROVADOS -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/conferencia/aprovados")
    suspend fun postSetaApproved(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body body: BodySetBoarding
    ): Response<Unit>

    //SETA ITENS REPROVADOS -->
    @Headers("Content-Type: application/json")
    @POST("v2/armazem/{idArmazem}/tarefa/conferencia/pendente")
    suspend fun postSetaDisapproved(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body body: BodySetBoarding
    ): Response<Unit>


    companion object {
        var TOKEN = ""
        var IDARMAZEM = 0
    }
}