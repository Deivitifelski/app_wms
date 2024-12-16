package com.documentos.wms_beirario.data

import android.util.Log
import kotlinx.coroutines.handleCoroutineException
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class RetrofitClient {
    companion object {
        var baseUrl = "http://srvcol-hml.beirario.intranet:5002/wms/"
    }

    private var retrofit: Retrofit? = null

    fun getClient(): ServiceApi {
        val httpOk = HttpLoggingInterceptor()
        httpOk.level = HttpLoggingInterceptor.Level.BODY

        if (retrofit == null) {
            val httpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .callTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(httpOk)
                .addInterceptor { chain ->
                    val request = chain.request()
                    val host = request.url.host

                    // Lista de hosts a serem bloqueados
                    val blockedHosts = listOf(
                        "ssdk-sg.pangle.io",
                        "tnc16-alisg.isnssdk.com",
                        "sf16-static.i18n-pglstatp.com"
                    )

                    if (blockedHosts.contains(host)) {
                        Log.e("->", "\nRequisição bloqueada para o host: $host\n" )
                        throw IOException("Requisição bloqueada para o host: $host")
                    }

                    chain.proceed(request)
                }
                .retryOnConnectionFailure(true)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }
        return retrofit!!.create(ServiceApi::class.java)
    }
}

