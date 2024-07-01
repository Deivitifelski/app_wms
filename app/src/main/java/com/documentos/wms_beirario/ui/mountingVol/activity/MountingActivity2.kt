package com.documentos.wms_beirario.ui.mountingVol.activity

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityMounting2Binding
import com.documentos.wms_beirario.model.mountingVol.MountingTaskResponse1
import com.documentos.wms_beirario.model.mountingVol.RequestMounting6
import com.documentos.wms_beirario.repository.mountingvol.MountingVolRepository
import com.documentos.wms_beirario.ui.bluetooh.BluetoohPrinterActivity
import com.documentos.wms_beirario.ui.mountingVol.adapters.AdapterMountingVol2
import com.documentos.wms_beirario.ui.mountingVol.viewmodels.MountingVolViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

class MountingActivity2 : AppCompatActivity(), Observer {

    private lateinit var mBinding: ActivityMounting2Binding
    private lateinit var mViewModel: MountingVolViewModel2
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mAdapter: AdapterMountingVol2
    private lateinit var mIntent: MountingTaskResponse1
    private lateinit var mProgress: Dialog
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private var service: BluetoothService? = null
    private lateinit var writer: BluetoothWriter
    private var mIdOrdemMon: String = ""
    private lateinit var token: String
    private var idArmazem: Int = 0
    private lateinit var sharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMounting2Binding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initViewModel()
        setupDataWedge()
        initCons()
        setToolbar()
        setObservable()
        editsetup()
    }

    override fun onStart() {
        super.onStart()
        clickEditHideKey()
        setupRecyclerView()
        callApi()
        verificaConnect()
    }

    override fun onResume() {
        super.onResume()
        initDataWedge()
    }

    private fun initConfigPrinter() {
        service = BluetoothClassicService.getDefaultInstance()
        writer = BluetoothWriter(service)
    }

    private fun verificaConnect() {
        if (BluetoohPrinterActivity.STATUS != "CONNECTED") {
            mAlert.alertSelectPrinter(
                this,
                "Nenhuma impressora está conectada!\nDeseja se conectar a uma?",
                this
            )
        } else {
            initConfigPrinter()
        }
    }

    private fun clickEditHideKey() {
        hideKeyExtensionActivity(mBinding.editMount2)
        mBinding.editMount2.setOnClickListener {
            showKeyExtensionActivity(mBinding.editMount2)
        }
    }

    private fun setupDataWedge() {
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
    }


    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            MountingVolViewModel2.Mounting2ViewModelFactory(MountingVolRepository())
        )[MountingVolViewModel2::class.java]
    }

    private fun setToolbar() {
        mBinding.toolbarMounting2.apply {
            subtitle = getVersionNameToolbar()
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun callApi() {
        mViewModel.getNumSerieVol(mIntent.idProdutoKit.toString(), idArmazem, token)
    }

    private fun editsetup() {
        mBinding.editMount2.extensionSetOnEnterExtensionCodBarras {
            sendData(mBinding.editMount2.text.toString())
        }
    }


    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun initCons() {
        sharedPreferences = CustomSharedPreferences(this)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        mBinding.editMount2.requestFocus()
        try {
            if (intent.extras != null) {
                val prodKit = intent.getSerializableExtra("ID_PROD_KIT") as MountingTaskResponse1
                mIntent = prodKit
            }
        } catch (e: Exception) {
            toastError(this, "Erro ao receber dados!")
        }
        mProgress = CustomAlertDialogCustom().progress(this)
        mProgress.hide()
        mBinding.txtNomeKitProd.text = mIntent.nome
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    private fun setupRecyclerView() {
        mAdapter = AdapterMountingVol2()
        mBinding.apply {
            rvVolMounting2.apply {
                layoutManager = LinearLayoutManager(this@MountingActivity2)
                adapter = mAdapter
            }
        }
        /**CLIQUE NA IMAGEM DA IMPRESSORA -->*/
        mAdapter.clickPrinter = { clickImgPrinter ->
            if (BluetoohPrinterActivity.STATUS != "CONNECTED") {
                mAlert.alertSelectPrinter(
                    this,
                    "Nenhuma impressora está conectada!\nDeseja se conectar a uma?",
                    this
                )
            } else {
                mIdOrdemMon = clickImgPrinter.idOrdemMontagemVolume //set var idOrdem Montagem
                mViewModel.getPrinterMounting1(
                    clickImgPrinter.idOrdemMontagemVolume,
                    idArmazem,
                    token
                )
            }
        }
    }

    private fun sendData(scan: String) {
        val qrCode = mAdapter.searchItem(scan)
        if (scan.isNullOrEmpty()) {
            toastError(this, "Campo Vazio!")
        } else {
            if (qrCode != null) {
                mSonsMp3.somSucessReading(this)
                val intent = Intent(this, MountingActivity3::class.java)
                intent.putExtra("DATA_MOUNTING2", qrCode)
                intent.putExtra("NOME", mIntent)
                startActivity(intent)
                extensionSendActivityanimation()
            } else {
                mAlert.alertMessageErrorSimples(this, "Número de série inválido!")
            }
        }
        clearEdit()
    }

    private fun setObservable() {
        mViewModel.apply {
            mShowShow.observe(this@MountingActivity2) { sucess ->
                sucess.forEach {
                    Log.e("TAG", "NÚMERO DE SÉRIE --> ${it.numeroSerie}")
                }
                if (sucess.isNotEmpty()) {
                    mAdapter.submitList(sucess)
                } else {
                    mBinding.txtInfMounting2.text = "Sem Volumes"
                }
            }
            //--------->
            mErrorShow.observe(this@MountingActivity2) { error ->
                mAlert.alertMessageErrorSimples(this@MountingActivity2, error)
            }
            mValidaProgressShow.observe(this@MountingActivity2) { progress ->
                mBinding.progressMounting2.isVisible = progress
            }
            //--------SUCESSO SEGUNDA LEITURA------>
            mShowShow2.observe(this@MountingActivity2) { sucess2 ->
                if (sucess2.isNotEmpty()) {
                    mBinding.layoutMounting1.visibility = View.GONE
                } else {
                    mBinding.txtInfMounting2.text = "Sem Volumes"
                }
            }
            /**SUCESSO AO IMPRIMIR -->*/
            mSucessPrinterShow.observe(this@MountingActivity2) { printer ->
                try {
                    setImpressaoUnica()
                    mBinding.progressMounting2.isVisible = true
                    writer.write(printer.codigoZpl)
                    Toast.makeText(
                        this@MountingActivity2,
                        "Imprimindo:\n${printer.descricaoEtiqueta}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        mBinding.progressMounting2.isVisible = false
                    }, 1600)

                } catch (e: Exception) {
                    mBinding.progressMounting2.isVisible = false
                    Toast.makeText(
                        this@MountingActivity2,
                        "Erro ao enviar zpl a impressora! $e",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        clearEdit()
    }

    private fun setImpressaoUnica() {
        try {
            val body = RequestMounting6(idOrdemMontagemVolume = mIdOrdemMon)
            mViewModel.setImpressaoUni(body, idArmazem, token)
        } catch (e: Exception) {
            mToast.toastDefault(this, "Erro ao setar impresão!")
        }
    }

    private fun clearEdit() {
        mBinding.editMount2.apply {
            text?.clear()
            setText("")
            requestFocus()
        }
        UIUtil.hideKeyboard(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
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


    override fun onDestroy() {
        super.onDestroy()
        mProgress.dismiss()
        unregisterReceiver(receiver)
    }
}