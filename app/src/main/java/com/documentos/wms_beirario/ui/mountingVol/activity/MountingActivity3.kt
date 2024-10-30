package com.documentos.wms_beirario.ui.mountingVol.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityMounting3Binding
import com.documentos.wms_beirario.databinding.LayoutAlertSucessCustomBinding
import com.documentos.wms_beirario.model.mountingVol.MountingTaskResponse1
import com.documentos.wms_beirario.model.mountingVol.ResponseMounting2Item
import com.documentos.wms_beirario.repository.mountingvol.MountingVolRepository
import com.documentos.wms_beirario.ui.mountingVol.adapters.AdapterMountingAndress2
import com.documentos.wms_beirario.ui.mountingVol.viewmodels.MountingVolViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

class MountingActivity3 : AppCompatActivity(), Observer {

    private lateinit var mBinding: ActivityMounting3Binding
    private lateinit var mViewModel: MountingVolViewModel2
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mAdapter2: AdapterMountingAndress2
    private lateinit var mIntent: ResponseMounting2Item
    private lateinit var mIntentName: MountingTaskResponse1
    private lateinit var mProgress: Dialog
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var token: String
    private var idArmazem: Int = 0
    private lateinit var sharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMounting3Binding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initViewModel()
        setupDataWedge()
        initCons()
        setToolbar()
        setObservable()
        editsetup()

    }

    override fun onResume() {
        super.onResume()
        initDataWedge()
        clickEditHideKey()
        setupRecyclerView()
        callApi()
    }

    private fun clickEditHideKey() {
        hideKeyExtensionActivity(mBinding.editMounting3)
        mBinding.editMounting3.setOnClickListener {
            showKeyExtensionActivity(mBinding.editMounting3)
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
        mBinding.toolbarMounting3.apply {
            title = "Volume | ${mIntent.numeroSerie}"
            subtitle = getVersionNameToolbar()
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun callApi() {
        mViewModel.getAndressVol(mIntent.idOrdemMontagemVolume, idArmazem, token)
    }

    private fun editsetup() {
        mBinding.editMounting3.extensionSetOnEnterExtensionCodBarras {
            sendData(mBinding.editMounting3.text.toString())
        }
    }


    private fun sendData(scan: String) {
        if (scan.isNullOrEmpty()) {
            toastError(this, "Campo Vazio!")
        } else {
            val qrCode = mAdapter2.searchItem(scan)
            if (qrCode != null) {
                mSonsMp3.somSucessReading(this)
                val intent = Intent(this, MountingActivity4::class.java)
                intent.putExtra("DATA_MOUNTING3", qrCode)
                intent.putExtra("DATA_MOUNTING2", mIntent)
                Log.e("montagem 3", "ENVIANDO PARA MONTAGEM 4 --> $qrCode || $mIntent ")
                startActivity(intent)
                extensionSendActivityanimation()
            } else {
                mAlert.alertMessageErrorSimples(this, "Endereço Inválido!")
            }
        }
        clearEdit()
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
        mBinding.editMounting3.requestFocus()
        try {
            if (intent.extras != null) {
                val prodKit = intent.getSerializableExtra("DATA_MOUNTING2") as ResponseMounting2Item
                val name = intent.getSerializableExtra("NOME") as MountingTaskResponse1
                mIntent = prodKit
                mIntentName = name
                mBinding.txtNomeMounting3.text = mIntentName.nome
            }
        } catch (e: Exception) {
            toastError(this, "Erro ao receber dados!")
        }
        mProgress = CustomAlertDialogCustom().progress(this)
        mProgress.hide()
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    private fun setupRecyclerView() {
        mAdapter2 = AdapterMountingAndress2()
        mBinding.apply {
            rvMounting2.apply {
                layoutManager = LinearLayoutManager(this@MountingActivity3)
                adapter = mAdapter2
            }
        }
    }

    private fun setObservable() {
        mViewModel.apply {
            mErrorShow.observe(this@MountingActivity3) { error ->
                mAlert.alertMessageErrorSimples(this@MountingActivity3, error)
            }
            //--------->
            mValidaProgressShow.observe(this@MountingActivity3) { progress ->
                mBinding.progressMounting3.isVisible = progress
            }

            mShowShow2.observe(this@MountingActivity3) { sucess ->
                sucess.forEach {
                    Log.e("mounting3", "COD BARRAS --> ${it.codigoBarras} ")
                }
                if (sucess.isEmpty()) {
                    alertMessageSucess("Montagem concluida!")
                } else {
                    mAdapter2.submitList(sucess)
                }
            }
        }
        clearEdit()
    }

    private fun clearEdit() {
        mBinding.editMounting3.apply {
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
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            sendData(scanData.toString())
            clearEdit()

        }
    }

    /**
     * CRIAR MODAL : tarefas finalizadas
     */
    private fun alertMessageSucess(message: String) {
        val mAlert = AlertDialog.Builder(this)
        mAlert.setCancelable(false)
        val binding = LayoutAlertSucessCustomBinding.inflate(layoutInflater)
        mAlert.setView(binding.root)
        val mShow = mAlert.show()
        mAlert.create()
        binding.editCustomAlertSucess.addTextChangedListener {
            if (it.toString() != "") {
                mShow.dismiss()
            }
        }
        binding.txtMessageSucess.text = message
        binding.buttonSucessLayoutCustom.setOnClickListener {
            CustomMediaSonsMp3().somClick(this)
            mShow.dismiss()
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mProgress.dismiss()
        unregisterReceiver(receiver)
    }
}