package com.documentos.wms_beirario.data

import com.documentos.wms_beirario.model.armazenagem.ArmazenagemResponse
import com.documentos.wms_beirario.model.armazens.ArmazensResponse
import com.documentos.wms_beirario.model.codBarras.CodigodeBarrasResponse
import com.documentos.wms_beirario.model.inventario.RequestInventoryReadingProcess
import com.documentos.wms_beirario.model.inventario.ResponseInventoryPending1
import com.documentos.wms_beirario.model.inventario.ResponseQrCode2
import com.documentos.wms_beirario.model.login.LoginRequest
import com.documentos.wms_beirario.model.login.LoginResponse
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.*
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

interface RetrofitService {
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
    fun getArmazenagem(
        @Path("idArmazem") idarmazem: Int = IDARMAZEM,
        @Header("Authorization") token: String = TOKEN
    ): Call<List<ArmazenagemResponse>>

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

    companion object {
        private val retrofitService: RetrofitService by lazy {
            val baseUrl = "http://10.0.1.111:5002/wms/v1/"
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

            retrofitService.create(RetrofitService::class.java)
        }

        fun getInstance(): RetrofitService {
            return retrofitService
        }

        var TOKEN = ""
        var IDARMAZEM = 0
    }
}