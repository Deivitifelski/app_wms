package com.documentos.wms_beirario.data

import ArmazenagemResponse
import com.documentos.wms_beirario.model.armazenagem.ArmazemRequestFinish
import com.documentos.wms_beirario.model.armazens.ArmazensResponse
import com.documentos.wms_beirario.model.auditoria.BodyAuditoriaFinish
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoria1
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoriaEstantes2
import com.documentos.wms_beirario.model.auditoria.ResponseFinishAuditoria
import com.documentos.wms_beirario.model.codBarras.CodigodeBarrasResponse
import com.documentos.wms_beirario.model.desmontagemVol.RequestDisassamblyVol
import com.documentos.wms_beirario.model.desmontagemVol.ResponseUnMountingFinish
import com.documentos.wms_beirario.model.desmontagemVol.UnmountingVolumes1
import com.documentos.wms_beirario.model.etiquetagem.*
import com.documentos.wms_beirario.model.inventario.*
import com.documentos.wms_beirario.model.login.LoginRequest
import com.documentos.wms_beirario.model.login.LoginResponse
import com.documentos.wms_beirario.model.mountingVol.*
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.*
import com.documentos.wms_beirario.model.picking.*
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode2
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode3
import com.documentos.wms_beirario.model.recebimento.request.PostReciptQrCode1
import com.documentos.wms_beirario.model.recebimento.response.ReceiptDoc1
import com.documentos.wms_beirario.model.recebimento.response.ReceiptMessageFinish
import com.documentos.wms_beirario.model.receiptproduct.*
import com.documentos.wms_beirario.model.reimpressao.ResponseEtiquetasReimpressao
import com.documentos.wms_beirario.model.reimpressao.ResultReimpressaoDefault
import com.documentos.wms_beirario.model.separation.*
import com.documentos.wms_beirario.model.tipo_tarefa.TipoTarefaResponseItem
import retrofit2.Response
import retrofit2.http.*

interface ServiceApi {

    /**Controle de Acesso - Login -->*/
    @POST("v1/auth/login")
    suspend fun postLogin(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    /**Armazém - Retornar armazens do operador -->*/
    @GET("v1/armazem")
    suspend fun getArmazens(@Header("Authorization") token: String = TOKEN): Response<List<ArmazensResponse>>

    @GET("v1/armazem/{id_armazem}/tipoTarefa")
    suspend fun getTipoTarefa(
        @Path("id_armazem") id_armazem: Int,
        @Header("Authorization") token: String
    ): Response<List<TipoTarefaResponseItem>>

    /**---------------------------------ARMAZENAGEM-----------------------------------------------*/
    //ARMAZENAGEM -->
    @GET("v1/armazem/{idArmazem}/armazenagem/tarefa/pendente")
    suspend fun Armazenagemget1(
        @Path("idArmazem") idarmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN
    ): Response<List<ArmazenagemResponse>>

    //ARMAZENAGEM FINALIZAR -->
    @POST("v1/armazem/{idArmazem}/armazenagem/tarefa/finalizar")
    suspend fun armazenagemPostFinish2(
        @Path("idArmazem") idarmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body armazemRequestFinish: ArmazemRequestFinish
    ): Response<Unit>

    /**---------------------------------CONSULTA COD.BARRAS---------------------------------------*/
    @GET("v1/armazem/{idArmazem}/consulta/{codigoBarras}")
    suspend fun getCodBarras(
        @Path("idArmazem") idarmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Path("codigoBarras") codigoBarras: String
    ): Response<CodigodeBarrasResponse>

    /**---------------------------------SEPARAÇAO-----------------------------------------------*/

    @GET("v1/armazem/{idArmazem}/tarefa/separacao/estante")
    suspend fun getItemsSeparation(
        @Path("idArmazem") idarmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<List<ResponseItemsSeparationItem>>

    @GET("v1/armazem/{idArmazem}/tarefa/separacao/andar")
    suspend fun getAndaresSeparation(
        @Path("idArmazem") idarmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<ResponseGetAndaresSeparation>

    //NOVO POST ENVIANDO O ARRAY DE ESTANTES E ANDARES -->
    @POST("v1/armazem/{idArmazem}/tarefa/separacao/estantes/andares")
    suspend fun postListCheckBox(
        @Path("idArmazem") idarmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body bodyArrayAndarEstantes: RequestSeparationArrays
    ): Response<ResponseSeparationNew>


    @POST("v1/armazem/{idArmazem}/tarefa/separacao/estante/endereco/separa")
    suspend fun postSeparationEnd(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body separationEnd: SeparationEnd
    ): Response<Unit>

    //Separação | Retornar produtos a separar no endereco -->
    @GET("v1/armazem/{idArmazem}/tarefa/separacao/endereco/{idEnderecoOrigem}/produtos")
    suspend fun getSeparaProdAndress(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idEnderecoOrigem") idEnderecoOrigem: String,
        @Header("Authorization") token: String = TOKEN,
    ): Response<SeparacaoProdAndress4>

    //Separação | Separa o produtos lido pelo codigo de barras no endereco
    @POST("v1/armazem/{idArmazem}/tarefa/separacao/endereco/produto")
    suspend fun postSepProdAndress(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body bodySeparation3: bodySeparation3,
        @Header("Authorization") token: String = TOKEN,
    ): Response<Unit>


    /**---------------------------------MOVIMENTAÇAO-------------------------------------------->*/
    //Faz Get das tarefas de movimentação filtrada pelo usuario (TRUE) -->
    @GET("v1/armazem/{idArmazem}/movimentacao/pendente/{filtrarOperador}")
    suspend fun MovementShowMovements(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("filtrarOperador") filtrarOperador: Boolean,
        @Header("Authorization") token: String = TOKEN,
    ): Response<List<MovementResponseModel1>>

    //CRIAR NOVA TAREFA -->
    @POST("v1/armazem/{idArmazem}/movimentacao/criar")
    suspend fun movementAddNewTask(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<MovementNewTask>

    //MOVIMENTAÇAO  Retorna Itens Proxima Tela -->
    @GET("v1/armazem/{idArmazem}/movimentacao/{idTarefa}/itens/pendente")
    suspend fun movementgetRetornaItensMov2(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Path("idTarefa") idTarefa: String
    ): Response<List<MovementReturnItemClickMov>>

    //Adiciona Item a tarefa de movimentação -->
    @POST("v1/armazem/{idArmazem}/movimentacao/adicionarItem")
    suspend fun movementAddItemMov(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body movementAddTask: MovementAddTask
    ): Response<Unit>

    //Finish -->
    @POST("v1/armazem/{idArmazem}/movimentacao/finalizar")
    suspend fun movementFinishMov(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body postRequestModelFinish: MovementFinishAndress
    ): Response<Unit>


    /**-------------------------------INVENTARIO-------------------------------------------------*/
    //Inventario 1 - Retornar tarefas de inventario pendentes -->
    @GET("v1/armazem/{idArmazem}/inventario/abastecimento/pendente")
    suspend fun Inventorypending1(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<List<ResponseInventoryPending1>>

    //Inventario 2 - Processar leitura código de barras -->
    @POST("v1/armazem/{idArmazem}/inventario/abastecimento/processarCodigoBarras")
    suspend fun inventoryQrCode2(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body inventoryReadingProcess: RequestInventoryReadingProcess
    ): Response<ResponseQrCode2>

    //Inventário 3 - Retornar lista dos produtos e volumes do endereço
    @GET("v1/armazem/{idArmazem}/inventario/abastecimento/{idInventario}/contagem/{numeroContagem}/endereco/{idEndereco}")
    suspend fun inventoryResponseRecyclerView(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idInventario") idInventario: Int,
        @Path("numeroContagem") numeroContagem: Int,
        @Path("idEndereco") idEndereco: Int,
    ): Response<ResponseListRecyclerView>

    //Retorna Corrugados -->
    @GET("v1/corrugado")
    suspend fun getCorrugados(
        @Header("Authorization") token: String = TOKEN,
    ): Response<InventoryResponseCorrugados>

    //CRIAR VOLUME A VULSO -->
    @POST("v1/armazem/{idArmazem}/inventario/abastecimento/{idInventario}/contagem/{numeroContagem}/endereco/{idEndereco}/volume/avulso")
    suspend fun inventoryCreateVoidPrinter(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idInventario") idInventario: Int,
        @Path("numeroContagem") numeroContagem: Int,
        @Path("idEndereco") idEndereco: Int,
        @Body createVoidPrinter: CreateVoidPrinter
    ): Response<EtiquetaInventory>

    /**Etiqueta - Processa tarefa de etiquetagem do volume --> (REVISAR)*/
    @GET("v1/armazem/{idArmazem}/inventario/abastecimento/{idInventarioAbastecimentoItem}")
    suspend fun inventoryPrinterVol(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idInventarioAbastecimentoItem") idInventarioAbastecimentoItem: String
    ): Response<EtiquetaInventory>

    /**-------------------RECEBIMENTO----------------------------------->*/
    //Recebimento : Transferencia - Receber documento de transferencia -->
    @POST("v1/armazem/{idArmazem}/transferencia/documento/receber")
    suspend fun receiptPost1(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body postDocumentoRequestRec1: PostReciptQrCode1
    ): Response<ReceiptDoc1>

    //Recebimento : Transferencia - Apontar volume recebidoa -->
    @POST("v1/armazem/{idArmazem}/transferencia/documento/recebimento/tarefa/{idTarefa}/apontarItem")
    suspend fun receiptPointed2(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idTarefa") idTarefa: String,
        @Header("Authorization") token: String = TOKEN,
        @Body postReceiptQrCode2: PostReceiptQrCode2
    ): Response<ReceiptDoc1>

    //Recebimento/Transferencia - Finalizar recebimento/conferencia -->
    @POST("v1/armazem/{idArmazem}/transferencia/documento/conferencia/tarefa/{idTarefa}/finalizar")
    suspend fun receipt3(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idTarefa") idTarefa: String,
        @Header("Authorization") token: String = TOKEN,
        @Body postReceiptQrCode3: PostReceiptQrCode3
    ): Response<ReceiptMessageFinish>

    /**------------------------------ETIQUETAGEM------------------------------------------------->*/
    //Etiquetagem 1 - Processa tarefa de etiquetagem do volume
    @POST("v2/armazem/{idArmazem}/tarefa/etiquetagem/processar")
    suspend fun postEtiquetagem1(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body etiquetagempost1: EtiquetagemRequest1
    ): Response<ResponseEtiquetagemEdit1>

    //Etiquetagem 2 - Consulta Etiquetagem Pendente - Pendências por nota fiscal
    @GET("v1/armazem/{idArmazem}/consulta/tarefa/etiquetagem/notasFiscais/pendente")
    suspend fun etiquetagemGet2(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<List<EtiquetagemResponse2>>

    //
    //ETIQUETAGEM 3 -> Pendências por pedidos da nota fiscal
    @POST("v1/armazem/{idArmazem}/consulta/tarefa/etiquetagem/notasFiscais/pedidos/pendente")
    suspend fun postEtiquetagem3(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body etiquetagemRequestModel3: EtiquetagemRequestModel3
    ): Response<List<EtiquetagemResponse3>>

    @GET("v1/armazem/{idArmazem}/consulta/tarefa/etiquetagem/pedidos/pendente")
    suspend fun getetiquetagempedNf(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<ResponsePendencePedidoEtiquetagem>


    @GET("v1/armazem/{idArmazem}/consulta/tarefa/etiquetagem/pedidos/pendente/onda")
    suspend fun getPendenciaOnda(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<ResponsePendencyOndaEtiquetagem>

    @GET("v1/armazem/{idArmazem}/tarefa/etiquetagem/pendentes")
    suspend fun getPendenciaRequisicao(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<ResponseEtiquetagemRequisicao>


    /**-----------------------------PICKING------------------------------------------------------>*/
    //Picking 1 - Retornar area que possuem tarefas de picking
    @GET("v1/armazem/{idArmazem}/tarefa/picking/area")
    suspend fun getReturnAreaPicking1(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<List<PickingResponseModel1>>

    //Picking 2 - Retornar tarefas de picking da area -->
    @GET("v1/armazem/{idArmazem}/tarefa/picking/area/{idArea}")
    suspend fun getReturnTarefasPicking2(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idArea") idArea: Int,
    ): Response<List<PickingResponse2>>

    //Picking 3 - Apontar item coletatado-->
    @POST("v1/armazem/{idArmazem}/tarefa/picking/area/{idArea}/aponta")
    suspend fun postItemLidoPicking3(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idArea") idArea: Int,
        @Header("Authorization") token: String = TOKEN,
        @Body picking3: PickingRequest1
    ): Response<Unit>

    //Picking 1 new fluxo - Leitura de dados do Picking -->
    @POST("v1/armazem/{idArmazem}/tarefa/picking/volume")
    suspend fun postReandingDataPicking1(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body senDataPicking1: SendDataPicing1
    ): Response<Unit>

    //PICKING - Retorna agrupado por produto -->
    @GET("v1/armazem/{idArmazem}/tarefa/picking/agrupadoProduto")
    suspend fun getPickingReturnAgrounp(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<ResponsePickingReturnGrouped>


    //Picking 4 - Retorna agrupado por produto
    @GET("v1/armazem/{idArmazem}/tarefa/picking/agrupadoProduto")
    suspend fun getGroupedProductAgrupadoPicking4(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<List<PickingResponse3>>

    //Picking 5 - Finalizar produto do agrupamento
    @POST("v1/armazem/{idArmazem}/tarefa/picking/produto/finaliza")
    suspend fun postFinalizarPicking5(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body pickingRequest2: PickingRequest2
    ): Response<Unit>

    /**-----------------------------------MONTAGEM DE VOLUMES------------------------------------>*/
    // 1 -> Montagem de Volumes - Retornar tarefas de montagem de volumes
    @GET("v1/armazem/{idArmazem}/montagem/montar/ordem")
    suspend fun getMountingTask01(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<List<MountingTaskResponse1>>

    //1 - 2 ->Printer
    @GET("v1/armazem/{idArmazem}/montagem/montar/volume/impressao/{idOrdemMontagemVolume}")
    suspend fun getPrinterMounting(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idOrdemMontagemVolume") idOrdemMontagemVolume: String,
        @Header("Authorization") token: String = TOKEN,
    ): Response<ResponsePrinterMountingVol>


    // 2 --> Montagem de Volumes | Retornar numeros de series dos volumes para montar
    @GET("v1/armazem/{idArmazem}/montagem/montar/produto/{idProdutoKit}/volume")
    suspend fun returnNumSerieMounting2(
        @Path("idProdutoKit") idProdutoKit: String,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<ResponseMounting2>

    // 3 --> Montagem de Volumes | Retornar os endereços para leitura dos produtos individuais
    @GET("v1/armazem/{idArmazem}/montagem/montar/volume/{idOrdemMontagemVolume}/endereco")
    suspend fun returnAndressMounting2(
        @Path("idOrdemMontagemVolume") idOrdemMontagemVolume: String,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<ResponseAndressMonting3>

    // 4 --> Montagem de Volumes | Retornar os produtos do endereco para adicionar ao volume
    @GET("v1/armazem/{idArmazem}/montagem/montar/volume/{idOrdemMontagemVolume}/endereco/{idEnderecoOrigem}/produto")
    suspend fun returnProdMounting4(
        @Path("idOrdemMontagemVolume") idOrdemMontagemVolume: String,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idEnderecoOrigem") idEnderecoOrigem: String,
        @Header("Authorization") token: String = TOKEN,
    ): Response<ResponseMounting4>

    // 5 --> Montagem de Volumes | Adicionar produto EAN ao volume
    @POST("v1/armazem/{idArmazem}/montagem/montar/volume/produto/adicionar")
    suspend fun addProdEanMounting5(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body bodyMounting5: RequestMounting5,
        @Header("Authorization") token: String = TOKEN,
    ): Response<Unit>


    /**----------------------------RECEBIMENTO DE PRODUÇAO------------------------------------------*/
    //BUSCA IDS OPERADOR COM PENDENCIAS -->
    @GET("v1/armazem/{idArmazem}/armazenagem/pedido/pendente/{filtrarOperador}/operador/{idOperador}")
    suspend fun getReceiptProduct1(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("filtrarOperador") filtrarOperador: Boolean,
        @Path("idOperador") idOperador: String
    ): Response<List<ReceiptProduct1>>

    @GET("v1/armazem/{idArmazem}/operador/armazenagem/pendente")
    suspend fun getPendenciesOperatorReceiptProduct(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
    ): Response<List<ReceiptIdOperador>>

    //RECEBIMENTO DE PRODUÇAO - Confere o recebimento dos volumes da produção -->
    @POST("v1/armazem/{idArmazem}/conferencia/recebimentoProducao")
    suspend fun postReadingReceiptProduct2(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body qrCode: QrCodeReceipt1
    ): Response<Unit>

    //RECEBIMENTO DE PRODUÇÃO - Retornar pedidos itens pendentes de armazenagem
    @GET("v1/armazem/{idArmazem}/armazenagem/pedido/{pedido}/itens/pendente/{filtrarOperador}/operador/{idOperador}")
    suspend fun getReceiptProduct3(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("pedido") pedido: String,
        @Path("filtrarOperador") filtrarOperador: Boolean,
        @Path("idOperador") idOperador: String
    ): Response<List<ReceiptProduct2>>

    //Controle de Acesso - Validar permissão de supervisor -->
    @POST("v1/auth/login/validar/supervisor/armazem/{idArmazem}")
    suspend fun postValidAccesReceiptProduct(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body posLoginValidadREceipPorduct: PosLoginValidadREceipPorduct
    ): Response<Unit>

    //Finish ARMAZENA ITEM RECEBIMENTO -->
    @POST("v1/armazem/{idArmazem}/armazenagem/pedido/tarefa/item/finalizar")
    suspend fun postFinishReceiptProduct(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body postFinishReceiptProduct3: PostFinishReceiptProduct3
    ): Response<Unit>

    /**REIMPRESSAO -------------------------------------------------------------------->*/

    //Etiquetagem | Tarefas de reimpressão por número da requisição -->
    @GET("v2/armazem/{idArmazem}/tarefa/etiquetagem/reimpressao/numeroRequisicao/{numeroRequisicao}")
    suspend fun reimpressaoPorNumRequisicao(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("numeroRequisicao") numeroRequisicao: String
    ): Response<ResultReimpressaoDefault>

    //Etiquetagem | Tarefas de reimpressao por número de série -->
    @GET("v2/armazem/{idArmazem}/tarefa/etiquetagem/reimpressao/numeroSerie/{numeroSerie}")
    suspend fun reimpressaoPorNumSerie(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("numeroSerie") numeroSerie: String
    ): Response<ResultReimpressaoDefault>

    //Etiquetagem | Tarefas de reimpressão por nota fiscal -->
    @GET("v2/armazem/{idArmazem}/tarefa/etiquetagem/reimpressao/nfNumero/{nfNumero}/nfSerie/{nfSerie}")
    suspend fun reimpressaoPorNumNf(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("nfNumero") nfNumero: String,
        @Path("nfSerie") nfSerie: String
    ): Response<ResultReimpressaoDefault>

    //Etiquetagem | Tarefas de reimpressao por número do pedido -->
    @GET("v2/armazem/{idArmazem}/tarefa/etiquetagem/reimpressao/numeroPedido/{numeroPedido}")
    suspend fun reimpressaoPorPedido(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("numeroPedido") numeroPedido: String,
    ): Response<ResultReimpressaoDefault>


    //Etiquetagem | Etiquetas para reimpressao
    @GET("v2/armazem/{idArmazem}/tarefa/etiquetagem/reimpressao/idTarefa/{idTarefa}/sequencialTarefa/{sequencialTarefa}")
    suspend fun getEtiquetasReimpressao(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idTarefa") idTarefa: String,
        @Path("sequencialTarefa") sequencialTarefa: String,
    ): Response<ResponseEtiquetasReimpressao>

    /**------------------------DESMONTAGEM DE VOLUMES----------------------------------->*/

    // 1 - Desmontagem de Volumes | Retornar tarefas de desmontagem de volumes -->
    @GET("v1/armazem/{idArmazem}/montagem/desmontar/ordem")
    suspend fun getReturnTaskUnmountingVol1(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
    ): Response<UnmountingVolumes1>


    // 2 - Desmontagem de Volumes | Retornar volumes e quantidades a desmontar -->
    @GET("v1/armazem/{idArmazem}/montagem/desmontar/endereco/{idEndereco}/volume")
    suspend fun getReturnVolQntsUnmountingVol2(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idEndereco") idEndereco: Int
    ): Response<ResponseUnMountingFinish>

    // 3 - Desmontagem de Volumes | Desmonta o volume pelo numero de serie -->
    @POST("v1/armazem/{idArmazem}/montagem/desmontar")
    suspend fun postDisassembleVol3(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body requestDisassamblyVol: RequestDisassamblyVol
    ): Response<Unit>

    /**------------------------AUDITORIA----------------------------------->*/

    // 1 - AUDITORIA - Busca ID da auditoria -->
    @GET("v1/armazem/{idArmazem}/tarefa/auditoria/{idAuditoria}")
    suspend fun getIdAuditoria(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idAuditoria") idAuditoria: String,
    ): Response<ResponseAuditoria1>


    // 2 - AUDITORIA - Busca ID da auditoria -->
    @GET("v1/armazem/{idArmazem}/tarefa/auditoria/estantes/{idAuditoria}")
    suspend fun getAuditoriaEstantes(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idAuditoria") idAuditoria: String,
    ): Response<List<ResponseAuditoriaEstantes2>>

    // 3 - AUDITORIA - RETORNA OS ITENS DENTRO DA ESTANTES -->
    @GET("v1/armazem/{idArmazem}/tarefa/auditoria/item/{idAuditoria}/{estante}")
    suspend fun getAuditoria3(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idAuditoria") idAuditoria: String,
        @Path("estante") estante: String,
    ): Response<ResponseFinishAuditoria>


    // 3 - AUDITORIA - RETORNA OS ITENS DENTRO DA ESTANTES -->
    @POST("v1/armazem/{idArmazem}/tarefa/auditoria/apontarItem")
    suspend fun postAuditoria4(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body body: BodyAuditoriaFinish
    ): Response<ResponseFinishAuditoria>


    companion object {
        var TOKEN = ""
        var IDARMAZEM = 0
    }

    //http://10.0.1.111:5002/wms/v1/
}