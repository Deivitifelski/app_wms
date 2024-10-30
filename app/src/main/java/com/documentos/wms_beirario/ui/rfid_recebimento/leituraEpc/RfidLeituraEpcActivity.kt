package com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc

import android.animation.ObjectAnimator
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityRfidLeituraEpcBinding
import com.documentos.wms_beirario.databinding.DialogTagProximityBinding
import com.documentos.wms_beirario.model.recebimentoRfid.RecebimentoRfidEpcResponse
import com.documentos.wms_beirario.model.recebimentoRfid.ResponseGetRecebimentoNfsPendentes
import com.documentos.wms_beirario.repository.recebimentoRfid.RecebimentoRfidRepository
import com.documentos.wms_beirario.ui.rfid_recebimento.detalhesEpc.DetalheCodigoEpcActivity
import com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.adapter.LeituraRfidAdapter
import com.documentos.wms_beirario.ui.rfid_recebimento.viewModel.RecebimentoRfidViewModel
import com.documentos.wms_beirario.utils.extensions.alertConfirmation
import com.documentos.wms_beirario.utils.extensions.alertDefaulError
import com.documentos.wms_beirario.utils.extensions.alertDefaulSimplesError
import com.documentos.wms_beirario.utils.extensions.configureReader
import com.documentos.wms_beirario.utils.extensions.configureRfidReader
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation
import com.documentos.wms_beirario.utils.extensions.progressConected
import com.documentos.wms_beirario.utils.extensions.releaseSoundPool
import com.documentos.wms_beirario.utils.extensions.seekBarPowerRfid
import com.documentos.wms_beirario.utils.extensions.showAlertDialogOpcoesRfidEpcClick
import com.documentos.wms_beirario.utils.extensions.somBeepRfidPool
import com.documentos.wms_beirario.utils.extensions.toastDefault
import com.google.android.material.chip.Chip
import com.zebra.rfid.api3.BEEPER_VOLUME
import com.zebra.rfid.api3.ENUM_TRANSPORT
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
import com.zebra.rfid.api3.STATUS_EVENT_TYPE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RfidLeituraEpcActivity : AppCompatActivity(), RfidEventsListener {

    private lateinit var binding: ActivityRfidLeituraEpcBinding
    private lateinit var adapterLeituras: LeituraRfidAdapter
    private lateinit var rfidReader: RFIDReader
    private lateinit var viewModel: RecebimentoRfidViewModel
    private val TAG = "RFID"
    private var reader: Readers? = null
    private var readerList: ArrayList<ReaderDevice>? = null
    private lateinit var listIdDoc: ArrayList<ResponseGetRecebimentoNfsPendentes>
    private var powerRfid: Int = 150
    private lateinit var token: String
    private var idArmazem: Int? = null
    private var nivelAntenna: Int = 3
    private var tagReaders: Int = 0
    private lateinit var sharedPreferences: CustomSharedPreferences
    private var progressBar: ProgressBar? = null
    private lateinit var textRssiValue: TextView
    private lateinit var proximityDialog: AlertDialog
    private var epcSelected: String? = null
    private var isShowModalTagLocalization = false
    private val uniqueTagIds = HashSet<String>()
    private var listOfValueRelated = mutableListOf<RecebimentoRfidEpcResponse>()
    private var listOfValueNotRelated = HashSet<RecebimentoRfidEpcResponse>()
    private var listOfValueFound = HashSet<RecebimentoRfidEpcResponse>()
    private var listOfValueMissing = mutableListOf<RecebimentoRfidEpcResponse>()
    private val STATUS_RELATED = "R"
    private val STATUS_FOUND = "E"
    private val STATUS_NOT_RELATED = "N"
    private val STATUS_MISSING = "F"
    private lateinit var progressConnection: ProgressDialog
    private var alertDialog: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRfidLeituraEpcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupShared()
        connectReader()
        setupViewModel()
        clickButtonConfig()
        setupAdapter()
        cliqueChips()
        clickButtonLimpar()
        clickButtonFinalizar()
        clickRfidAntenna()
        observer()
        getTagsEpcs()

    }


    private fun observer() {
        viewModel.apply {
            errorObserver()
            resultEpcsObserver()
            resultProgress()
        }
    }

    private fun RecebimentoRfidViewModel.resultProgress() {
        progress.observe(this@RfidLeituraEpcActivity) {
            binding.progressLoanding.isVisible = it
        }
    }

    private fun RecebimentoRfidViewModel.resultEpcsObserver() {
        sucessRetornaEpc.observe(this@RfidLeituraEpcActivity) { data ->
            listOfValueRelated = data.map { it.apply { status = "R" } }.toMutableList()
            setCountTagsChips(listOfValueRelated)
        }
    }


    private fun RecebimentoRfidViewModel.errorObserver() {
        errorDb.observe(this@RfidLeituraEpcActivity) { error ->
            alertDefaulSimplesError(message = error)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this, RecebimentoRfidViewModel.RecebimentoRfidViewModelFactory(
                RecebimentoRfidRepository()
            )
        )[RecebimentoRfidViewModel::class.java]
    }

    private fun getTagsEpcs() {
        try {
            if (intent != null) {
                listIdDoc =
                    intent.getSerializableExtra("LISTA_ID_NF") as ArrayList<ResponseGetRecebimentoNfsPendentes>
                viewModel.getTagsEpcs(token = token, idArmazem = idArmazem!!, listIdDoc = listIdDoc)
            }
        } catch (e: Exception) {
            alertDefaulError(
                message = "Erro ao receber informações", onClick = { finish() }, context = this
            )
        }
    }

    private fun setCountTagsChips(listTags: List<RecebimentoRfidEpcResponse>) {
        updateInputsCountChips()
        adapterLeituras.updateData(listTags)
    }

    private fun updateInputsCountChips() {
        val sizeRelational = listOfValueRelated.size
        val sizeEncontradas = listOfValueFound.size
        val sizeNaoRelacionadas = listOfValueNotRelated.size
        val sizeFaltando = sizeRelational - sizeEncontradas
        binding.chipRelacionados.text = "Relacionados - $sizeRelational"
        binding.chipEncontrados.text = "Encontrados - $sizeEncontradas"
        binding.chipNaoRelacionado.text = "Não relacionados - $sizeNaoRelacionadas"
        binding.chipFaltando.text = "Faltando - $sizeFaltando"
        binding.textQtdLeituras.text = "$sizeEncontradas / $sizeRelational"
        if (sizeRelational == sizeEncontradas && sizeNaoRelacionadas > 0) {
            showAlertDialog(sizeNaoRelacionadas)
        }
        binding.buttonFinalizar.isEnabled =
            sizeRelational == sizeEncontradas && sizeNaoRelacionadas == 0
        val porcentagemReanding = (sizeEncontradas * 100) / sizeRelational
        binding.progressPorcentReanding.progress = porcentagemReanding
        binding.textPorcentagemProgress.text = "Leituras: $porcentagemReanding%"
    }


    private fun showAlertDialog(sizeNaoRelacionadas: Int) {
        val message =
            "Identificamos que existem $sizeNaoRelacionadas etiquetas que não estão relacionadas às notas fiscais. Por favor, verifique a origem dessas etiquetas antes de prosseguir com o processo."
        alertDialog?.takeIf { it.isShowing }?.dismiss()

        alertDialog = AlertDialog.Builder(this)
            .setTitle("Aviso")
            .setMessage(message)
            .setPositiveButton("Entendi") { dialog, _ -> dialog.dismiss() }
            .create()

        alertDialog?.show()
    }

    private fun setupShared() {
        sharedPreferences = CustomSharedPreferences(this)
        sharedPreferences.apply {
            token = getString(CustomSharedPreferences.TOKEN) as String
            idArmazem = getInt(CustomSharedPreferences.ID_ARMAZEM)
            powerRfid = sharedPreferences.getInt(CustomSharedPreferences.POWER_RFID, 150)
            nivelAntenna =
                sharedPreferences.getInt(CustomSharedPreferences.NIVEL_ANTENNA_RFID, 3)
            Log.e(TAG, "powerRfidShared: $powerRfid - nivelAntenaShared: $nivelAntenna")
        }
    }


    private fun clickRfidAntenna() {
        binding.iconRfidSinal.setOnClickListener {
            seekBarPowerRfid(powerRfid, nivelAntenna) { newPower, nivel ->
                powerRfid = newPower
                nivelAntenna = nivel
                Log.e(TAG, "powerRfidClicado: $powerRfid - nivelAntenaClicado: $nivelAntenna")
                sharedPreferences.saveInt(CustomSharedPreferences.POWER_RFID, newPower)
                sharedPreferences.saveInt(CustomSharedPreferences.NIVEL_ANTENNA_RFID, nivel)
                rfidReader.configureRfidReader(
                    transmitPowerIndex = powerRfid,
                    rfModeTableIndex = nivelAntenna,
                    session = SESSION.SESSION_S0,
                    inventoryState = INVENTORY_STATE.INVENTORY_STATE_A,
                    slFlag = SL_FLAG.SL_ALL
                )
            }
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
                    withContext(Dispatchers.Main) {
                        progressConnection =
                            progressConected("Conectando a ${readerDevice.name}")
                        progressConnection.show()
                    }
                    rfidReader.connect()

                    withContext(Dispatchers.Main) {
                        if (rfidReader.isConnected) {
                            handleConnectionSuccess()
                        } else {
                            handleConnectionFailure("Não foi possível conectar ao leitor")
                        }
                        progressConnection.dismiss()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressConnection.dismiss()
                        handleConnectionFailure("Ocorreu um erro ao conectar:\n${e.localizedMessage}")
                    }
                }
            }
        }
    }


    private fun handleConnectionSuccess() {
        binding.progressRfid.isVisible = false
        binding.iconRfidSinal.isVisible = true
        binding.iconRfidSinal.setImageResource(R.drawable.icon_rfid_sucess_connect)
        configureReader()
        rfidReader.configureRfidReader(
            transmitPowerIndex = powerRfid,
            rfModeTableIndex = nivelAntenna,
            session = SESSION.SESSION_S0,
            inventoryState = INVENTORY_STATE.INVENTORY_STATE_AB_FLIP,
            slFlag = SL_FLAG.SL_ALL
        )
    }

    private fun handleConnectionFailure(message: String) {
        binding.progressRfid.isVisible = false
        binding.iconRfidSinal.isVisible = true
        toastDefault(message = message)
        binding.iconRfidSinal.setImageResource(R.drawable.icon_rfid_not_connect)
    }


    /**Configurações do listner */
    private fun configureReader() {
        rfidReader.configureReader(this)
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
            alertConfirmation(message = "Deseja Puxar de transito?",
                actionNo = {},
                actionYes = {
                    viewModel.trafficPull(
                        idArmazem = idArmazem!!,
                        token = token,
                        listIdDoc = listIdDoc
                    )
                }
            )
        }
    }

    private fun clickButtonLimpar() {
        binding.buttonClear.setOnClickListener {
            alertConfirmation(message = "Deseja limpar as leituras e iniciar novamente?",
                actionNo = {},
                actionYes = { clearReading() })
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
        adapterLeituras = LeituraRfidAdapter { tag ->
            epcSelected = tag.numeroSerie
            showAlertDialogOpcoesRfidEpcClick(tag) { opcao ->
                if (opcao == 0) {
                    val intent = Intent(this, DetalheCodigoEpcActivity::class.java)
                    intent.putExtra("EPC", tag.numeroSerie)
                    startActivity(intent)
                    extensionSendActivityanimation()
                } else {
                    //localizar
                    showProximityDialog()
                }
            }
        }
    }

    private fun showProximityDialog() {
        isShowModalTagLocalization = true
        Log.e(TAG, "EPC: $epcSelected")
        val epcMask = "FFFF000000000000000000"
        setupVolBeepRfid(quiet = true)
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        val binding = DialogTagProximityBinding.inflate(LayoutInflater.from(this))
        progressBar = binding.progressBarProximity
        textRssiValue = binding.textRssiValue
        builder.setView(binding.root)
            .setTitle("Localizar a tag:\n${epcSelected ?: "-"}")
            .setNegativeButton("Fechar") { dialog, _ ->
                dialog.dismiss()
                setupVolBeepRfid(quiet = false)
                epcSelected = null
                isShowModalTagLocalization = false
            }
        proximityDialog = builder.create()
        proximityDialog.show()
    }

    //Define o volume do bipe quando abre modal de localizar a tag seleciona
    private fun setupVolBeepRfid(quiet: Boolean) {
        if (quiet) {
            rfidReader.Config.beeperVolume = BEEPER_VOLUME.QUIET_BEEP
        } else {
            rfidReader.Config.beeperVolume = BEEPER_VOLUME.MEDIUM_BEEP
        }
    }

    // Função para atualizar o progresso e o valor de RSSI
    private fun updateProximity(rssi: Int) {
        try {
            if (progressBar != null) {
                val proximityPercentage = calculateProximityPercentage(rssi)
                val currentProgress = progressBar!!.progress
                val animation = ObjectAnimator.ofInt(
                    progressBar, null, currentProgress, proximityPercentage
                )
                animation.duration = 100 // Duração da animação
                animation.interpolator = DecelerateInterpolator()
                animation.addUpdateListener { animator ->
                    val animatedValue = animator.animatedValue as Int
                    textRssiValue.text = "Proximidade: $animatedValue%"
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
        return ((adjustedRssi + 90) * (100f / 60f)).toInt()
    }


    private fun cliqueChips() {
        binding.chipRelacionados.isChecked = true
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chipId = checkedIds[0]
                val chip = group.findViewById<Chip>(chipId)
                when (chip.id) {
                    R.id.chip_relacionados -> {
                        updateFilter(listOfValueRelated.map {
                            it.apply {
                                status = STATUS_RELATED
                            }
                        }
                            .toMutableList())
                    }

                    R.id.chip_encontrados -> {
                        updateFilter(listOfValueFound.map { it.apply { status = STATUS_FOUND } }
                            .toMutableList())
                    }

                    R.id.chip_nao_relacionado -> {
                        updateFilter(listOfValueNotRelated.map {
                            it.apply {
                                status = STATUS_NOT_RELATED
                            }
                        }.toMutableList())
                    }

                    R.id.chip_faltando -> {
                        val difference = listOfValueRelated.filterNot { it in listOfValueFound }
                        updateFilter(difference.map { it.apply { status = STATUS_MISSING } }
                            .toMutableList())
                    }
                }
            } else {
                Toast.makeText(this, "Nenhum Chip selecionado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFilter(listFilter: MutableList<RecebimentoRfidEpcResponse>) {
        adapterLeituras.updateData(listFilter)
    }


    override fun eventReadNotify(data: RfidReadEvents?) {
        // Evitar processamento se a tag for nula
        val tag = data?.readEventData?.tagData ?: return
        rfidReader.Actions.getReadTags(100) ?: return

        // Se não estiver exibindo o modal de localização de tag
        if (!isShowModalTagLocalization) {
            tagReaders++

            // Iniciar a corrotina para processamento em IO
            CoroutineScope(Dispatchers.IO).launch {
                val isNewTag = uniqueTagIds.add(tag.tagID)

                // Separar lógica de atualização da lista
                if (isNewTag) {
                    updateTagLists(tag.tagID)

                    withContext(Dispatchers.Main) {
                        updateInputsCountChips()
                        updateChipCurrent()
                    }
                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                epcSelected?.let { selectedEpc ->
                    if (tag.tagID == selectedEpc) {
                        withContext(Dispatchers.Main) {
                            somBeepRfidPool() // Dispara o beep
                            updateProximity(tag.peakRSSI.toInt()) // Atualizar proximidade
                            Log.d(TAG, "igual: ${tag.peakRSSI}")
                        }
                    }
                }
            }
        }
    }


    // Função para atualizar as listas de tags
    private fun updateTagLists(tagID: String) {
        val tagFound = listOfValueRelated.firstOrNull { it.numeroSerie == tagID }
        if (tagFound != null) {
            listOfValueFound.add(tagFound.apply { status = STATUS_FOUND })
        } else {
            listOfValueNotRelated.add(
                RecebimentoRfidEpcResponse(numeroSerie = tagID, status = STATUS_NOT_RELATED)
            )
        }
    }

    private fun updateChipCurrent() {
        when {
            binding.chipRelacionados.isChecked -> {
                updateFilter(listOfValueRelated.map { it.apply { status = STATUS_RELATED } }
                    .toMutableList())
            }

            binding.chipNaoRelacionado.isChecked -> {
                updateFilter(listOfValueNotRelated.map {
                    it.apply {
                        status = STATUS_NOT_RELATED
                    }
                }
                    .toMutableList())
            }

            binding.chipEncontrados.isChecked -> {
                updateFilter(listOfValueFound.map { it.apply { status = STATUS_FOUND } }
                    .toMutableList())
            }

            binding.chipFaltando.isChecked -> {
                val difference = listOfValueRelated.filterNot { it in listOfValueFound }
                updateFilter(difference.map { it.apply { status = STATUS_MISSING } }
                    .toMutableList())
            }
        }
    }


    override fun eventStatusNotify(rfidStatusEvents: RfidStatusEvents?) {
        if (rfidStatusEvents != null) {
            val eventType = rfidStatusEvents.StatusEventData?.statusEventType
            val eventData = rfidStatusEvents.StatusEventData
            when (eventType) {
                STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT -> {
                    val triggerEvent = eventData?.HandheldTriggerEventData?.handheldEvent
                    Log.i(TAG, "Evento de gatilho recebido: $triggerEvent")

                    when (triggerEvent) {
                        HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED -> {
                            startReading()  // Gatilho pressionado, iniciar leitura
                            Log.i(TAG, "Iniciando leitura (Trigger pressionado)")
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

    private fun clearReading() {
        binding.chipRelacionados.isChecked = true
        scrollToSelectedChip(binding.chipRelacionados)
        listOfValueRelated.clear()
        listOfValueFound.clear()
        listOfValueNotRelated.clear()
        uniqueTagIds.clear()
        listOfValueMissing.clear()
        getTagsEpcs()
    }

    // Função para fazer scroll até o Chip selecionado
    private fun scrollToSelectedChip(selectedChip: Chip) {
        try {
            binding.scrollChip.post {
                binding.scrollChip.smoothScrollTo(
                    selectedChip.left, selectedChip.top
                )
            }
        } catch (e: Exception) {
            toastDefault(message = "Ocorreu um erro ao fazer scroll")
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
        releaseSoundPool()
    }
}
