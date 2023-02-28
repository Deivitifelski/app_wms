package com.documentos.wms_beirario.data

import kotlinx.coroutines.handleCoroutineException
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {
    companion object {
        var baseUrl = "http://10.0.1.111:5002/wms/"
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
