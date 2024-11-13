package com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc

import android.Manifest
import android.animation.ObjectAnimator
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import co.kr.bluebird.sled.BTReader
import co.kr.bluebird.sled.IRfidInventory
import co.kr.bluebird.sled.SDConsts
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityRfidLeituraEpcBinding
import com.documentos.wms_beirario.databinding.DialogTagProximityBinding
import com.documentos.wms_beirario.model.recebimentoRfid.RecebimentoRfidEpcResponse
import com.documentos.wms_beirario.model.recebimentoRfid.ResponseGetRecebimentoNfsPendentes
import com.documentos.wms_beirario.repository.recebimentoRfid.RecebimentoRfidRepository
import com.documentos.wms_beirario.ui.rfid_recebimento.RFIDReaderManager
import com.documentos.wms_beirario.ui.rfid_recebimento.detalhesEpc.DetalheCodigoEpcActivity
import com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.adapter.LeituraRfidAdapter
import com.documentos.wms_beirario.ui.rfid_recebimento.viewModel.RecebimentoRfidViewModel
import com.documentos.wms_beirario.utils.extensions.alertConfirmation
import com.documentos.wms_beirario.utils.extensions.alertDefaulError
import com.documentos.wms_beirario.utils.extensions.alertDefaulSimplesError
import com.documentos.wms_beirario.utils.extensions.alertMessageSucessAction
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation
import com.documentos.wms_beirario.utils.extensions.progressConected
import com.documentos.wms_beirario.utils.extensions.seekBarPowerRfid
import com.documentos.wms_beirario.utils.extensions.showAlertDialogOpcoesRfidEpcClick
import com.documentos.wms_beirario.utils.extensions.showConnectionOptionsDialog
import com.documentos.wms_beirario.utils.extensions.somBeepRfidPool
import com.documentos.wms_beirario.utils.extensions.somError
import com.documentos.wms_beirario.utils.extensions.somLoandingConnected
import com.documentos.wms_beirario.utils.extensions.toastDefault
import com.google.android.material.chip.Chip
import com.zebra.rfid.api3.INVENTORY_STATE
import com.zebra.rfid.api3.SESSION
import com.zebra.rfid.api3.SL_FLAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RfidLeituraEpcActivity : AppCompatActivity(), IRfidInventory {

    private lateinit var binding: ActivityRfidLeituraEpcBinding
    private lateinit var adapterLeituras: LeituraRfidAdapter
    private lateinit var viewModel: RecebimentoRfidViewModel
    private val TAG = "RFID"
    private lateinit var listIdDoc: ArrayList<ResponseGetRecebimentoNfsPendentes>
    private var powerRfid: Int = 150
    private lateinit var token: String
    private var idArmazem: Int? = null
    private var nivelAntenna: Int = 3
    private var proximityPercentage: Int = 0
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
    private val discoveredDevices = mutableListOf<BluetoothDevice>()
    private val deviceNames = mutableListOf<String>()
    private lateinit var deviceListAdapter: ArrayAdapter<String>
    private val scanDuration = 10000L // Tempo limite de escaneamento (10 segundos)
    private val handler = Handler()
    private lateinit var rfidReaderManager: RFIDReaderManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var rfidBlueBird: BTReader

    // Criação do contrato de solicitação de permissão
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startBluetoothDiscovery()
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRfidLeituraEpcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rfidBlueBird = BTReader.getReader(this, Handler())
        rfidBlueBird.SD_Open()
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
        connectRfidManager()


    }

    private fun verifyBluetoohSupported() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth não suportado", Toast.LENGTH_LONG).show()
            return
        } else {
            verifyBluettohOpen()
        }
    }

    private fun verifyBluettohOpen() {
        // Verifica se o Bluetooth está ativado
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        }

        // Solicita permissão de localização se necessário
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            showModalBluetooh()
            startBluetoothDiscovery()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun startBluetoothDiscovery() {
        // Registra o receiver para ouvir os dispositivos encontrados
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(broadcastReceiver, filter)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            bluetoothAdapter.startDiscovery()
        }
    }

    // Recebedor de eventos de dispositivos Bluetooth encontrados
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Obtém o dispositivo encontrado
                val device: BluetoothDevice =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                val deviceName = device.name
                val deviceAddress = device.address
                Log.e("->", "Dispositivo encontrado: $deviceName, $deviceAddress")
                if (!discoveredDevices.contains(device)) {
                    discoveredDevices.add(device)
                    deviceNames.add("${device.name ?: "Desconhecido"} - ${device.address}")
                    deviceListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun showModalBluetooh() {
        if (bluetoothAdapter != null) {
            discoveredDevices.clear()
            deviceNames.clear()
            deviceListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceNames)
            val dialog = AlertDialog.Builder(this).setTitle("Dispositivos Bluetooth")
                .setIcon(R.drawable.icon_bluetooh_setting)
                .setView(progressBar).setAdapter(deviceListAdapter) { _, position ->
                    val device = discoveredDevices[position]
                    connectBluetooh(device)
                }

                .setPositiveButton("Atualizar", null)
                .setNegativeButton("Cancelar") { _, _ ->
                    if (isDeviceConnected()) {
                        iconConnectedSucess(connected = true)
                    } else {
                        iconConnectedSucess(connected = false)
                    }
                }.create()

            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                discoveredDevices.clear()
                deviceNames.clear()
                deviceListAdapter.notifyDataSetChanged()
//                startDiscovery()
                toastDefault(message = "Buscando dispositivos Bluetooth...")
            }
            initScanBluetooh(bluetoothAdapter)
        } else {
            somError()
            toastDefault(message = "Bluetooth não disponível")
        }
    }

    private fun connectBluetooh(device: BluetoothDevice) {
        val mac = device.address
        Log.i(TAG, "[BT_Connect] :: mac = ${device.address}")
        if (rfidBlueBird.BT_GetConnectState() != SDConsts.BTConnectState.CONNECTED) {
            rfidBlueBird.BT_Connect(mac)
        } else {
            toastDefault(message = "Já conectado ${rfidBlueBird.BT_GetConnectedDeviceName() ?: ""}")
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
            powerRfid = sharedPreferences.getInt(CustomSharedPreferences.POWER_RFID, 150)
            nivelAntenna =
                sharedPreferences.getInt(CustomSharedPreferences.NIVEL_ANTENNA_RFID, 3)
            Log.e(TAG, "powerRfidShared: $powerRfid - nivelAntenaShared: $nivelAntenna")
        }
    }

    private fun connectRfidManager() {
        rfidReaderManager = RFIDReaderManager.getInstance()
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
                        verifyBluetoohSupported()
                    } else {
                        progressConnection = progressConected(msg = "Conectando...")
                        progressConnection.show()
                        rfidReaderManager.connectUsbRfid(
                            context = this,
                            onResult = { res ->
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
                })
            }
        }
    }

    private fun setupAntennaRfid(changed: Boolean? = false) {
        rfidReaderManager.configureRfidReader(
            transmitPowerIndex = powerRfid,
            rfModeTableIndex = nivelAntenna,
            session = SESSION.SESSION_S0,
            inventoryState = INVENTORY_STATE.INVENTORY_STATE_A,
            slFlag = SL_FLAG.SL_ALL,
            onResult = { res ->
                toastDefault(message = res)
                Log.e(TAG, "onResult: $res")
            },
            changed = changed
        )
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
                                    updateProximity(tag.peakRSSI.toInt()) // Atualizar proximidade
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
                if (!click) {
                    updateProximity(-90)
                }
            }
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

    private fun resultPorcentage() {
        viewModel.sucessReturnPercentage.observe(this) {
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
            seekBarPowerRfid(powerRfid, nivelAntenna) { newPower, nivel ->
                powerRfid = newPower
                nivelAntenna = nivel
                Log.e(TAG, "powerRfidClicado: $powerRfid - nivelAntenaClicado: $nivelAntenna")
                sharedPreferences.saveInt(CustomSharedPreferences.POWER_RFID, newPower)
                sharedPreferences.saveInt(CustomSharedPreferences.NIVEL_ANTENNA_RFID, nivel)
                setupAntennaRfid(changed = true)
            }
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
//                        verifyDeviceSupportBluetooth()
                        true
                    }

                    R.id.menu_option_2 -> {
                        true
                    }

                    R.id.menu_option_3 -> {
                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }
    }


    private fun isDeviceConnected(): Boolean {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            return false
        }

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_DENIED
        ) {
            // Verifique os dispositivos pareados e veja se algum está conectado
            val connectedDevices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
            for (device in connectedDevices) {
                val isConnected =
                    device.bondState == BluetoothDevice.BOND_BONDED && isConnected(device)
                if (isConnected) {
                    return true
                }
            }
        }
        return false
    }

    private fun isConnected(device: BluetoothDevice): Boolean {
        return try {
            val method = device.javaClass.getMethod("isConnected")
            method.invoke(device) as Boolean
        } catch (e: Exception) {
            false
        }
    }

    private fun initScanBluetooh(bluetoothAdapter: BluetoothAdapter) {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_DENIED
        ) {
            if (bluetoothAdapter.isDiscovering) bluetoothAdapter.cancelDiscovery()

            handler.postDelayed({ }, scanDuration)
        }
    }

    private fun addDeviceToList(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_DENIED
        ) {
            if (!discoveredDevices.contains(device)) {
                discoveredDevices.add(device)
                deviceNames.add("${device.name ?: "Desconhecido"} - ${device.address}")
                deviceListAdapter.notifyDataSetChanged()
            }
        }
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
        rfidReaderManager.setupVolumeBeep(quiet = true)
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        val binding = DialogTagProximityBinding.inflate(LayoutInflater.from(this))
        progressBar = binding.progressBarProximity
        textRssiValue = binding.textRssiValue
        builder.setView(binding.root).setTitle("Localizar a tag:\n${epcSelected ?: "-"}")
            .setNegativeButton("Fechar") { dialog, _ ->
                dialog.dismiss()
                rfidReaderManager.setupVolumeBeep(quiet = false)
                epcSelected = null
                isShowModalTagLocalization = false
            }
        proximityDialog = builder.create()
        proximityDialog.show()
    }


    // Função para atualizar o progresso e o valor de RSSI
    private fun updateProximity(rssi: Int) {
        try {
            if (progressBar != null) {
                viewModel.calculateProximityPercentage(rssi)
                val currentProgress = progressBar!!.progress
                val animation = ObjectAnimator.ofInt(
                    progressBar, "progress", currentProgress, proximityPercentage
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


    override fun RF_PerformInventoryWithRssiLimitation(
        p0: Boolean,
        p1: Boolean,
        p2: Boolean,
        p3: Int
    ): Int {
        Log.d(
            "RFInventory",
            "Iniciando inventário com limitação de RSSI. Parâmetros: $p0, $p1, $p2, Limite RSSI: $p3"
        )
        // Implementar lógica para inventário com limitação de RSSI
        return 1 // Retorna 1 para sucesso, ou outro código de erro conforme necessário
    }

    override fun RF_PerformInventory(p0: Boolean, p1: Boolean, p2: Boolean): Int {
        Log.d("RFInventory", "Iniciando inventário. Parâmetros: $p0, $p1, $p2")
        // Implementar lógica para inventário simples
        return 1 // Retorna 1 para sucesso ou 0 para falha
    }

    override fun RF_PerformInventory(p0: Boolean, p1: Boolean, p2: Boolean, p3: Boolean): Int {
        Log.d("RFInventory", "Iniciando inventário com parâmetros: $p0, $p1, $p2, $p3")

        return 1 // Retorna 1 para sucesso ou 0 para falha
    }

    override fun RF_PerformInventoryWithLocating(p0: Boolean, p1: Boolean, p2: Boolean): Int {
        Log.d("RFInventory", "Iniciando inventário com localização. Parâmetros: $p0, $p1, $p2")
        return 1 // Retorna 1 para sucesso ou 0 para falha
    }

    override fun RF_PerformInventoryWithPhaseFreq(p0: Boolean, p1: Boolean, p2: Boolean): Int {
        Log.d(
            "RFInventory",
            "Iniciando inventário com fase de frequência. Parâmetros: $p0, $p1, $p2"
        )
        return 1 // Retorna 1 para sucesso ou 0 para falha
    }

    override fun RF_PerformInventoryForLocating(p0: String?): Int {
        Log.d("RFInventory", "Iniciando inventário para localização. Parâmetro: $p0")
        // Implementar lógica para inventário para localização
        return 1 // Retorna 1 para sucesso ou 0 para falha
    }

    override fun RF_StopInventory(): Int {
        Log.d("RFInventory", "Parando inventário.")
        // Implementar lógica para parar o inventário
        return 0 // Retorna 0 para indicar sucesso, ou outro código conforme necessário
    }


}
