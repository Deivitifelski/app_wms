package com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityRfidLeituraEpcBinding
import com.documentos.wms_beirario.ui.recebimentoRFID.RecebimentoRfidActivity
import com.documentos.wms_beirario.ui.rfid_recebimento.detalhesEpc.DetalheCodigoEpcActivity
import com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.adapter.LeituraRfidAdapter
import com.documentos.wms_beirario.utils.extensions.alertConfirmation
import com.documentos.wms_beirario.utils.extensions.extensionStartActivity
import com.documentos.wms_beirario.utils.extensions.seekBarPowerRfid
import com.documentos.wms_beirario.utils.extensions.toastDefault
import com.google.android.material.chip.Chip
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE
import com.zebra.rfid.api3.ENVIRONMENT_MODE
import com.zebra.rfid.api3.HANDHELD_TRIGGER_EVENT_TYPE
import com.zebra.rfid.api3.RFIDReader
import com.zebra.rfid.api3.RFID_EVENT_TYPE
import com.zebra.rfid.api3.ReaderDevice
import com.zebra.rfid.api3.Readers
import com.zebra.rfid.api3.RfidEventsListener
import com.zebra.rfid.api3.RfidReadEvents
import com.zebra.rfid.api3.RfidStatusEvents
import com.zebra.rfid.api3.START_TRIGGER_TYPE
import com.zebra.rfid.api3.STATUS_EVENT_TYPE
import com.zebra.rfid.api3.STOP_TRIGGER_TYPE
import com.zebra.rfid.api3.TriggerInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RfidLeituraEpcActivity : AppCompatActivity(), RfidEventsListener {

    private lateinit var binding: ActivityRfidLeituraEpcBinding
    private lateinit var adapterLeituras: LeituraRfidAdapter
    private lateinit var rfidReader: RFIDReader
    private val TAG = "RFID"
    private var reader: Readers? = null
    private var readerList: ArrayList<ReaderDevice>? = null
    private var powerRfid: Int = 50
    private lateinit var token: String
    private var idArmazem: Int? = null
    private var nivelAntenna: Int = 1
    private lateinit var sharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRfidLeituraEpcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connectReader()
        setupShared()
        clickButtonConfig()
        setupAdapter()
        cliqueChips()
        clickButtonLimpar()
        clickButtonFinalizar()
        clickRfidAntenna()


    }

    private fun setupShared() {
        sharedPreferences = CustomSharedPreferences(this)
        sharedPreferences.apply {
            token = getString(CustomSharedPreferences.TOKEN) as String
            idArmazem = getInt(CustomSharedPreferences.ID_ARMAZEM)
            idArmazem = getInt(CustomSharedPreferences.ID_ARMAZEM)
        }
    }

    private fun clickRfidAntenna() {
        binding.iconRfidSinal.setOnClickListener {
            powerRfid = sharedPreferences.getInt(CustomSharedPreferences.POWER_RFID)
            nivelAntenna = sharedPreferences.getInt(CustomSharedPreferences.NIVEL_ANTENNA_RFID)
            seekBarPowerRfid(powerRfid, nivelAntenna) { newPowerUser, newPowerAnttena, nivel ->
                powerRfid = newPowerUser
                sharedPreferences.saveInt(CustomSharedPreferences.POWER_RFID, newPowerUser)
                sharedPreferences.saveInt(CustomSharedPreferences.NIVEL_ANTENNA_RFID, nivel)
                setRfidPowerLevel(newPowerAnttena, nivel)
            }
        }
    }

    private fun setRfidPowerLevel(powerLevel: Int, nivelAntenna: Int) {
        try {
            val maxPowerLevel = 270
            if (powerLevel < 0 || powerLevel > maxPowerLevel) {
                handleConnectionFailure("Nível de potência fora do intervalo permitido: $powerLevel. Deve estar entre 0 e $maxPowerLevel.")
                return
            }
            if (rfidReader.isConnected) {
                // Obter a configuração da antena 1
                val antennaConfig = rfidReader.Config.Antennas.getAntennaRfConfig(1)

                // Ajustar o modo de RF com base no nível da antena
                when (nivelAntenna) {
                    1 -> {
                        // Modo 1: Alta taxa de transferência (curta distância)
                        antennaConfig.environment_mode = ENVIRONMENT_MODE.HIGH_INTERFERENCE
                    }

                    2 -> {
                        // Modo 2: Balanceado (sensibilidade vs taxa de leitura)
                        antennaConfig.environment_mode = ENVIRONMENT_MODE.VERY_HIGH_INTERFERENCE
                    }

                    3 -> {
                        // Modo 3: Sensibilidade máxima (tags difíceis de serem lidas)
                        antennaConfig.environment_mode = ENVIRONMENT_MODE.LOW_INTERFERENCE
                    }

                    else -> {
                        handleConnectionFailure("Nível da antena inválido: $nivelAntenna.")
                        return
                    }
                }

                antennaConfig.transmitPowerIndex = powerLevel
                antennaConfig.receiveSensitivityIndex = 1000
                Log.e(TAG, "getrfModeTableIndex: ${antennaConfig.getrfModeTableIndex()}")
                rfidReader.Config.Antennas.setAntennaRfConfig(1, antennaConfig)


            } else {
                handleConnectionFailure("O leitor RFID não está conectado.")
            }
        } catch (e: Exception) {
            handleConnectionFailure("Erro ao tentar alterar a potência do leitor: ${e.message}")
        }
    }


    private fun connectReader(reconectando: Boolean = false) {
        CoroutineScope(Dispatchers.Main).launch {
            if (reconectando) {
                binding.iconRfidSinal.isVisible = false
                binding.progressRfid.visibility = View.VISIBLE
                toastDefault(message = "Buscando Leitor de RFID...")
            }

            // Agora inicie a operação assíncrona
            withContext(Dispatchers.IO) {
                try {
                    reader = Readers(this@RfidLeituraEpcActivity, ENUM_TRANSPORT.SERVICE_USB)
                    readerList = reader?.GetAvailableRFIDReaderList()

                    if (readerList.isNullOrEmpty()) {
                        withContext(Dispatchers.Main) {
                            handleConnectionFailure("Não foi possível conectar, lista de leitores vazia")
                        }
                        return@withContext
                    }
                    val readerDevice: ReaderDevice = readerList!![0]
                    rfidReader = readerDevice.rfidReader
                    rfidReader.connect()

                    withContext(Dispatchers.Main) {
                        if (rfidReader.isConnected) {
                            handleConnectionSuccess(readerDevice.name)
                        } else {
                            handleConnectionFailure("Não foi possível conectar ao leitor")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        handleConnectionFailure("Ocorreu um erro ao conectar:\n${e.localizedMessage}")
                    }
                }
            }
        }
    }


    private fun handleConnectionSuccess(deviceName: String) {
        binding.progressRfid.isVisible = false
        binding.iconRfidSinal.isVisible = true
        toastDefault(message = "Conectado com sucesso: $deviceName")
        binding.iconRfidSinal.setImageResource(R.drawable.icon_rfid_sucess_connect)
        configureReader()
    }

    private fun handleConnectionFailure(message: String) {
        binding.progressRfid.isVisible = false
        binding.iconRfidSinal.isVisible = true
        toastDefault(message = message)
        binding.iconRfidSinal.setImageResource(R.drawable.icon_rfid_not_connect)
    }


    /**Configurações do listner */
    private fun configureReader() {
        try {
            rfidReader.Events.addEventsListener(this)
            rfidReader.Events.setInventoryStopEvent(true)
            rfidReader.Events.setInventoryStartEvent(true)
            rfidReader.Events.setScanDataEvent(true)
            rfidReader.Events.setReaderDisconnectEvent(true)
            rfidReader.Events.setBatteryEvent(true)
            rfidReader.Events.setHandheldEvent(true)
            rfidReader.Events.setTagReadEvent(true)
            rfidReader.Events.setInfoEvent(true)
            rfidReader.Events.setPowerEvent(true)
            rfidReader.Events.setTemperatureAlarmEvent(true)
            rfidReader.Events.setAttachTagDataWithReadEvent(true)
            val triggerInfo = TriggerInfo()
            triggerInfo.StartTrigger.triggerType = START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE
            triggerInfo.StopTrigger.triggerType = STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE
            rfidReader.Config.setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, true)
            rfidReader.Config.startTrigger = triggerInfo.StartTrigger
            rfidReader.Config.stopTrigger = triggerInfo.StopTrigger

            //--------------
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Erro ao configurar o leitor: ${e.message}")
            Toast.makeText(this, "Erro ao configurar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clickButtonConfig() {
        binding.iconConfig.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.menu_rfid_recebimento, popup.menu)

            // Definir o comportamento ao clicar em cada item
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_option_1 -> {
                        true
                    }

                    R.id.menu_option_2 -> {
                        connectReader(reconectando = true)
                        true
                    }

                    R.id.menu_option_3 -> {
                        disconnectRFD()
                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }
    }

    private fun clickButtonFinalizar() {
        binding.buttonFinalizar.setOnClickListener {
            extensionStartActivity(RecebimentoRfidActivity())
        }
    }

    private fun clickButtonLimpar() {
        binding.buttonClear.setOnClickListener {
            alertConfirmation(message = "Deseja limpar as leituras e iniciar novamente?",
                actionNo = {},
                actionYes = {})
        }
    }

    private fun setupAdapter() {
        cliqueItemDaLista()
        binding.rvItemEpcRecebimento.apply {
            adapter = adapterLeituras
            layoutManager = LinearLayoutManager(this@RfidLeituraEpcActivity)
        }
    }

    private fun cliqueItemDaLista() {
        adapterLeituras = LeituraRfidAdapter {
            extensionStartActivity(DetalheCodigoEpcActivity())
        }
    }

    private fun cliqueChips() {
        binding.chipRelacionados.isChecked = true
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chipId = checkedIds[0]
                val chip = group.findViewById<Chip>(chipId)
                when (chip.id) {
                    R.id.chip_faltando -> {}
                    R.id.chip_encontrados -> {}
                    R.id.chip_nao_relacionado -> {}
                    R.id.chip_relacionados -> {}
                }
            } else {
                Toast.makeText(this, "Nenhum Chip selecionado", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun eventReadNotify(data: RfidReadEvents?) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                Log.e(TAG, "TAG RECEBIDA: ${data?.readEventData?.tagData?.tagID}")
            }
        }

    }

    override fun eventStatusNotify(rfidStatusEvents: RfidStatusEvents?) {
        if (rfidStatusEvents != null) {
            val eventType = rfidStatusEvents.StatusEventData?.statusEventType
            val eventData = rfidStatusEvents.StatusEventData
            Log.e(TAG, "Evento recebido: ${eventData.toString()}")
            Log.i(TAG, "Tipo de evento recebido: $eventType")


            when (eventType) {
                STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT -> {
                    val triggerEvent = eventData?.HandheldTriggerEventData?.handheldEvent
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            Log.i(TAG, "Evento de gatilho recebido: $triggerEvent")
                        }
                    }

                    when (triggerEvent) {
                        HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED -> {
                            startReading()  // Gatilho pressionado, iniciar leitura
                            CoroutineScope(Dispatchers.IO).launch {
                                withContext(Dispatchers.Main) {
                                    Log.i(TAG, "Iniciando leitura (Trigger pressionado)")
                                }
                            }
                        }

                        HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED -> {
                            stopReading()  // Gatilho liberado, parar leitura
                            CoroutineScope(Dispatchers.IO).launch {
                                withContext(Dispatchers.Main) {
                                    Log.i(TAG, "Parando leitura (Trigger liberado)")
                                }
                            }
                        }
                    }
                }

                RFID_EVENT_TYPE.DISCONNECTION_EVENT -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            Log.e(TAG, "Leitor RFID desconectado")
                            toastDefault(message = "RFID_EVENT_TYPE Desconectado do dispositivo RFID")
                        }
                    }
                }

                STATUS_EVENT_TYPE.POWER_EVENT -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            toastDefault(message = "STATUS_EVENT_TYPE Alterada a potência do leitor RFID: $powerRfid%")
                        }
                    }
                }

                STATUS_EVENT_TYPE.DISCONNECTION_EVENT -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            Log.e(TAG, "Leitor RFID desconectado")
                            toastDefault(message = "Desconectado do dispositivo RFID")
                        }
                    }
                }

                STATUS_EVENT_TYPE.BATTERY_EVENT -> {
                    val batteryLevel = eventData?.BatteryData?.level ?: "Nível desconhecido"
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            Log.w(
                                TAG,
                                "Alerta de bateria: Nível da bateria do dispositivo está baixo ($batteryLevel%)"
                            )
                            toastDefault(message = "Bateria baixa: $batteryLevel%")
                        }
                    }
                }

                STATUS_EVENT_TYPE.TEMPERATURE_ALARM_EVENT -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            Log.w(TAG, "Alerta de temperatura: O dispositivo está superaquecendo!")
                            toastDefault(message = "Atenção: Superaquecimento detectado!")
                        }
                    }
                }

                STATUS_EVENT_TYPE.BUFFER_FULL_WARNING_EVENT -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            Log.w(TAG, "Aviso: O buffer de leitura está quase cheio.")
                            toastDefault(message = "Buffer de leitura quase cheio.")
                        }
                    }
                }

                else -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            Log.w(TAG, "Evento desconhecido recebido: $eventType")
                        }
                    }
                }
            }
        }
    }


    private fun stopReading() {
        try {
            rfidReader.Actions.Inventory.stop() // Para a leitura
            Log.d(TAG, "Leitura de tags parada")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Erro ao parar leitura: ${e.message}")
        }
    }

    private fun disconnectRFD() {
        try {
            if (rfidReader.isConnected) {
                rfidReader.Events.removeEventsListener(this);
                rfidReader.disconnect()
                rfidReader.Dispose()
                reader = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "onDestroy: ${e.localizedMessage}")
        }
    }


    private fun startReading() {
        try {
            rfidReader.Actions.Inventory.perform()
            Log.d(TAG, "Iniciando leitura de tags")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Erro ao iniciar leitura: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnectRFD()
    }


}
