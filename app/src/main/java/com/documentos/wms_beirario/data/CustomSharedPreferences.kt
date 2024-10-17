package com.documentos.wms_beirario.data

import android.content.Context

open class CustomSharedPreferences(context: Context) {



    companion object {
        val ACTION_DATAWEDGE = "android.intent.action.DATAWEDGE"
        const val ID_INVENTORY = "ID_CLICK_INVENTORY1"
        const val ID_INVENTORY1 = "ID_CLICK_INVENTORY11"
        const val NOME_SUPERVISOR_LOGADO = "name_supervisor"
        const val ID_OPERADOR = "id_operador"
        const val ID_TAREFA = "id_tarefa_selecionada"
        const val ID_ARMAZEM = "id_armazem"
        const val POWER_RFID = "potencia antena"
        const val TOKEN = "token_user"
        const val NAME_USER = "nome_digitado_usuario_login"
        const val SENHA_USER = "senha_do_usuario_login"
        const val DEVICE_PRINTER = "ultima_printer_select_user"
    }

    //TODO Criando uma variavel para ser usada dentro das funçoes --->
    private val mSharedPreference = context.getSharedPreferences("WMS", Context.MODE_PRIVATE)

    fun saveString(key: String, value: String) {
        mSharedPreference.edit().putString(key, value).apply()
    }

    fun getString(key: String): String? {
        return mSharedPreference.getString(key, null)
    }

    fun saveInt(key: String, value: Int) {
        mSharedPreference.edit().putInt(key, value).apply()
    }

    fun getInt(key: String): Int {
        return mSharedPreference.getInt(key, 0)
    }

    fun saveBoolean(key: String, value: Boolean) {
        mSharedPreference.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String): Boolean {
        return mSharedPreference.getBoolean(key, false)

    }
}