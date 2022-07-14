package com.documentos.wms_beirario.ui.mountingVol.activity

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityMounting2Binding
import com.documentos.wms_beirario.model.mountingVol.MountingTaskResponse1
import com.documentos.wms_beirario.repository.mountingvol.MountingVolRepository
import com.documentos.wms_beirario.ui.mountingVol.adapters.AdapterMountingAndress2
import com.documentos.wms_beirario.ui.mountingVol.adapters.AdapterMountingVol2
import com.documentos.wms_beirario.ui.mountingVol.viewmodels.MountingVolViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

class MountingActivity2 : AppCompatActivity(), java.util.Observer {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMounting2Binding.inflate(layoutInflater)
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
            subtitle = "[${getVersion()}]"
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun callApi() {
        mViewModel.getNumSerieVol(mIntent.idProdutoKit.toString())
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
        mBinding.editMount2.requestFocus()
        try {
            if (intent.extras != null) {
                val prodKit = intent.getSerializableExtra("ID_PROD_KIT") as MountingTaskResponse1
                mIntent = prodKit
            }
        } catch (e: Exception) {
            mErroToastExtension(this, "Erro ao receber dados!")
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
    }

    private fun sendData(scan: String) {
        val qrCode = mAdapter.searchItem(scan)
        if (scan.isNullOrEmpty()) {
            mErroToastExtension(this, "Campo Vazio!")
        } else {
            if (qrCode != null) {
                val intent = Intent(this, MountingActivity3::class.java)
                intent.putExtra("DATA_MOUNTING2", qrCode)
                intent.putExtra("NOME", mIntent)
                startActivity(intent)
                extensionSendActivityanimation()
            }
        }
        clearEdit()
    }

    private fun setObservable() {
        mViewModel.apply {
            mShowShow.observe(this@MountingActivity2) { sucess ->
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
        }
        clearEdit()
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