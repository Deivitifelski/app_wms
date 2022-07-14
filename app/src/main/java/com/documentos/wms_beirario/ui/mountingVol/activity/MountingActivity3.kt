package com.documentos.wms_beirario.ui.mountingVol.activity

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityMounting3Binding
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

class MountingActivity3 : AppCompatActivity(), java.util.Observer {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMounting3Binding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initViewModel()
        setupDataWedge()
        initCons()
        callApi()
        setToolbar()
        setupRecyclerView()
        setObservable()
        editsetup()

    }

    override fun onResume() {
        super.onResume()
        initDataWedge()
        clickEditHideKey()
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
            title = "Volume | ${mIntentName.nome}"
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun callApi() {
        mViewModel.getAndressVol(mIntent.idOrdemMontagemVolume)
    }

    private fun editsetup() {
        mBinding.editMounting3.extensionSetOnEnterExtensionCodBarras {
            sendData(mBinding.editMounting3.text.toString())
        }
    }


    private fun sendData(scan: String) {
        if (scan.isNullOrEmpty()) {
            mErroToastExtension(this, "Campo Vazio!")
        } else {
            mViewModel.getAndressVol(idOrdemMontagemVolume = scan)
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
            mErroToastExtension(this, "Erro ao receber dados!")
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
            mShowShow2.observe(this@MountingActivity3) { sucess ->
                if (sucess.isNotEmpty()) {
                    mAdapter2.submitList(sucess)
                } else {
                    Toast.makeText(this@MountingActivity3, "Lista Vazia!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            //--------->
            mValidaProgressShow.observe(this@MountingActivity3) { progress ->
                mBinding.progressMounting3.isVisible = progress
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