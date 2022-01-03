package com.documentos.wms_beirario.data

import android.content.Context

open class CustomSharedPreferences(context: Context) {


    companion object {
        const val ID_INVENTORY = "ID_CLICK_INVENTORY1"
        const val NOME_SUPERVISOR_LOGADO = "name_supervisor"
        const val ID_OPERADOR = "id_operador"
        const val ID_TAREFA = "id_tarefa_selecionada"
        const val ID_ARMAZEM = "id_armazem"
        const val TOKEN = "token_user"
        const val NAME_USER = "nome_digitado_usuario_login"
        const val SENHA_USER = "senha_do_usuario_login"
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


}