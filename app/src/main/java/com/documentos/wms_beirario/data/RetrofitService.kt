package com.documentos.wms_beirario.data

import com.documentos.wms_beirario.model.armazenagem.ArmazenagemResponse
import com.documentos.wms_beirario.model.armazens.ArmazensResponse
import com.documentos.wms_beirario.model.login.LoginRequest
import com.documentos.wms_beirario.model.login.LoginResponse
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
   suspend fun postLogin(@Body loginRequest: LoginRequest):Response<LoginResponse>

    /**Armazém - Retornar armazens do operador -->*/
    @GET("armazem")
    suspend fun getArmazens(@Header("Authorization") token: String): Response<List<ArmazensResponse>>

    @GET("armazem/{id_armazem}/tipoTarefa")
    fun getTipoTarefa(
        @Path("id_armazem") id_armazem: Int,
        @Header("Authorization") token: String
    ) : Call<List<TipoTarefaResponseItem>>

    /**---------------------------------ARMAZENAGEM-----------------------------------------------*/
    //ARMAZENAGEM -->
    @GET("armazem/{idArmazem}/armazenagem/tarefa/pendente")
    fun getArmazenagem(
        @Path("idArmazem") idarmazem: Int,
        @Header("Authorization") token: String
    ): Call<List<ArmazenagemResponse>>


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
    }
}