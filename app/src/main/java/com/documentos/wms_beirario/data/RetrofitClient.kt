package com.documentos.wms_beirario.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class RetrofitClient(private val context: Context? = null) {
    companion object {
        var baseUrl = "http://srvcol-hml.beirario.intranet:5002/wms/"
    }

    private var retrofit: Retrofit? = null

    fun getClient(): ServiceApi {
        val httpOk = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

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

                    val dns = try {
                        val inetAddress = InetAddress.getByName(host)
                        inetAddress.hostAddress
                    } catch (e: Exception) {
                        "DNS não resolvido"
                    }

                    val blockedHosts = listOf(
                        "ssdk-sg.pangle.io",
                        "tnc16-alisg.isnssdk.com",
                        "sf16-static.i18n-pglstatp.com"
                    )

                    // Registrar o log com host, DNS e data de acesso
                    val logEntry = createLogEntry(dns = dns, host = host)
                    if (context != null) {
                        writeToLogFile(logEntry)
                    }

                    if (blockedHosts.contains(host)) {
                        Log.e("->", "\nRequisição bloqueada para o host: $host\n")
                        val logEntryErrro = "Data: ${Date()} | Host: Requisição bloqueada para o host: $host\n"
                        if (context != null) {
                            writeToLogFile(logEntryErrro)
                        }
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


    private fun createLogEntry(host: String, dns: String): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("pt", "BR"))
        val formattedDate = dateFormat.format(Date())
        val logEntry = "Data: $formattedDate | Host: $host | DNS: $dns\n"
        return logEntry
    }

    private fun writeToLogFile(logEntry: String) {
        try {
            // Verifica se o armazenamento externo está disponível
            val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            // Cria o arquivo de log no diretório de Documentos
            val logFile = File(storageDir, "network_logs_wms.txt")
            // Cria o diretório, se não existir
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            // Adiciona a entrada ao arquivo
            logFile.appendText(logEntry)
        } catch (e: IOException) {
            Log.e("LogError", "Erro ao gravar no arquivo de log: ${e.message}")
        }
    }
}
