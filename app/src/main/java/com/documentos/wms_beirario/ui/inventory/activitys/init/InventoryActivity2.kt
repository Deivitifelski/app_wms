package com.documentos.wms_beirario.ui.inventory.activitys.init

import InventoryReadingViewModel2
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityInventory2Binding
import com.documentos.wms_beirario.model.inventario.*
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import com.documentos.wms_beirario.ui.bluetooh.BluetoohPrinterActivity
import com.documentos.wms_beirario.ui.inventory.activitys.bottomNav.ShowAndressInventoryActivity
import com.documentos.wms_beirario.ui.inventory.activitys.createVoid.CreateVoidInventoryActivity
import com.documentos.wms_beirario.ui.inventory.adapter.AdapterInventory2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*


class InventoryActivity2 : AppCompatActivity(), Observer {

    private lateinit var mViewModel: InventoryReadingViewModel2
    private val TAG = "InventoryActivity2"
    private lateinit var mAdapter: AdapterInventory2
    private lateinit var mBinding: ActivityInventory2Binding
    private lateinit var mProcess: RequestInventoryReadingProcess
    private var mIdAndress: Int? = null
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private var mAndressVisual: String? = null
    private lateinit var mListQrCode2: MutableList<ResponseQrCode2>
    private var mCodeLido: String = ""
    private var mCodeLidoInit: String = ""
    private lateinit var mIntentDataActivity1: ResponseInventoryPending1
    private var service: BluetoothService? = null
    private lateinit var writer: BluetoothWriter
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false

    //RECEBE OS DADOS NOVAMENTE PARA ATUALIZARRTELA -->
    private val result =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val data =
                        result.data?.getSerializableExtra("DATA_INVENTORY_2") as ProcessaLeituraResponseInventario2
                    mProcess = RequestInventoryReadingProcess(
                        mIntentDataActivity1.id,
                        numeroContagem = mIntentDataActivity1.numeroContagem,
                        idEndereco = mIdAndress,
                        codigoBarras = data.codigoBarras.toString()
                    )
                    mCodeLidoInit = ""
                    /**ENVIANDO OBJETO  ->*/
                    mViewModel.readingQrCode(
                        inventoryReadingProcess = mProcess
                    )
                } catch (e: Exception) {
                    Toast.makeText(this, "Erro ao receber dados", Toast.LENGTH_SHORT).show()
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityInventory2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mListQrCode2 = mutableListOf()
        startIntent()
        initRecyclerView()
        initConst()
        hideViews()
        initViewModel()
        setObservable()
        setTollbar()
        setupDataWedge()
        setupEditQrcode()
        observConectPrint()
        hideKeyExtensionActivity(mBinding.editQrcode)
    }

    override fun onResume() {
        super.onResume()
        initDataWedge()
    }

    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun initConfigPrinter() {
        service = BluetoothClassicService.getDefaultInstance()
        writer = BluetoothWriter(service)
    }


    private fun observConectPrint() {
        if (BluetoohPrinterActivity.STATUS != "CONNECTED") {
            CustomAlertDialogCustom().alertSelectPrinter(this, activity = this)
        } else {
            initConfigPrinter()
        }
    }

    private fun startIntent() {
        try {
            val getData = intent
            val mData = getData.getSerializableExtra("DATA_ACTIVITY_1")
            mIntentDataActivity1 = mData as ResponseInventoryPending1
            Log.e(TAG, "startIntent -> $mIntentDataActivity1")
        } catch (e: Exception) {
            mErroToastExtension(this, "Erro ao receber dados!")
        }
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this, InventoryReadingViewModel2.ReadingFragiewModelFactory(
                InventoryoRepository1()
            )
        )[InventoryReadingViewModel2::class.java]
    }

    private fun setTollbar() {
        mBinding.toolbar3.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = getVersionNameToolbar()
        }

    }

    private fun initRecyclerView() {
        mAdapter = AdapterInventory2()
        mBinding.rvLastReading.apply {
            layoutManager = LinearLayoutManager(this@InventoryActivity2)
            adapter = mAdapter
        }
    }

    private fun initConst() {
        mBinding.progressBar.isVisible = false
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    private fun setupEditQrcode() {
        mBinding.editQrcode.requestFocus()
    }


    private fun sendDataApi(barcode: String) {
        if (barcode.isNotEmpty()) {
            UIUtil.hideKeyboard(this)
            /**CRIANDO O OBJETO A SER ENVIADO ->*/
            mCodeLido = barcode
            mProcess = RequestInventoryReadingProcess(
                mIntentDataActivity1.id,
                numeroContagem = mIntentDataActivity1.numeroContagem,
                idEndereco = mIdAndress, // --> PRIMEIRA LEITURA == NULL
                codigoBarras = barcode
            )
            /**ENVIANDO OBJETO  ->*/
            mViewModel.readingQrCode(
                inventoryReadingProcess = mProcess
            )
            mBinding.editQrcode.setText("")
        }
    }

    private fun setObservable() {
        /**SUCESSO LEITURA -->*/
        mViewModel.mSucessShow.observe(this) { response ->
            clearEdit()
            if (response.result.layoutEtiqueta != null) {
                mBinding.progressBar.isVisible = true
                printerLayout(response.result.layoutEtiqueta)
            }
            mBinding.editQrcode.hint = "Leia um Ean ou num.Série:"
            if (response.result.idEndereco != null) {
                if (mCodeLido == mCodeLidoInit || mCodeLidoInit == "") {
                    mAndressVisual = response.result.enderecoVisual.toString()
                    mIdAndress = response.result.idEndereco
                    mCodeLidoInit = response.result.codigoBarras.toString()
                    setViews(response.result, response.leituraEnderecoCreateRvFrag2)
                    clickButton(response.result)
                } else {
                    alertDialog(response.result)
                }
            } else {
                setViews(response.result, response.leituraEnderecoCreateRvFrag2)
            }
        }
        /**ERRO LEITURA -->*/
        mViewModel.mErrorShow.observe(this) { messageError ->
            clearEdit()
            mAlert.alertMessageErrorSimples(this, messageError)
        }
        /**VALIDA PROGRESSBAR -->*/
        mViewModel.mValidaProgressShow.observe(this) { validaProgress ->
            mBinding.progressBar.isVisible = validaProgress
        }

        mViewModel.mErrorAllShow.observe(this) { errorAll ->
            clearEdit()
            mAlert.alertMessageErrorSimples(this, errorAll)
        }

    }

    private fun printerLayout(layoutEtiqueta: String) {
        if (BluetoohPrinterActivity.STATUS == "CONNECTED") {
            try {
                lifecycleScope.launch(Dispatchers.Default) {
                    writer.write(layoutEtiqueta)
                }
            } catch (e: Exception) {
                Log.e(TAG, "ERRO AO TENTAR IMPRIMIR --> $layoutEtiqueta")
            }
        } else {
            mBinding.progressBar.isVisible = false
            vibrateExtension(500)
            mAlert.alertSelectPrinter(
                this, getString(R.string.printer_of_etiquetagem_modal), activity = this
            )
        }
    }

    private fun setViews(
        response: ProcessaLeituraResponseInventario2,
        leituraEnderecoCreateRvFrag2: List<LeituraEndInventario2List>,
    ) {
        if (response.produtoPronto == null) {
            mBinding.itTxtProdutos.text = "0"
        } else {
            mBinding.itTxtProdutos.text = response.produtoPronto.toString()
        }

        mBinding.itTxtEndereco.text = mAndressVisual
        mBinding.linearParent.visibility = View.VISIBLE
        mBinding.linearButton.visibility = View.VISIBLE
        mBinding.itTxtVolumes.text = response.produtoVolume.toString()
        mAdapter.submitList(leituraEnderecoCreateRvFrag2)
    }

    private fun hideViews() {
        mBinding.apply {
            this.linearButton.visibility = View.INVISIBLE
            linearParent.visibility = View.INVISIBLE
        }
    }

    /**
     * ENDEREÇO VISUAL APOS LER UM EAN RETURN NULL
     */
    private fun alertDialog(mResponse: ProcessaLeituraResponseInventario2) {
        vibrateExtension(500)
        mAlert.alertMessageAtencaoOptionAction(this,
            getString(R.string.deseja_manter_endereço),
            actionYes = {
                /**ENVIANDO OBJETO  ->*/
                mCodeLidoInit = mResponse.codigoBarras.toString()
                mProcess = RequestInventoryReadingProcess(
                    mIntentDataActivity1.id,
                    numeroContagem = mIntentDataActivity1.numeroContagem,
                    idEndereco = mIdAndress, // --> PRIMEIRA LEITURA == NULL
                    codigoBarras = mResponse.codigoBarras.toString()
                )
                mCodeLido = mProcess.codigoBarras
                mViewModel.readingQrCode(
                    inventoryReadingProcess = mProcess
                )
                mBinding.editQrcode.setText("")
            },
            actionNo = {
                Toast.makeText(this, "Endereço mantido", Toast.LENGTH_SHORT).show()
                mBinding.editQrcode.setText("")
            })
    }

    private fun clickButton(responseQrCode: ProcessaLeituraResponseInventario2) {
        mBinding.apply {
            /**CLICK BUTTON VER ENDEREÇO -->*/
            buttonVerEnd.setOnClickListener {
                vibrateExtension(100)
                val intent =
                    Intent(this@InventoryActivity2, ShowAndressInventoryActivity::class.java)
                intent.putExtra("SEND_ANDRESS_REANDING_QRCODE", responseQrCode)
                intent.putExtra("SEND_ANDRESS_RESPONSE_ACTIVITY_1", mIntentDataActivity1)
                startActivity(intent)
                extensionStarActivityanimation(this@InventoryActivity2)
            }

            /**CLICK BUTTON AVULSO -->*/
            buttonAvulso.setOnClickListener {
                val intent =
                    Intent(this@InventoryActivity2, CreateVoidInventoryActivity::class.java)
                intent.putExtra("SEND_ANDRESS_REANDING_QRCODEAvulSo", responseQrCode)
                intent.putExtra("SEND_ANDRESS_RESPONSE_ACTIVITY_1Avulso", mIntentDataActivity1)
                result.launch(intent)
                extensionStarActivityanimation(this@InventoryActivity2)
            }
        }
    }

    private fun setupDataWedge() {
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
    }

    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            sendDataApi(scanData.toString())
            Log.e(TAG, "SCAN RECEBIDO -> $scanData")
            clearEdit()
        }
    }

    private fun clearEdit() {
        mBinding.editQrcode.setText("")
        mBinding.editQrcode.requestFocus()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}