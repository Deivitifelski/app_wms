package com.documentos.wms_beirario

import android.app.Application
import com.documentos.wms_beirario.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApp : Application() {

    /**
     * CLASSE ONDE ADICIONA OS MUDULOS QUE CONTEM INJEÇAO DE DEPENDENCIA CONTIDAS NA -> (DI)
     */
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MyApp)
            modules(
                listOf(
                    mainModule,
                    mainModuleTipoTarefa,
                    mainModuleSeparacao,
                    mainModuleProduct,
                    mainModuleReceiptProduct,
                    mainModulePicking,
                    mainModuleMovimentAndress,
                    mainModuleMountingVol
                )
            )
        }
    }
}