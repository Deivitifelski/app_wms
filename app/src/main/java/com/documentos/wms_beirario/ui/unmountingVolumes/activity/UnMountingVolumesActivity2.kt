package com.documentos.wms_beirario.ui.unmountingVolumes.activity

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityUnMountingVolumes2Binding
import com.documentos.wms_beirario.model.desmontagemVol.RequestDisassamblyVol
import com.documentos.wms_beirario.model.desmontagemVol.UnmountingVolumes1Item
import com.documentos.wms_beirario.repository.desmontagemvolumes.DisassemblyRepository
import com.documentos.wms_beirario.ui.unmountingVolumes.adapter.Disassambly2Adapter
import com.documentos.wms_beirario.ui.unmountingVolumes.viewModel.ViewModelInmounting2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import java.util.*

class UnMountingVolumesActivity2 : AppCompatActivity(), Observer {

    private lateinit var mBinding: ActivityUnMountingVolumes2Binding
    private lateinit var mAdapter: Disassambly2Adapter
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mSons: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mViewModel: ViewModelInmounting2
    private lateinit var mIntent: UnmountingVolumes1Item
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mAlertFinish: AlertDialog.Builder
    private lateinit var mProgress: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityUnMountingVolumes2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        setupToolbar()
        initIntent()
        initConst()
        setupDataWedge()
        initAdapter()
        initData()
        setObservables()
        setupEdit()
    }

    override fun onResume() {
        super.onResume()
        mProgress.hide()
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
        hideKeyExtensionActivity(mBinding.editMount2)
    }

    private fun setupToolbar() {
        mBinding.unMonting2.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = getVersionNameToolbar()
        }
    }

    private fun setupEdit() {
        mBinding.editMount2.apply {
            requestFocus()
            extensionSetOnEnterExtensionCodBarras {
                senData(mBinding.editMount2.text.toString())
            }
            setOnClickListener {
                showKeyExtensionActivity(mBinding.editMount2)
            }
        }
    }

    private fun initIntent() {
        try {
            val mGetIntent: Intent = intent
            if (mGetIntent.extras != null) {
                val data = mGetIntent.getSerializableExtra("ITEM_CLICK_1") as UnmountingVolumes1Item
                mIntent = data
                mBinding.unMonting2.title = mIntent.enderecoVisual
            } else {
                mErrorToast("Erro ao receber dados tela anterior!")
            }
        } catch (e: Exception) {
            mErrorToast(e.toString())
        }
    }

    private fun initData() {
        mViewModel.getTaskDisassembly2(mIntent.idEndereco)
    }

    private fun senData(scan: String) {
        mBinding.editMount2.text!!.clear()
        mBinding.editMount2.setText("")
        if (scan.isNullOrEmpty()) {
            mBinding.editLayoutUnMount2.shake {
                mErroToastExtension(this, getString(R.string.edit_emply))
            }
        } else {
            mViewModel.postReanding(
                body = RequestDisassamblyVol(
                    idEndereco = mIntent.idEndereco,
                    numeroSerie = scan
                )
            )
            clearText()
        }
    }

    private fun initAdapter() {
        mBinding.rvUnMonting2.apply {
            layoutManager = LinearLayoutManager(this@UnMountingVolumesActivity2)
            adapter = mAdapter
        }
    }

    private fun setObservables() {
        mViewModel.mProgressShow.observe(this) { progress ->
            if (progress) mProgress.show() else mProgress.hide()
        }

        mViewModel.mSucessShow.observe(this) { listSucess ->
            try {
                mBinding.editMount2.text!!.clear()
                mBinding.editMount2.setText("")
                if (listSucess.isEmpty()) {
                    mAlert.alertMessageSucessFinishBack(
                        this,
                        this,
                        "Todos os volumes foram desmontados!"
                    )
                } else {
                    mAdapter.update(listSucess)
                }
            } catch (e: Exception) {
                mErrorToast("Erro ao receber dados da API:\n${e.cause}")
            }
        }

        mViewModel.mErrorAllShow.observe(this) { error ->
            clearText()
            mAlert.alertMessageErrorSimples(this, error)
        }
        mViewModel.mErrorHttpShow.observe(this) { error ->
            clearText()
            mAlert.alertMessageErrorSimples(this, error)
        }

        //SUCESS LETURA -->
        mViewModel.mSucessReandingFinishShow.observe(this) {
            try {
                mBinding.progressMonting2.isVisible = true
                initAdapter()
                initData()
                mSons.somSucess(this)
                Handler(Looper.getMainLooper()).postDelayed({
                    mBinding.progressMonting2.isVisible = false
                }, 500)
            } catch (e: Exception) {
                mErrorToast("Erro ao receber sucesso da leitura!\n${e.message}")
            }
        }

    }

    private fun mErrorToast(msg: String) {
        vibrateExtension(500)
        mToast.toastCustomError(this, msg)
    }

    private fun initConst() {
        mViewModel = ViewModelProvider(
            this, ViewModelInmounting2.UnMounting1ViewModelFactory2(
                DisassemblyRepository()
            )
        )[ViewModelInmounting2::class.java]
        mProgress = CustomAlertDialogCustom().progress(this, "Aguarde...")
        mBinding.progressMonting2.isVisible = false
        mToast = CustomSnackBarCustom()
        mSons = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        /**CLICK NO ITEM -->*/
        mAdapter = Disassambly2Adapter()
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
            Log.e("DESMONTAGEM", "NEW SCAN DESMONTAGEM --> $scanData")
            senData(scanData.toString())
            clearText()
            hideKeyExtensionActivity(mBinding.editMount2)
        }
    }

    private fun clearText() {
        mBinding.editMount2.apply {
            text?.clear()
            setText("")
            requestFocus()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        mProgress.dismiss()
    }
}