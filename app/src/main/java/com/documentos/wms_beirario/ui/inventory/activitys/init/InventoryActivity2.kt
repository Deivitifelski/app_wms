package com.documentos.wms_beirario.ui.inventory.activitys.init

import InventoryReadingViewModel2
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityInventory2Binding
import com.documentos.wms_beirario.databinding.LayoutAlertAtencaoOptionsBinding
import com.documentos.wms_beirario.model.inventario.*
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import com.documentos.wms_beirario.ui.inventory.activitys.bottomNav.ShowAndressInventoryActivity
import com.documentos.wms_beirario.ui.inventory.activitys.createVoid.CreateVoidInventoryActivity
import com.documentos.wms_beirario.ui.inventory.adapter.AdapterInventory2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*


class InventoryActivity2 : AppCompatActivity(), Observer {

    private lateinit var mViewModel: InventoryReadingViewModel2
    private val TAG = "InventoryActivity2"
    private lateinit var mAdapter: AdapterInventory2
    private lateinit var mBinding: ActivityInventory2Binding
    private lateinit var mProcess: RequestInventoryReadingProcess
    private var mIdAndress: Int? = null
    private lateinit var mNewResultObj: ProcessaLeituraResponseInventario2
    private lateinit var mNewResultObjIfNull: ProcessaLeituraResponseInventario2
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mListQrCode2: MutableList<ResponseQrCode2>
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mIntentDataActivity1: ResponseInventoryPending1


    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityInventory2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        initConst()
        mListQrCode2 = mutableListOf()
        startIntent()
        initRecyclerView()
        hideViews()
        initViewModel()
        setObservable()
        setTollbar()
        setupEditQrcode()
    }

    private fun startIntent() {
        try {
            val getData = intent
            val mData = getData.getSerializableExtra("DATA_ACTIVITY_1")
            mIntentDataActivity1 = mData as ResponseInventoryPending1
            Log.e(TAG, "startIntent -> $mIntentDataActivity1")
        } catch (e: Exception) {
            mErrorShow("Erro ao receber dados!")
        }
    }


    override fun onResume() {
        super.onResume()
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
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
        mBinding.toolbar3.setNavigationOnClickListener {
            onBackPressed()
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
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
        mBinding.progressBar.isVisible = false
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    private fun setupEditQrcode() {
        mBinding.editQrcode.requestFocus()
        mBinding.editQrcode.extensionSetOnEnterExtensionCodBarras {
            sendDataApi(mBinding.editQrcode.text.toString().trim().uppercase())
            clearEdit()
        }
    }

    private fun sendDataApi(barcode: String) {
        if (barcode.isNotEmpty()) {
            UIUtil.hideKeyboard(this)
            /**CRIANDO O OBJETO A SER ENVIADO ->*/
            mProcess = RequestInventoryReadingProcess(
                mIntentDataActivity1.id,
                numeroContagem = mIntentDataActivity1.numeroContagem,
                idEndereco = mIdAndress, // --> PRIMEIRA LEITURA == NULL
                codigoBarras = barcode
            )
            mBinding.editQrcode.setText("")
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
            addListLiveData(response)
            clearEdit()
            //Quando objeto vir com idEndereço null -->
            if (response.result.enderecoVisual == null) {
                mNewResultObjIfNull = ProcessaLeituraResponseInventario2(
                    codigoBarras = mNewResultObj.codigoBarras,
                    idEndereco = mNewResultObj.idEndereco,
                    enderecoVisual = mNewResultObj.enderecoVisual,
                    idInventarioAbastecimentoItem = response.result.idInventarioAbastecimentoItem,
                    idProduto = response.result.idProduto,
                    layoutEtiqueta = response.result.layoutEtiqueta,
                    numeroSerie = response.result.numeroSerie,
                    sku = response.result.sku,
                    produtoPronto = response.result.produtoPronto,
                    produtoVolume = response.result.produtoVolume,
                    EAN = response.result.EAN
                )
                setViews(mNewResultObjIfNull, response.leituraEnderecoCreateRvFrag2)
                clickButton(mNewResultObjIfNull)
            } else {
                mNewResultObj = response.result
                clickButton(mNewResultObj)
                //Quando objeto NAO vir com idEndereço null e for a primeira leitura -->
                if (mIdAndress == null) {
                    mIdAndress = response.result.idEndereco
                    setViews(response.result, response.leituraEnderecoCreateRvFrag2)
                    //Validar se chama o dialog de troca de endereço ou nao -->
                } else {
                    if (mIdAndress == response.result.idEndereco || response.result.idEndereco == null || response.result.idEndereco == 0) {
                        setViews(response.result, response.leituraEnderecoCreateRvFrag2)
                    } else {
                        alertDialog(response.result.idEndereco)
                    }
                }
            }
        }

        mViewModel.mSucessComparationShow2.observe(this) { responseDialog ->
            mNewResultObj = responseDialog.result
            setViews(responseDialog.result, responseDialog.leituraEnderecoCreateRvFrag2)
        }

        /**ERRO LEITURA -->*/
        mViewModel.mErrorShow.observe(this) { messageError ->
            vibrateExtension(500)
            mAlert.alertMessageErrorSimples(this, messageError, 2000)
        }
        /**VALIDA PROGRESSBAR -->*/
        mViewModel.mValidaProgressShow.observe(this) { validaProgress ->
            mBinding.progressBar.isVisible = validaProgress
        }

        mViewModel.mErrorAllShow.observe(this, { errorAll ->
            mAlert.alertMessageErrorSimples(this, errorAll, 2000)
        })

    }

    private fun addListLiveData(response: ResponseQrCode2) {
        if (mListQrCode2.isNullOrEmpty()) {
            mListQrCode2.add(0, response)
        } else {
            mListQrCode2.add(1, response)
            mListQrCode2.removeAt(1)
            Log.e("LISTA", "addListLiveData --> $mListQrCode2")
        }
    }

    private fun setViews(
        response: ProcessaLeituraResponseInventario2,
        leituraEnderecoCreateRvFrag2: List<LeituraEndInventario2List>
    ) {
        if (response.produtoPronto == null) {
            mBinding.itTxtProdutos.text = "0"
        } else {
            mBinding.itTxtProdutos.text = response.produtoPronto.toString()
        }
        mBinding.itTxtEndereco.text = response.enderecoVisual
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

    private fun alertDialog(mNewIdEndereco: Int) {
        vibrateExtension(500)
        mSonsMp3.somError(this)
        val mAlert = AlertDialog.Builder(this)
        mAlert.setCancelable(false)
        val mBindinginto =
            LayoutAlertAtencaoOptionsBinding.inflate(LayoutInflater.from(this))
        mAlert.setView(mBindinginto.root)
        val mShow = mAlert.show()
        mBindinginto.txtMessageAtencao.text = getString(R.string.deseja_manter_endereço)
        mBindinginto.buttonSimAlert.setOnClickListener {
            mIdAndress = null
            mProcess = RequestInventoryReadingProcess(
                mIntentDataActivity1.id,
                numeroContagem = mIntentDataActivity1.numeroContagem,
                idEndereco = mIdAndress,
                codigoBarras = mListQrCode2[0].result.codigoBarras.toString()
            )
            mBinding.editQrcode.setText("")
            /**ENVIANDO OBJETO  ->*/
            mViewModel.readingQrCode(
                inventoryReadingProcess = mProcess
            )
            mBinding.editQrcode.setText("")
            mShow.dismiss()
        }
        mBindinginto.buttonNaoAlert.setOnClickListener {
            val newObj = RequestInventoryReadingProcess(
                idEndereco = mNewIdEndereco,
                numeroContagem = mProcess.numeroContagem,
                idInventario = mProcess.idInventario,
                codigoBarras = mProcess.codigoBarras
            )
            mIdAndress = mNewIdEndereco
            mViewModel.readingQrCodeDialog(newObj)
            mBinding.editQrcode.setText("")
            mShow.hide()
        }
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
                startActivity(intent)
                extensionStarActivityanimation(this@InventoryActivity2)
            }
        }
    }


    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            try {
                if (scanData!!.isNotEmpty()) {
                    sendDataApi(scanData.toString())
                    Log.e(TAG, "onNewIntent --> $scanData")
                    clearEdit()
                } else {
                    mErrorShow("Erro ao receber dados!")
                }
            } catch (e: Exception) {
                mErrorShow(e.toString())
            }
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

    private fun mErrorShow(title: String) {
        vibrateExtension(500)
        mToast.toastCustomError(this, title)
    }

    private fun mSucessShow(title: String) {
        vibrateExtension(500)
        mToast.toastCustomSucess(this, title)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}