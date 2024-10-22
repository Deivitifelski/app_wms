package com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityRfidLeituraEpcBinding
import com.documentos.wms_beirario.model.recebimentoRfid.RecebimentoRfidEpcs
import com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.adapter.LeituraRfidAdapter
import com.documentos.wms_beirario.utils.extensions.alertConfirmation
import com.documentos.wms_beirario.utils.extensions.seekBarPowerRfid
import com.documentos.wms_beirario.utils.extensions.showAlertDialogOpcoesRfidEpcClick
import com.documentos.wms_beirario.utils.extensions.toastDefault
import com.google.android.material.chip.Chip
import com.zebra.rfid.api3.BEEPER_VOLUME
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE
import com.zebra.rfid.api3.ENVIRONMENT_MODE
import com.zebra.rfid.api3.HANDHELD_TRIGGER_EVENT_TYPE
import com.zebra.rfid.api3.INVENTORY_STATE
import com.zebra.rfid.api3.RFIDReader
import com.zebra.rfid.api3.ReaderDevice
import com.zebra.rfid.api3.Readers
import com.zebra.rfid.api3.RfidEventsListener
import com.zebra.rfid.api3.RfidReadEvents
import com.zebra.rfid.api3.RfidStatusEvents
import com.zebra.rfid.api3.SESSION
import com.zebra.rfid.api3.SL_FLAG
import com.zebra.rfid.api3.START_TRIGGER_TYPE
import com.zebra.rfid.api3.STATUS_EVENT_TYPE
import com.zebra.rfid.api3.STOP_TRIGGER_TYPE
import com.zebra.rfid.api3.TagData
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
    private var nivelAntenna: Int = 3
    private var tagReaders: Int = 0
    private var lisTags = mutableListOf<RecebimentoRfidEpcs>()
    private lateinit var sharedPreferences: CustomSharedPreferences
    private var progressBar: ProgressBar? = null
    private lateinit var textRssiValue: TextView
    private lateinit var proximityDialog: AlertDialog
    private var tagSelecionada: TagData? = null
    private val uniqueTagIds = HashSet<String>()
    private val tagList = listOf(
        RecebimentoRfidEpcs(TagData().apply { tagID = "D88379771003521000095542" }),
        RecebimentoRfidEpcs(TagData().apply { tagID = "D88379771003521000095545" }),
        RecebimentoRfidEpcs(TagData().apply { tagID = "D88379771003521000095540" }),
        RecebimentoRfidEpcs(TagData().apply { tagID = "D88379771003521000095538" }),
        RecebimentoRfidEpcs(TagData().apply { tagID = "E28011608000021C4C182B4E" }),
        RecebimentoRfidEpcs(TagData().apply { tagID = "D88379771003521000095543" }),
        RecebimentoRfidEpcs(TagData().apply { tagID = "281134940001300000000919" }),
        RecebimentoRfidEpcs(TagData().apply { tagID = "D88379771003521000095539" }),
        RecebimentoRfidEpcs(TagData().apply { tagID = "E28011608000021C4C182B5E" }),
        RecebimentoRfidEpcs(TagData().apply { tagID = "D88379771003521000095544" }),
        RecebimentoRfidEpcs(TagData().apply { tagID = "AAA100002FF63C2600000910" }),
        RecebimentoRfidEpcs(TagData().apply { tagID = "303B028240D7A3C000000006" }),
        RecebimentoRfidEpcs(TagData().apply { tagID = "AAA1000027C592AB00000218" })
    )


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
            powerRfid = sharedPreferences.getInt(CustomSharedPreferences.POWER_RFID)
            nivelAntenna = sharedPreferences.getInt(CustomSharedPreferences.NIVEL_ANTENNA_RFID)
        }

    }

    private fun clickRfidAntenna() {
        binding.iconRfidSinal.setOnClickListener {
            seekBarPowerRfid(powerRfid, nivelAntenna) { powerMascaraUser, newPowerAnttena, nivel ->
                powerRfid = newPowerAnttena
                nivelAntenna = nivel
                sharedPreferences.saveInt(CustomSharedPreferences.POWER_RFID, powerMascaraUser)
                sharedPreferences.saveInt(CustomSharedPreferences.NIVEL_ANTENNA_RFID, nivel)
                configureRfidReader(
                    transmitPowerIndex = newPowerAnttena,
                    rfModeTableIndex = nivel,
                    session = SESSION.SESSION_S0,
                    inventoryState = INVENTORY_STATE.INVENTORY_STATE_A,
                    slFlag = SL_FLAG.SL_ALL
                )
            }
        }
    }


    private fun configureRfidReader(
        transmitPowerIndex: Int,
        rfModeTableIndex: Int,
        session: SESSION,
        inventoryState: INVENTORY_STATE,
        slFlag: SL_FLAG
    ) {
        try {
            // Configurar a potência de transmissão da antena
            val numAntennas = rfidReader.ReaderCapabilities.numAntennaSupported
            Log.e(TAG, "NUM_ANTENNAS: ${numAntennas}")
            val config = rfidReader.Config.Antennas.getAntennaRfConfig(1)
            config.transmitPowerIndex = transmitPowerIndex
            config.environment_mode = ENVIRONMENT_MODE.HIGH_INTERFERENCE
            rfidReader.Config.Antennas.setAntennaRfConfig(1, config)
            Log.d("RFID_CONFIG", "Potência ajustada para o índice: $transmitPowerIndex")
            rfidReader.Config.beeperVolume = BEEPER_VOLUME.QUIET_BEEP
            // Configurar o modo RF da antena
            config.setrfModeTableIndex(rfModeTableIndex.toLong())
            rfidReader.Config.Antennas.setAntennaRfConfig(1, config)
            Log.d("RFID_CONFIG", "Modo RF ajustado para o índice: $rfModeTableIndex")

            // Configurar o controle de singulação
            val singulationControl = rfidReader.Config.Antennas.getSingulationControl(1)
            singulationControl.session = session
            singulationControl.Action.inventoryState = inventoryState
            singulationControl.Action.slFlag = slFlag
            rfidReader.Config.Antennas.setSingulationControl(1, singulationControl)

            Log.d(
                "RFID_CONFIG",
                "Controle de singulação ajustado: Sessão = $session, Estado do inventário = $inventoryState, SL Flag = $slFlag"
            )

            // Deletar pre-filtros se necessário
            rfidReader.Actions.PreFilters.deleteAll()
            Log.d("RFID_CONFIG", "Todos os pre-filtros deletados.")

        } catch (e: Exception) {
            Log.e("RFID_CONFIG_ERROR", "Erro ao configurar o leitor RFID: ${e.message}")
            e.printStackTrace()
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
            configureRfidReader(
                transmitPowerIndex = rfidReader.ReaderCapabilities.transmitPowerLevelValues.size - 1,
                rfModeTableIndex = nivelAntenna, // Ajuste conforme necessário
                session = SESSION.SESSION_S1,
                inventoryState = INVENTORY_STATE.INVENTORY_STATE_A,
                slFlag = SL_FLAG.SL_ALL
            )
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
        adapterLeituras.updateData(tagList)
    }

    private fun cliqueItemDaLista() {
        adapterLeituras = LeituraRfidAdapter { tag ->
            tagSelecionada = tag
            showAlertDialogOpcoesRfidEpcClick(tag) { opcao ->
                if (opcao == 0) {
                    //detalhes
                } else {
                    //localizar
                    showProximityDialog()
                }
            }
        }
    }

    private fun showProximityDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_tag_proximity, null)

        // Inicializando elementos do layout do dialog
        progressBar = dialogView.findViewById(R.id.progressBarProximity)
        textRssiValue = dialogView.findViewById(R.id.textRssiValue)

        builder.setView(dialogView)
            .setTitle("Localizar a tag:\n${tagSelecionada?.tagID ?: "-"}")
            .setNegativeButton("Fechar") { dialog, _ ->
                dialog.dismiss() // Fecha o diálogo quando pressionado
            }

        proximityDialog = builder.create()
        proximityDialog.show() // Exibe o diálogo
    }

    // Função para atualizar o progresso e o valor de RSSI
    private fun updateProximity(rssi: Int) {
        try {
            if (progressBar != null) {
                val proximityPercentage = calculateProximityPercentage(rssi)
                val currentProgress = progressBar!!.progress
                val animation = ObjectAnimator.ofInt(
                    progressBar,
                    "progress",
                    currentProgress,
                    proximityPercentage
                )
                animation.duration = 300 // Duração da animação
                animation.interpolator = DecelerateInterpolator()
                animation.addUpdateListener { animator ->
                    val animatedValue = animator.animatedValue as Int
                    textRssiValue.text =
                        "Proximidade: $animatedValue%" // Atualiza o texto em cada frame da animação
                }
                animation.start()
            }

        } catch (e: Exception) {
            toastDefault(message = "Ocorreu um erro ao trazer a localizacao da tag")
        }
    }


    // Função que converte o RSSI em um valor de porcentagem
    private fun calculateProximityPercentage(rssi: Int): Int {
        val adjustedRssi = rssi.coerceIn(-90, -30)
        return ((adjustedRssi + 90) * (100f / 60f)).toInt() // mapeia corretamente
    }


    private fun cliqueChips() {
        binding.chipRelacionados.isChecked = true
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chipId = checkedIds[0]
                val chip = group.findViewById<Chip>(chipId)
                when (chip.id) {
                    R.id.chip_relacionados -> {
                        adapterLeituras.filter("R")
                    }

                    R.id.chip_encontrados -> {
                        adapterLeituras.filter("E")
                    }

                    R.id.chip_nao_relacionado -> {
                        adapterLeituras.filter("N")
                    }

                    R.id.chip_faltando -> {
                        adapterLeituras.filter("F")
                    }
                }
            } else {
                Toast.makeText(this, "Nenhum Chip selecionado", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Lista para armazenar apenas os tagIDs únicos
// Use um HashSet para garantir a unicidade dos tagIDs e melhorar a performance de busca


    override fun eventReadNotify(data: RfidReadEvents?) {
        val tag = data?.readEventData?.tagData ?: return // Evitar processamento se a tag for nula
        tagReaders++

        CoroutineScope(Dispatchers.IO).launch {
            if (uniqueTagIds.add(tag.tagID)) {
                lisTags.add(RecebimentoRfidEpcs(tag))

                withContext(Dispatchers.Main) {
                    binding.textNf.text = "Qtd tags lidas: $tagReaders"
                    adapterLeituras.containsEpc(tag.tagID)
                    binding.textRemessa.text = "Qtd tags encontradas: ${lisTags.size}"
                }
            }

            lisTags.forEach {
                Log.e(TAG, it.tagData.tagID)
            }

            tagSelecionada?.let {
                if (tag.tagID == it.tagID) {
                    updateProximity(tag.peakRSSI.toInt() ?: 0)
                    Log.d(TAG, "igual: ${tag.peakRSSI}")
                }
            }
            Log.d(TAG, "PEAKRSSI: ${tag.peakRSSI}")
        }
    }


    override fun eventStatusNotify(rfidStatusEvents: RfidStatusEvents?) {
        if (rfidStatusEvents != null) {
            val eventType = rfidStatusEvents.StatusEventData?.statusEventType
            val eventData = rfidStatusEvents.StatusEventData
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
                                    updateProximity(-90)
                                }
                            }
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
