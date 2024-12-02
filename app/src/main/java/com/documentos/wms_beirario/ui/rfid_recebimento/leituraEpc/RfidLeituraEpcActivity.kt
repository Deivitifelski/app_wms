package com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import co.kr.bluebird.sled.BTReader
import co.kr.bluebird.sled.SDConsts
import co.kr.bluebird.sled.SelectionCriterias
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityRfidLeituraEpcBinding
import com.documentos.wms_beirario.databinding.DialogTagProximityBinding
import com.documentos.wms_beirario.model.recebimentoRfid.RecebimentoRfidEpcResponse
import com.documentos.wms_beirario.model.recebimentoRfid.ResponseGetRecebimentoNfsPendentes
import com.documentos.wms_beirario.repository.recebimentoRfid.RecebimentoRfidRepository
import com.documentos.wms_beirario.ui.rfid_recebimento.RFIDReaderManager
import com.documentos.wms_beirario.ui.rfid_recebimento.RFIDReaderManager.Companion.DEVICE_BLUETOOTH_ZEBRA
import com.documentos.wms_beirario.ui.rfid_recebimento.RFIDReaderManager.Companion.GATILHO_CLICADO
import com.documentos.wms_beirario.ui.rfid_recebimento.bluetoohRfid.BluetoohRfidActivity
import com.documentos.wms_beirario.ui.rfid_recebimento.detalhesEpc.DetalheCodigoEpcActivity
import com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.adapter.LeituraRfidAdapter
import com.documentos.wms_beirario.ui.rfid_recebimento.viewModel.RecebimentoRfidViewModel
import com.documentos.wms_beirario.utils.extensions.alertBatterRfid
import com.documentos.wms_beirario.utils.extensions.alertConfirmation
import com.documentos.wms_beirario.utils.extensions.alertDefaulError
import com.documentos.wms_beirario.utils.extensions.alertDefaulSimplesError
import com.documentos.wms_beirario.utils.extensions.alertInfoTimeDefaultAndroid
import com.documentos.wms_beirario.utils.extensions.alertMessageSucessAction
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation
import com.documentos.wms_beirario.utils.extensions.mapPowerBlueBird
import com.documentos.wms_beirario.utils.extensions.mapPowerZebra
import com.documentos.wms_beirario.utils.extensions.progressConected
import com.documentos.wms_beirario.utils.extensions.seekBarPowerRfid
import com.documentos.wms_beirario.utils.extensions.showAlertDialogOpcoesRfidEpcClick
import com.documentos.wms_beirario.utils.extensions.showConnectionOptionsDialog
import com.documentos.wms_beirario.utils.extensions.somBeepRfidPool
import com.documentos.wms_beirario.utils.extensions.somError
import com.documentos.wms_beirario.utils.extensions.somLoandingConnected
import com.documentos.wms_beirario.utils.extensions.somSucess
import com.documentos.wms_beirario.utils.extensions.somWarning
import com.documentos.wms_beirario.utils.extensions.statusbatteryBlueBird
import com.documentos.wms_beirario.utils.extensions.toastDefault
import com.documentos.wms_beirario.utils.extensions.toastError
import com.google.android.material.chip.Chip
import com.zebra.rfid.api3.INVENTORY_STATE
import com.zebra.rfid.api3.SESSION
import com.zebra.rfid.api3.SL_FLAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RfidLeituraEpcActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRfidLeituraEpcBinding
    private lateinit var adapterLeituras: LeituraRfidAdapter
    private lateinit var viewModel: RecebimentoRfidViewModel
    private val TAG = "RFID"
    private lateinit var listIdDoc: ArrayList<ResponseGetRecebimentoNfsPendentes>
    private var powerRfid: Int = 150
    private lateinit var token: String
    private var idArmazem: Int? = null
    var isModeSetupVisible: Boolean = false
    private var nivelAntenna: Int = 3
    private var proximityPercentage: Int = 0
    private lateinit var sharedPreferences: CustomSharedPreferences
    private var progressBar: ProgressBar? = null
    private var textRssiValue: TextView? = null
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
    private var nivelBateria: Int? = null
    private var alertDialog: AlertDialog? = null
    private lateinit var rfidReaderManager: RFIDReaderManager
    private lateinit var readerRfidBlueBirdBt: BTReader
    private var isBattery15 = false
    private var isBattery05 = false
    private lateinit var progressConnection: Dialog

    private val handlerEpc = Handler(Looper.getMainLooper()) { res ->
        handleInventoryHandler(res)
        true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRfidLeituraEpcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rfidReaderManager = RFIDReaderManager.getInstance()
        setupShared()
        setupViewModel()
        clickButtonConfig()
        setupAdapter()
        cliqueChips()
        clickButtonLimpar()
        clickButtonFinalizar()
        clickRfidAntenna()
        observer()
        getTagsEpcs()
        setupToolbar()

    }


    override fun onResume() {
        super.onResume()
        isConnectedBluetooh()
    }

    private fun isConnectedBluetooh() {
        nivelBateria = null
        binding.iconBatteryRfid.visibility = View.GONE
        binding.txtPorcentageBattery.visibility = View.GONE
        readerRfidBlueBirdBt = BTReader.getReader(this, handlerEpc)
        readerRfidBlueBirdBt.SD_Open()
        if (readerRfidBlueBirdBt.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED) {
            somSucess()
            iconConnectedSucess(connected = true)
            if (readerRfidBlueBirdBt.BT_GetConnectedDeviceName().contains("RFD")) {
                setupAntennaRfid()
                setupRfid()
            } else {
                binding.iconBatteryRfid.visibility = View.VISIBLE
                binding.txtPorcentageBattery.visibility = View.VISIBLE
            }
        } else {
            connectRfidManager()
        }
    }

    private fun connectRfidManager() {
        rfidReaderManager.verifyConnectRfid { isConneted ->
            if (isConneted) {
                somLoandingConnected()
                iconConnectedSucess(connected = true)
                setupRfid()
            } else {
                showConnectionOptionsDialog(onCancel = {
                    alertDefaulSimplesError(message = "É necessário conectar o leitor RFID para realizar as leituras.")
                    iconConnectedSucess(connected = false)
                }, onResult = { result ->
                    if (result == "Bluetooth") {
                        startActivity(Intent(this, BluetoohRfidActivity::class.java))
                        extensionSendActivityanimation()
                    } else {
                        acessDirectZebra()
                    }
                })
            }
        }
    }


    private fun setupRfid() {
        rfidReaderManager.configureReaderRfid(
            onResultTag = { tag ->
                if (!isShowModalTagLocalization) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val isNewTag = uniqueTagIds.add(tag.tagID)
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
                                    somBeepRfidPool()
                                    updateProximityZebra(tag.peakRSSI.toInt())
                                    Log.d(TAG, "igual: ${tag.peakRSSI}")
                                }
                            }
                        }
                    }
                }
            },

            onResultEvent = { event ->
                Log.e(TAG, "EVENTO RECEBIDO ACTIVITY: $event")
            },

            onResultEventClickTrigger = { click ->
                if (!GATILHO_CLICADO) {
                    updateProximityZebra(-90)
                }
            }
        )
    }

    private fun updateProximityZebra(rssi: Int) {
        try {
            if (progressBar != null) {
                viewModel.calculateProximityPercentage(rssi)
                val currentProgress = progressBar!!.progress
                val animation = ObjectAnimator.ofInt(
                    progressBar, "progress", currentProgress, proximityPercentage
                )
                animation.duration = 100
                animation.interpolator = DecelerateInterpolator()
                animation.addUpdateListener { animator ->
                    val animatedValue = animator.animatedValue as Int
                    textRssiValue?.text = "Proximidade: $animatedValue%"
                }
                animation.start()
            }
        } catch (e: Exception) {
            toastDefault(message = "Ocorreu um erro ao trazer a localizacao da tag")
        }
    }


    private fun setupToolbar() {
        binding.iconVoltar.setOnClickListener {
            finish()
            extensionBackActivityanimation()
        }
    }


    private fun setupShared() {
        sharedPreferences = CustomSharedPreferences(this)
        sharedPreferences.apply {
            token = getString(CustomSharedPreferences.TOKEN) as String
            idArmazem = getInt(CustomSharedPreferences.ID_ARMAZEM)
            powerRfid = sharedPreferences.getInt(CustomSharedPreferences.POWER_RFID, 100)
            nivelAntenna = sharedPreferences.getInt(CustomSharedPreferences.NIVEL_ANTENNA_RFID, 3)
            Log.e(TAG, "powerRfidShared: $powerRfid - nivelAntenaShared: $nivelAntenna")
        }
    }

    private fun setupAntennaRfid(changed: Boolean? = false) {

        rfidReaderManager.configureRfidReader(
            transmitPowerIndex = mapPowerZebra(powerRfid),
            rfModeTableIndex = nivelAntenna,
            session = SESSION.SESSION_S0,
            inventoryState = INVENTORY_STATE.INVENTORY_STATE_A,
            slFlag = SL_FLAG.SL_ALL,
            onResult = { res ->
                toastDefault(message = res)
                Log.e(TAG, "onResult: $res")
            },
            onError = { error ->
                toastError(message = error)
                somError()
            },
            changed = changed
        )
    }


    private fun iconConnectedSucess(connected: Boolean) {
        binding.progressRfid.visibility = View.GONE
        binding.iconRfidSinal.visibility = View.VISIBLE
        if (connected) {
            binding.iconRfidSinal.setImageResource(R.drawable.icon_rfid_sucess_connect)
        } else {
            binding.iconRfidSinal.setImageResource(R.drawable.icon_rfid_not_connect)
        }
    }


    private fun observer() {
        viewModel.apply {
            errorObserver()
            resultEpcsObserver()
            resultProgress()
            resultTrafficPull()
            resultPorcentage()
        }
    }

    private fun RecebimentoRfidViewModel.resultPorcentage() {
        sucessReturnPercentage.observe(this@RfidLeituraEpcActivity) {
            proximityPercentage = it
        }
    }

    private fun RecebimentoRfidViewModel.resultTrafficPull() {
        sucessPullTraffic.observe(this@RfidLeituraEpcActivity) {
            isFinishClickButton(isClick = true)
            alertMessageSucessAction(message = "Notas Fiscais conferidas e puxadas de transito!",
                action = {
                    finish()
                    extensionSendActivityanimation()
                })
        }
    }

    private fun RecebimentoRfidViewModel.resultProgress() {
        progress.observe(this@RfidLeituraEpcActivity) {
            binding.progressLoanding.isVisible = it
        }
    }

    private fun isFinishClickButton(isClick: Boolean) {
        binding.buttonClear.isEnabled = isClick
        binding.buttonFinalizar.isEnabled = isClick
        binding.iconVoltar.isEnabled = isClick
    }

    private fun RecebimentoRfidViewModel.resultEpcsObserver() {
        sucessRetornaEpc.observe(this@RfidLeituraEpcActivity) { data ->
            listOfValueRelated = data.map { it.apply { status = "R" } }.toMutableList()
            setCountTagsChips(listOfValueRelated)
        }
    }


    private fun RecebimentoRfidViewModel.errorObserver() {
        errorDb.observe(this@RfidLeituraEpcActivity) { error ->
            isFinishClickButton(isClick = true)
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
                viewModel.getTagsEpcs(
                    token = token, idArmazem = idArmazem!!, listIdDoc = listIdDoc
                )
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
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun showAlertDialog(sizeNaoRelacionadas: Int) {
        val message =
            "Identificamos que existem $sizeNaoRelacionadas etiquetas que não estão relacionadas às notas fiscais. Por favor, verifique a origem dessas etiquetas antes de prosseguir com o processo."
        alertDialog?.takeIf { it.isShowing }?.dismiss()

        alertDialog = AlertDialog.Builder(this).setTitle("Aviso").setMessage(message)
            .setPositiveButton("Entendi") { dialog, _ -> dialog.dismiss() }.create()

        alertDialog?.show()
    }


    private fun clickRfidAntenna() {
        binding.iconRfidSinal.setOnClickListener {
            isModeSetupVisible = true
            binding.iconRfidSinal.isEnabled = false
            seekBarPowerRfid(powerRfid, nivelAntenna) { power, nivel ->
                applyAntennaConfigurations(power, nivel)
            }
        }
    }

    private fun applyAntennaConfigurations(power: Int, nivel: Int) {
        powerRfid = power
        nivelAntenna = nivel
        sharedPreferences.saveInt(CustomSharedPreferences.POWER_RFID, power)
        sharedPreferences.saveInt(CustomSharedPreferences.NIVEL_ANTENNA_RFID, nivel)

        try {
            if (readerRfidBlueBirdBt.BT_GetConnectState() != SDConsts.BTConnectState.CONNECTED) {
                setupAntennaRfid(changed = true)
                finalizeConfiguration()
            } else {
                configureConnectedAntenna(power, nivel)
            }
        } catch (e: Exception) {
            isModeSetupVisible = false
            binding.iconRfidSinal.isEnabled = true
            Log.e("-->", "Erro ao alterar nível da antena: ${e.message}")
        }
    }

    private fun configureConnectedAntenna(power: Int, nivel: Int) {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            try {
                readerRfidBlueBirdBt.RF_SetRadioPowerState(mapPowerBlueBird(power))
                readerRfidBlueBirdBt.RF_SetRFMode(nivel)

                withContext(Dispatchers.Main) {
                    toastDefault(message = "Configurações aplicadas (BlueBird)!")
                    finalizeConfiguration()
                }
            } catch (e: Exception) {
                isModeSetupVisible = false
                binding.iconRfidSinal.isEnabled = true
                Log.e("-->", "Erro durante configuração da antena conectada: ${e.message}")
            }
        }
    }

    private fun finalizeConfiguration() {
        isModeSetupVisible = false
        binding.iconRfidSinal.isEnabled = true // Reativa o clique no botão
    }


    private fun clickButtonConfig() {
        binding.iconConfig.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.menu_rfid_recebimento, popup.menu)

            // Definir o comportamento ao clicar em cada item
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_option_1 -> {
                        startActivity(Intent(this, BluetoohRfidActivity::class.java))
                        extensionSendActivityanimation()
                        true
                    }

                    R.id.menu_option_2 -> {
                        acessDirectZebra()
                        true
                    }

                    R.id.menu_option_3 -> {
                        if (nivelBateria != null) {
                            alertBatterRfid(nivel = nivelBateria!!)
                        } else {
                            toastDefault(message = "Visivel somente em dispositivos (BlueBird)")
                        }
                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }
    }

    private fun acessDirectZebra() {
        progressConnection = progressConected(msg = "Conectando...")
        progressConnection.show()
        rfidReaderManager.connectUsbRfid(
            context = this,
            onResult = { res ->
                readerRfidBlueBirdBt.BT_Disconnect()
                binding.iconBatteryRfid.visibility = View.INVISIBLE
                binding.txtPorcentageBattery.visibility = View.INVISIBLE
                toastDefault(message = res)
                somLoandingConnected()
                iconConnectedSucess(connected = true)
                progressConnection.dismiss()
                setupAntennaRfid()
                setupRfid()
            }, onError = { error ->
                iconConnectedSucess(connected = false)
                somError()
                alertDefaulSimplesError(message = error)
                progressConnection.dismiss()
            })
    }


    private fun clickButtonFinalizar() {
        binding.buttonFinalizar.setOnClickListener {
            alertConfirmation(message = "Deseja Puxar de transito?",
                icon = R.drawable.icon_dowload,
                actionNo = {},
                actionYes = {
                    isFinishClickButton(isClick = false)
                    viewModel.trafficPull(
                        idArmazem = idArmazem!!, token = token, listIdDoc = listIdDoc
                    )
                })
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
            showAlertDialogOpcoesRfidEpcClick(tag) { opcao ->
                if (opcao == 0) {
                    val intent = Intent(this, DetalheCodigoEpcActivity::class.java)
                    intent.putExtra("EPC", tag.numeroSerie)
                    startActivity(intent)
                    extensionSendActivityanimation()
                } else {
                    showProximityDialog(tag.numeroSerie)
                }
            }
        }
    }

    private fun showProximityDialog(numeroSerie: String) {
        epcSelected = numeroSerie
        isShowModalTagLocalization = true
        Log.e(TAG, "EPC: $epcSelected")
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        val binding = DialogTagProximityBinding.inflate(LayoutInflater.from(this))
        progressBar = binding.progressBarProximity
        textRssiValue = binding.textRssiValue
        builder.setView(binding.root).setTitle("Localizar a tag:\n${epcSelected ?: "-"}")
            .setNegativeButton("Fechar") { dialog, _ ->
                dialog.dismiss()
                epcSelected = null
                isShowModalTagLocalization = false
            }
        proximityDialog = builder.create()
        proximityDialog.show()
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
                        }.toMutableList())
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
                        updateListRelated()
                    }
                }
            } else {
                Toast.makeText(this, "Nenhum Chip selecionado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateListRelated() {
        val setOfFoundValues = listOfValueFound.toSet()
        val missingItems = listOfValueRelated.asSequence().filter { it !in setOfFoundValues }
            .onEach { it.status = STATUS_MISSING }.toMutableList()
        updateFilter(missingItems)
    }

    private fun updateFilter(listFilter: MutableList<RecebimentoRfidEpcResponse>) {
        adapterLeituras.updateData(listFilter)
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
                }.toMutableList())
            }

            binding.chipEncontrados.isChecked -> {
                updateFilter(listOfValueFound.map { it.apply { status = STATUS_FOUND } }
                    .toMutableList())
            }

            binding.chipFaltando.isChecked -> {
                updateListRelated()
            }
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


    private fun handleInventoryHandler(message: Message) {
        Log.d(TAG, "mInventoryHandler")
        Log.d(TAG, "m arg1 = ${message.arg1} arg2 = ${message.arg2}")

        when (message.what) {
            SDConsts.Msg.SDMsg -> {
                when (message.arg1) {
                    SDConsts.SDCmdMsg.SLED_HOTSWAP_STATE_CHANGED -> {
                        val hotswapMessage =
                            if (message.arg2 == SDConsts.SDHotswapState.HOTSWAP_STATE) {
                                "HOTSWAP STATE CHANGED = HOTSWAP_STATE"
                            } else {
                                "HOTSWAP STATE CHANGED = NORMAL_STATE"
                            }
                        toastDefault(message = hotswapMessage)
                    }

                    SDConsts.SDCmdMsg.TRIGGER_PRESSED -> {
                        if (!isModeSetupVisible) {
                            if (epcSelected != null && isShowModalTagLocalization) {
                                setupRfidBlueBirdLocalization()
                            } else {
                                setupRfidBlueBird()
                            }
                        }
                    }

                    SDConsts.SDCmdMsg.SLED_INVENTORY_STATE_CHANGED -> {
                        Toast.makeText(
                            this,
                            "Inventory Stopped reason : ${message.arg2}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    SDConsts.SDCmdMsg.TRIGGER_RELEASED -> {
                        setProgressRssi(0)
                        if (readerRfidBlueBirdBt.RF_StopInventory() == SDConsts.SDResult.SUCCESS) {
                            Log.e(TAG, "Gatilho liberado!")
                        }
                    }

                    SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED -> {
                        statusbatteryBlueBird(message.arg2, binding.iconBatteryRfid)
                        binding.txtPorcentageBattery.visibility = View.VISIBLE
                        binding.txtPorcentageBattery.text = "${message.arg2}%"
                        nivelBateria = message.arg2
                        when {
                            message.arg2 < 5 -> {
                                if (!isBattery05) {
                                    isBattery05 = true
                                    alertInfoTimeDefaultAndroid(
                                        title = "Bateria Crítica",
                                        message = "A bateria do leitor está em nível crítico (${message.arg2}%). Por favor, conecte o dispositivo ao carregador imediatamente.",
                                        time = 10000
                                    )
                                }
                            }

                            message.arg2 < 15 -> {
                                if (!isBattery15) {
                                    isBattery15 = true
                                    alertInfoTimeDefaultAndroid(
                                        title = "Bateria Baixa",
                                        message = "A bateria do leitor está muito baixa (${message.arg2}%). Por favor, conecte o dispositivo ao carregador.",
                                        time = 10000
                                    )
                                }
                            }
                        }
                        Log.d(TAG, "Battery state = ${message.arg2}")
                    }
                }
            }

            SDConsts.Msg.RFMsg -> {
                when (message.arg1) {
                    SDConsts.RFCmdMsg.INVENTORY_CUSTOM_READ -> {
                        if (message.arg2 == SDConsts.RFResult.SUCCESS) {
                            Log.e("INVENTORY_CUSTOM_READ", "INVENTORY_CUSTOM_READ!")
                        }
                    }

                    SDConsts.RFCmdMsg.INVENTORY, SDConsts.RFCmdMsg.READ -> {
                        if (message.arg2 == SDConsts.RFResult.SUCCESS) {
                            (message.obj as? String)?.let { processReadData(it) }
                        }
                    }

                    SDConsts.RFCmdMsg.LOCATE -> {
                        if (message.arg2 == SDConsts.RFResult.SUCCESS) {
                            Log.e("INVENTORY", "INVENTORY LOCATE!")
                            if (message.obj != null && message.obj is Int)
                                (message.obj as? Int)?.let { processRssi(it) }
                        }
                    }
                }
            }

            SDConsts.Msg.BTMsg -> {
                when (message.arg1) {
                    SDConsts.BTCmdMsg.SLED_BT_CONNECTION_STATE_CHANGED -> {
                        Log.d(TAG, "SLED_BT_CONNECTION_STATE_CHANGED = ${message.arg2}")
                        if (readerRfidBlueBirdBt.BT_GetConnectState() != SDConsts.BTConnectState.CONNECTED) {
                            toastDefault(message = "Dispositivo desconectado")
                        }
                    }

                    SDConsts.BTCmdMsg.SLED_BT_DISCONNECTED, SDConsts.BTCmdMsg.SLED_BT_CONNECTION_LOST -> {

                    }
                }
            }
        }
    }

    private fun setupRfidBlueBirdLocalization() {
        GlobalScope.launch(Dispatchers.IO) {
            readerRfidBlueBirdBt.apply {
                RF_SetRFMode(1)
                RF_SetSession(SDConsts.RFSession.SESSION_S0);
                RF_SetToggle(SDConsts.RFToggle.ON)
                RF_SetSingulationControl(
                    5,
                    SDConsts.RFSingulation.MIN_SINGULATION,
                    SDConsts.RFSingulation.MAX_SINGULATION
                )
            }
            val s = SelectionCriterias()
            s.makeCriteria(
                SelectionCriterias.SCMemType.EPC, epcSelected!!,
                0, epcSelected!!.length * 4,
                SelectionCriterias.SCActionType.ASLINVA_DSLINVB.toInt()
            )
            readerRfidBlueBirdBt.RF_SetSelection(s)
            readerRfidBlueBirdBt.RF_PerformInventoryForLocating(epcSelected)
        }
    }

    private fun setupRfidBlueBird() {
        GlobalScope.launch(Dispatchers.IO) {
            readerRfidBlueBirdBt.apply {
                RF_SetRFMode(1)
                RF_SetSession(SDConsts.RFSession.SESSION_S1)
                RF_SetToggle(SDConsts.RFToggle.OFF)
                RF_SetSingulationControl(
                    10,
                    SDConsts.RFSingulation.MIN_SINGULATION,
                    SDConsts.RFSingulation.MAX_SINGULATION
                )
            }
            val ret = readerRfidBlueBirdBt.RF_PerformInventory(
                true,
                false,
                false,
                true
            )
        }
    }

    private fun processRssi(tag: Int) {
        setProgressRssi(tag)
    }

    private fun setProgressRssi(tag: Int) {
        textRssiValue?.text = "Proximidade: ${tag}%"
        progressBar?.setProgress(tag, true)
        somBeepRfidPool()
    }


    private fun processReadData(input: String) {
        Log.e(TAG, "TAG COMPLETA: $input")
        val semicolonIndex = input.indexOf(';')
        if (semicolonIndex == -1) return
        val tag = if (semicolonIndex > 4) input.substring(4, semicolonIndex) else ""
        val rssiIndex = input.indexOf("rssi:", semicolonIndex)
        if (rssiIndex == -1) return
        val rssiValor = input.substring(rssiIndex + 5).takeWhile { it != ';' }
        Log.e(TAG, "Resultado RFID: $tag - $rssiValor")
        if (!isShowModalTagLocalization) {
            CoroutineScope(Dispatchers.IO).launch {
                val isNewTag = uniqueTagIds.add(tag)
                if (isNewTag) {
                    updateTagLists(tag)
                    withContext(Dispatchers.Main) {
                        somBeepRfidPool()
                        updateInputsCountChips()
                        updateChipCurrent()
                    }
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        extensionBackActivityanimation()
    }

}
