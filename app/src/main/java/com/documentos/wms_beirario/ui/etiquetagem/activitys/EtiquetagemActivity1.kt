package com.documentos.wms_beirario.ui.etiquetagem.activitys

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityEtiquetagem1Binding
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequest1
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import com.documentos.wms_beirario.ui.bluetooh.BluetoohPrinterActivity
import com.documentos.wms_beirario.ui.bluetooh.BluettohLIbrary
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.ui.etiquetagem.viewmodel.EtiquetagemFragment1ViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class EtiquetagemActivity1 : AppCompatActivity(), Observer {
    private lateinit var mBinding: ActivityEtiquetagem1Binding
    private lateinit var mViewModel: EtiquetagemFragment1ViewModel
    private lateinit var mAlert: CustomAlertDialogCustom
    private val TAG = "EtiquetagemActivity1"
    private lateinit var mToast: CustomSnackBarCustom
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mDialog: Dialog
    private var service: BluetoothService? = null
    private lateinit var writer: BluetoothWriter

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityEtiquetagem1Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        setToolbar()
        initDialog()
        initViewModel()
        setObservable()
        setupEdit()
        clickButton()
        setupDataWedge()
    }

    private fun initConfigPrinter() {
        service = BluetoothClassicService.getDefaultInstance()
        writer = BluetoothWriter(service)
    }

    override fun onStart() {
        super.onStart()
        verificationsBluetooh()
    }

    override fun onResume() {
        super.onResume()
        initDataWedge()
        hideKeyExtensionActivity(mBinding.editEtiquetagem)
    }


    private fun initDialog() {
        mAlert = CustomAlertDialogCustom()
        mDialog = CustomAlertDialogCustom().progress(this)
        mDialog.hide()
    }


    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            EtiquetagemFragment1ViewModel.Etiquetagem1ViewModelFactory(EtiquetagemRepository())
        )[EtiquetagemFragment1ViewModel::class.java]
    }

    /**VERIFICA SE JA TEM IMPRESSORA CONECTADA!!--->*/
    private fun verificationsBluetooh() {
        if (SetupNamePrinter.mNamePrinterString.isEmpty()) {
            mAlert.alertSelectPrinter(this)
        } else {
            initConfigPrinter()
        }
    }

    private fun setToolbar() {
        setSupportActionBar(mBinding.toolbar)
        mToast = CustomSnackBarCustom()
        mBinding.toolbar.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = getVersionNameToolbar()
        }
    }

    private fun setupEdit() {
        mBinding.editEtiquetagem.requestFocus()
        mBinding.editEtiquetagem.extensionSetOnEnterExtensionCodBarras {
            sendData(mBinding.editEtiquetagem.text.toString())
        }
    }


    /**ENVIANDO PARA OUTRAS ACTIVITYS -->*/
    private fun clickButton() {
        mBinding.buttonPendencePorNf.setOnClickListener {
            startActivity(Intent(this, EtiquetagemPendenciaNFActivity::class.java))
            extensionSendActivityanimation()
        }
        mBinding.buttonPendencePorPendency.setOnClickListener {
            startActivity(Intent(this, EtiquetagemPedidoActivity::class.java))
            extensionSendActivityanimation()
        }
        mBinding.buttonPendencePorOnda.setOnClickListener {
            startActivity(Intent(this, EtiquetagemPendenciaOndaActivity::class.java))
            extensionSendActivityanimation()
        }

        mBinding.buttonPendencePorRequisicao.setOnClickListener {
            startActivity(Intent(this, EtiquetagemPendenciaRequisicaoActivity::class.java))
            extensionSendActivityanimation()
        }
    }

    private fun setObservable() {
        mViewModel.mSucessShow.observe(this) { zpl ->
            mDialog.hide()
            clearEdit()
            /**INSTANCIANDO PRINTER E ENVIANDO ARRAY QUE PODE SR 1 OU MAIS ZPLs -->*/
            try {
                lifecycleScope.launch(Dispatchers.Default) {
                    zpl.forEach {
                        writer.write(it.codigoZpl)
                    }
                }

                Toast.makeText(this@EtiquetagemActivity1, "Imprimindo...", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                mErrorToast("Erro ao tentar imprimir!")
            }
        }

        //ERROR ->
        mViewModel.mErrorShow.observe(this) { messageError ->
            clearEdit()
            mDialog.hide()
            mAlert.alertMessageErrorSimples(this, messageError)
        }
        //ERROS GERAIS -->
        mViewModel.mErrorAllShow.observe(this) { errorAll ->
            mDialog.hide()
            clearEdit()
            mAlert.alertMessageErrorSimples(this, errorAll)
        }
    }

    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }


    private fun sendData(scan: String) {
        try {
            if (SetupNamePrinter.mNamePrinterString.isEmpty()) {
                vibrateExtension(500)
                mAlert.alertSelectPrinter(this, getString(R.string.printer_of_etiquetagem_modal))
                clearEdit()
            } else if (scan.isNotEmpty()) {
                mDialog.show()
                mViewModel.etiquetagemPost(etiquetagemRequest1 = EtiquetagemRequest1(scan))
                clearEdit()
            }
        } catch (e: Exception) {
            mErrorToast(e.toString())
        }
    }

    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            sendData(scanData.toString())
            clearEdit()
        }
    }

    private fun setupDataWedge() {
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
    }

    private fun mErrorToast(toString: String) {
        vibrateExtension(500)
        mToast.toastCustomError(this, toString)
    }

    private fun clearEdit() {
        mBinding.editEtiquetagem.setText("")
        mBinding.editEtiquetagem.text?.clear()
        mBinding.editEtiquetagem.requestFocus()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_printer -> {
                extensionStartActivity(BluetoohPrinterActivity())
            }
        }
        return true
    }

    /**CLICK MENU ----------->*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_open_printer, menu)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mDialog.dismiss()
    }
}