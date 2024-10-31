package com.documentos.wms_beirario.ui.rfid_recebimento

import android.content.Context

class RFIDReader private constructor(private val context: Context) {

    // Propriedades para armazenar as configurações
    private var powerLevel: Int = 0
    private var interferenceMode: String = "LOW_INTERFERENCE"

    // Inicialização do leitor RFID
    init {
        loadSettings() // Carregar configurações ao inicializar
        connect() // Conectar ao dispositivo RFID
    }

    companion object {
        @Volatile
        private var instance: RFIDReader? = null

        fun getInstance(context: Context): RFIDReader {
            return instance ?: synchronized(this) {
                instance ?: RFIDReader(context.applicationContext).also { instance = it }
            }
        }
    }

    // Método para conectar o leitor
    fun connect() {
        // Lógica para conectar ao leitor RFID
        // Aplique as configurações ao conectar, se necessário
        applySettings()
    }

    // Método para desconectar o leitor
    fun disconnect() {
        // Lógica para desconectar do leitor RFID
    }

    // Método para alterar o nível de potência
    fun setPowerLevel(level: Int) {
        powerLevel = level
        saveSettings() // Salvar configurações sempre que forem alteradas
        applySettings()
    }

    // Método para alterar o modo de interferência
    fun setInterferenceMode(mode: String) {
        interferenceMode = mode
        saveSettings() // Salvar configurações sempre que forem alteradas
        applySettings()
    }

    // Método privado para aplicar as configurações
    private fun applySettings() {
        // Implementar a lógica para enviar as novas configurações para o leitor
        // Exemplo: enviar comandos específicos para o dispositivo RFID
    }

    // Método para salvar configurações em SharedPreferences
    private fun saveSettings() {
        val sharedPreferences = context.getSharedPreferences("RFIDReaderSettings", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("powerLevel", powerLevel)
            putString("interferenceMode", interferenceMode)
            apply()
        }
    }

    // Método para carregar configurações de SharedPreferences
    private fun loadSettings() {
        val sharedPreferences = context.getSharedPreferences("RFIDReaderSettings", Context.MODE_PRIVATE)
        powerLevel = sharedPreferences.getInt("powerLevel", 0) // Valor padrão 0
        interferenceMode = sharedPreferences.getString("interferenceMode", "LOW_INTERFERENCE") ?: "LOW_INTERFERENCE"
    }
}
