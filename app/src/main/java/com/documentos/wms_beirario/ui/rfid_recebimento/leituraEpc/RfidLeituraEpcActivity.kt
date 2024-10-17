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
import com.documentos.wms_beirario.utils.extensions.somSucess
import com.documentos.wms_beirario.utils.extensions.toastDefault
import com.google.android.material.chip.Chip
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.RFIDReader
import com.zebra.rfid.api3.ReaderDevice
import com.zebra.rfid.api3.Readers
import com.zebra.rfid.api3.RfidEventsListener
import com.zebra.rfid.api3.RfidReadEvents
import com.zebra.rfid.api3.RfidStatusEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RfidLeituraEpcActivity : AppCompatActivity(), RfidEventsListener {

    private lateinit var binding: ActivityRfidLeituraEpcBinding
    private lateinit var adapterLeituras: LeituraRfidAdapter
    private lateinit var rfidReader: RFIDReader
    private val TAG = "RFID"
    private var readers: Readers? = null
    private var readerList: ArrayList<ReaderDevice>? = null
    private var powerRfid: Int? = null
    private lateinit var token: String
    private var idArmazem: Int? = null
    private lateinit var sharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRfidLeituraEpcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupShared()
        clickButtonConfig()
        setupAdapter()
        cliqueChips()
        clickRfidAntenna()
        clickButtonLimpar()
        clickButtonFinalizar()
        connectReader()

    }

    private fun setupShared() {
        sharedPreferences = CustomSharedPreferences(this)
        sharedPreferences.apply {
            token = getString(CustomSharedPreferences.TOKEN) as String
            idArmazem = getInt(CustomSharedPreferences.ID_ARMAZEM)
            idArmazem = getInt(CustomSharedPreferences.ID_ARMAZEM)
            powerRfid = getInt(CustomSharedPreferences.POWER_RFID)
        }
        if (powerRfid == null) {
            powerRfid = 100
            setRfidPowerLevel(powerRfid ?: 100)
        }
    }

    private fun clickRfidAntenna() {
        binding.iconRfidSinal.setOnClickListener {
            seekBarPowerRfid(powerRfid) { newPower ->
                powerRfid = newPower
                toastDefault(message = "Potência alterada para $newPower%")
                sharedPreferences.saveInt(CustomSharedPreferences.POWER_RFID, newPower)
                setRfidPowerLevel(newPower)
            }
        }
    }

    private fun setRfidPowerLevel(powerLevel: Int) {
        try {
            // Verifique se o leitor está conectado
            if (rfidReader.isConnected) {
                val antennaConfig = rfidReader.Config.Antennas.getAntennaRfConfig(1)
                antennaConfig.transmitPowerIndex = powerLevel
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
                    readers = Readers(this@RfidLeituraEpcActivity, ENUM_TRANSPORT.SERVICE_USB)
                    readerList = readers?.GetAvailableRFIDReaderList()

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
        configureReader() // Assumindo que configureReader já lida com exceções
    }

    private fun handleConnectionFailure(message: String) {
        binding.progressRfid.isVisible = false
        binding.iconRfidSinal.isVisible = true
        toastDefault(message = message)
        binding.iconRfidSinal.setImageResource(R.drawable.icon_rfid_not_connect)
    }


    private fun configureReader() {
        try {
            rfidReader.Events.addEventsListener(this)
            rfidReader.Events.setInventoryStartEvent(true)
            rfidReader.Events.setHandheldEvent(true)
            rfidReader.Events.setTagReadEvent(true)
            rfidReader.Events.setAttachTagDataWithReadEvent(true)
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
            alertConfirmation(
                message = "Deseja limpar as leituras e iniciar novamente?",
                actionNo = {},
                actionYes = {}
            )
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
                somSucess()
                toastDefault(message = "EPC: ${data?.readEventData?.tagData?.tagID}")
            }
        }

    }

    override fun eventStatusNotify(event: RfidStatusEvents?) {
        event?.let {
            val handheldEvent = it.StatusEventData.HandheldTriggerEventData
            if (handheldEvent != null) {
                if (handheldEvent.handheldEvent.value == 1) {
                    // Gatilho pressionado, iniciar leitura
                    startReading()
                } else if (handheldEvent.handheldEvent.value == 0) {
                    // Gatilho liberado, parar leitura
                    stopReading()
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


    private fun startReading() {
        try {
            rfidReader.Actions.Inventory.perform()
            Log.d(TAG, "Iniciando leitura de tags")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Erro ao iniciar leitura: ${e.message}")
        }
    }
}
