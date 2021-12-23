package com.documentos.wms_beirario.data

import com.documentos.wms_beirario.model.armazenagem.ArmazenagemResponse
import com.documentos.wms_beirario.model.armazens.ArmazensResponse
import com.documentos.wms_beirario.model.codBarras.CodigodeBarrasResponse
import com.documentos.wms_beirario.model.desmontagemdevolumes.DisassemblyResponse1
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequest1
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequestModel3
import com.documentos.wms_beirario.model.etiquetagem.response.EtiquetagemResponse2
import com.documentos.wms_beirario.model.etiquetagem.response.EtiquetagemResponse3
import com.documentos.wms_beirario.model.inventario.*
import com.documentos.wms_beirario.model.login.LoginRequest
import com.documentos.wms_beirario.model.login.LoginResponse
import com.documentos.wms_beirario.model.mountingVol.MountingTaskResponse1
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.*
import com.documentos.wms_beirario.model.picking.*
import com.documentos.wms_beirario.model.recebimento.ReceiptDoc1
import com.documentos.wms_beirario.model.recebimento.ReceiptMessageFinish
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode2
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode3
import com.documentos.wms_beirario.model.recebimento.request.PostReciptQrCode1
import com.documentos.wms_beirario.model.receiptproduct.PosLoginValidadREceipPorduct
import com.documentos.wms_beirario.model.receiptproduct.QrCodeReceipt1
import com.documentos.wms_beirario.model.receiptproduct.ReceiptProduct1
import com.documentos.wms_beirario.model.receiptproduct.ReceiptProduct2
import com.documentos.wms_beirario.model.separation.ResponseItemsSeparationItem
import com.documentos.wms_beirario.model.separation.ResponseListCheckBoxItem
import com.documentos.wms_beirario.model.separation.SeparationEnd
import com.documentos.wms_beirario.model.separation.SeparationListCheckBox
import com.documentos.wms_beirario.model.tipo_tarefa.TipoTarefaResponseItem
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ServiceApi {

    var baseUrl: String

    /**Controle de Acesso - Login -->*/
    @POST("auth/login")
    suspend fun postLogin(@Body loginRequest: LoginRequest): Response<LoginResponse>

    /**Armazém - Retornar armazens do operador -->*/
    @GET("armazem")
    suspend fun getArmazens(@Header("Authorization") token: String = TOKEN): Response<List<ArmazensResponse>>

    @GET("armazem/{id_armazem}/tipoTarefa")
    suspend fun getTipoTarefa(
        @Path("id_armazem") id_armazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN
    ): Response<List<TipoTarefaResponseItem>>

    /**---------------------------------ARMAZENAGEM-----------------------------------------------*/
    //ARMAZENAGEM -->
    @GET("armazem/{idArmazem}/armazenagem/tarefa/pendente")
    suspend fun getArmazenagem(
        @Path("idArmazem") idarmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN
    ): Response<List<ArmazenagemResponse>>

    /**---------------------------------CONSULTA COD.BARRAS-----------------------------------------------*/
    @GET("armazem/{idArmazem}/consulta/{codigoBarras}")
    suspend fun getCodBarras(
        @Path("idArmazem") idarmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Path("codigoBarras") codigoBarras: String
    ): Response<CodigodeBarrasResponse>

    /**---------------------------------SEPARAÇAO-----------------------------------------------*/

    @GET("armazem/{idArmazem}/tarefa/separacao/estante")
    suspend fun getItemsSeparation(
        @Path("idArmazem") idarmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<List<ResponseItemsSeparationItem>>


    @POST("armazem/{idArmazem}/tarefa/separacao/estantes/")
    suspend fun postListCheckBox(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body separationListCheckBox: SeparationListCheckBox
    ): Response<List<ResponseListCheckBoxItem>>

    @POST("armazem/{idArmazem}/tarefa/separacao/estante/endereco/separa")
    suspend fun postSeparationEnd(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body separationEnd: SeparationEnd
    ): Response<Unit>

    /**---------------------------------MOVIMENTAÇAO-------------------------------------------->*/
    @GET("armazem/{idArmazem}/movimentacao/pendente")
    suspend fun MovementShowMovements(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<List<MovementResponseModel1>>

    //CRIAR NOVA TAREFA -->
    @POST("armazem/{idArmazem}/movimentacao/criar")
    suspend fun movementAddNewTask(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<MovementNewTask>

    //MOVIMENTAÇAO  Retorna Itens Proxima Tela -->
    @GET("armazem/{idArmazem}/movimentacao/{idTarefa}/itens/pendente")
    suspend fun movementgetRetornaItensMov2(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Path("idTarefa") idTarefa: String
    ): Response<List<MovementReturnItemClickMov>>

    //Adiciona Item a tarefa de movimentação -->
    @POST("armazem/{idArmazem}/movimentacao/adicionarItem")
    suspend fun movementAddItemMov(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body movementAddTask: MovementAddTask
    ): Response<Unit>

    //Finish -->
    @POST("armazem/{idArmazem}/movimentacao/finalizar")
    suspend fun movementFinishMov(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body postRequestModelFinish: MovementFinishAndress
    ): Response<Unit>


    /**-------------------------------INVENTARIO-------------------------------------------------*/
    //Inventario 1 - Retornar tarefas de inventario pendentes -->
    @GET("armazem/{idArmazem}/inventario/abastecimento/pendente")
    suspend fun Inventorypending1(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<List<ResponseInventoryPending1>>

    //Inventario 2 - Processar leitura código de barras -->
    @POST("armazem/{idArmazem}/inventario/abastecimento/processarCodigoBarras")
    suspend fun inventoryQrCode2(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body inventoryReadingProcess: RequestInventoryReadingProcess
    ): Response<ResponseQrCode2>

    //Inventário 3 - Retornar lista dos produtos e volumes do endereço
    @GET("armazem/{idArmazem}/inventario/abastecimento/{idInventario}/contagem/{numeroContagem}/endereco/{idEndereco}")
    suspend fun inventoryResponseRecyclerView(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idInventario") idInventario: Int,
        @Path("numeroContagem") numeroContagem: Int,
        @Path("idEndereco") idEndereco: Int,
    ): Response<ResponseListRecyclerView>

    //Retorna Corrugados -->
    @GET("corrugado")
    suspend fun getCorrugados(
        @Header("Authorization") token: String = TOKEN,
    ): Response<InventoryResponseCorrugados>

    //CRIAR VOLUME A VULSO -->
    @POST("armazem/{idArmazem}/inventario/abastecimento/{idInventario}/contagem/{numeroContagem}/endereco/{idEndereco}/volume/avulso")
    suspend fun inventoryCreateVoidPrinter(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idInventario") idInventario: Int,
        @Path("numeroContagem") numeroContagem: Int,
        @Path("idEndereco") idEndereco: Int,
        @Body createVoidPrinter: CreateVoidPrinter
    ): Response<EtiquetaInventory>

    /**PRECISO IMPLEMENTAR AINDA -->*/
//    @POST("armazem/:idArmazem/tarefa/etiquetagem/processa")
//    @Path("idArmazem") idArmazem: Int = IDARMAZEM

    /**-------------------RECEBIMENTO----------------------------------->*/
    //Recebimento : Transferencia - Receber documento de transferencia -->
    @POST("armazem/{idArmazem}/transferencia/documento/receber")
    suspend fun receiptPost1(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body postDocumentoRequestRec1: PostReciptQrCode1
    ): Response<ReceiptDoc1>

    //Recebimento : Transferencia - Apontar volume recebidoa -->
    @POST("armazem/{idArmazem}/transferencia/documento/recebimento/tarefa/{idTarefa}/apontarItem")
    suspend fun receiptPointed2(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idTarefa") idTarefa: String,
        @Header("Authorization") token: String = TOKEN,
        @Body postReceiptQrCode2: PostReceiptQrCode2
    ): Response<ReceiptDoc1>

    //Recebimento/Transferencia - Finalizar recebimento/conferencia -->
    @POST("armazem/{idArmazem}/transferencia/documento/conferencia/tarefa/{idTarefa}/finalizar")
    suspend fun receipt3(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idTarefa") idTarefa: String,
        @Header("Authorization") token: String = TOKEN,
        @Body postReceiptQrCode3: PostReceiptQrCode3
    ): Response<ReceiptMessageFinish>

    /**------------------------------ETIQUETAGEM------------------------------------------------->*/
    //Etiquetagem 1 - Processa tarefa de etiquetagem do volume
    @POST("armazem/{idArmazem}/tarefa/etiquetagem/processa")
    suspend fun postEtiquetagem1(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body etiquetagempost1: EtiquetagemRequest1
    ): Response<Unit>

    //Etiquetagem 2 - Consulta Etiquetagem Pendente - Pendências por nota fiscal
    @GET("armazem/{idArmazem}/consulta/tarefa/etiquetagem/notasFiscais/pendente")
    suspend fun etiquetagemGet2(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<List<EtiquetagemResponse2>>

    //
    //ETIQUETAGEM 3 -> Pendências por pedidos da nota fiscal
    @POST("armazem/{idArmazem}/consulta/tarefa/etiquetagem/notasFiscais/pedidos/pendente")
    suspend fun postEtiquetagem3(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body etiquetagemRequestModel3: EtiquetagemRequestModel3
    ): Response<List<EtiquetagemResponse3>>

    /**-----------------------------PICKING------------------------------------------------------>*/
    //Picking 1 - Retornar area que possuem tarefas de picking
    @GET("armazem/{idArmazem}/tarefa/picking/area")
    suspend fun getAreaPicking1(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<List<PickingResponse1>>

    //Picking 2- Retornar tarefas de picking da area -->
    @GET("armazem/{idArmazem}/tarefa/picking/area/{idArea}")
    suspend fun getReturnTarefasPicking2(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idArea") idArea: Int,
    ): Response<List<PickingResponse2>>

    //Picking 3 - Apontar item coletatado-->
    @POST("armazem/{idArmazem}/tarefa/picking/area/{idArea}/aponta")
    suspend fun postItemLidoPicking3(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("idArea") idArea: Int,
        @Header("Authorization") token: String = TOKEN,
        @Body picking3: PickingRequest1
    ): Response<Unit>

    //Picking 4 - Retorna agrupado por produto
    @GET("armazem/{idArmazem}/tarefa/picking/agrupadoProduto")
    suspend fun getGroupedProductAgrupadoPicking4(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<List<PickingResponse3>>

    //Picking 5 - Finalizar produto do agrupamento
    @POST("armazem/{idArmazem}/tarefa/picking/produto/finaliza")
    suspend fun postFinalizarPicking5(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
        @Body pickingRequest2: PickingRequest2
    ): Response<Unit>

    /**------------------------DESMONTAGEM DE VOLUMES----------------------------------->*/
    //Desmontagem de Volumes - Retornar tarefas de desmontagem de volumes -->
    @GET("armazem/{idArmazem}/montagem/desmontar/ordem")
    suspend fun getDisassembly1(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
    ): Response<List<DisassemblyResponse1>>

    /**-----------------------------------MONTAGEM DE VOLUMES------------------------------------>*/
    //Montagem de Volumes - Retornar tarefas de montagem de volumes
    @GET("armazem/{idArmazem}/montagem/montar/ordem")
    suspend fun getMountingTask01(
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN,
    ): Response<List<MountingTaskResponse1>>

    /**----------------------------RECEBIMENTO DE PRODUÇAO------------------------------------------*/
    //BUSCA IDS OPERADOR COM PENDENCIAS -->
    @GET("armazem/{idArmazem}/armazenagem/pedido/pendente/{filtrarOperador}/operador/{idOperador}")
    suspend fun getReceiptProduct1(
        @Header("Authorization") token: String = TOKEN ,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("filtrarOperador") filtrarOperador: Boolean,
        @Path("idOperador") idOperador: String
    ): Response<List<ReceiptProduct1>>

    //RECEBIMENTO DE PRODUÇAO - Confere o recebimento dos volumes da produção
    @POST("armazem/{idArmazem}/conferencia/recebimentoProducao")
    suspend fun postReadingReceiptProduct2(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body qrCode: QrCodeReceipt1
    ): Response<Unit>

    //RECEBIMENTO DE PRODUÇÃO - Retornar pedidos itens pendentes de armazenagem
    @GET("armazem/{idArmazem}/armazenagem/pedido/{pedido}/itens/pendente/{filtrarOperador}/operador/{idOperador}")
   suspend fun getReceiptProduct3(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Path("pedido") pedido: String,
        @Path("filtrarOperador") filtrarOperador: Boolean,
        @Path("idOperador") idOperador: String
    ): Response<List<ReceiptProduct2>>

    //Controle de Acesso - Validar permissão de supervisor -->
    @POST("auth/login/validar/supervisor/armazem/{idArmazem}")
   suspend fun postValidAccesReceiptProduct(
        @Header("Authorization") token: String = TOKEN,
        @Path("idArmazem") idArmazem: Int = IDARMAZEM,
        @Body posLoginValidadREceipPorduct: PosLoginValidadREceipPorduct
    ): Response<Unit>

//
//  //RECEBIMENTO - Retorna operadores com tarefas pendentes
//  @GET("armazem/{idArmazem}/operador/armazenagem/pendente")
//  suspend fun getReceiptProduct(
//      @Header("Authorization") token:String = token,
//      @Path("idArmazem") idArmazem: Int = IDARMAZEM,
//  ): Response<List<RecebimentoIdOperador>>


    /** RETROFIT ----------------> */
    companion object {
        private val serviceApi: ServiceApi by lazy {
            val httpOk = HttpLoggingInterceptor()
            httpOk.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
                .connectTimeout(16, TimeUnit.SECONDS)
                .readTimeout(16, TimeUnit.SECONDS)
                .writeTimeout(16, TimeUnit.SECONDS)
                .addInterceptor(httpOk)
                .build()

            val retrofitService = Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            retrofitService.create(ServiceApi::class.java)
        }

        fun getInstance(): ServiceApi {
            return serviceApi
        }

        var baseUrl = "http://10.0.1.111:5002/wms/v1/"
        var TOKEN = ""
        var IDARMAZEM = 0
    }
}