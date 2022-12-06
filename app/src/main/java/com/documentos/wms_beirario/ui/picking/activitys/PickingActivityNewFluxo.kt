package com.documentos.wms_beirario.ui.picking.activitys

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityPickingNewFluxoBinding
import com.documentos.wms_beirario.model.picking.PickingResponseNewFluxo
import com.documentos.wms_beirario.model.picking.ResponsePickingReturnGroupedItem
import com.documentos.wms_beirario.repository.picking.PickingRepository
import com.documentos.wms_beirario.ui.picking.viewmodel.PickingViewModelNewFluxo
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import java.util.*


class PickingActivityNewFluxo : AppCompatActivity(), Observer {

    private lateinit var mViewModel: PickingViewModelNewFluxo
    private lateinit var mBinding: ActivityPickingNewFluxoBinding
    private lateinit var mIntentData: PickingResponseNewFluxo
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mediaSonsMp3: CustomMediaSonsMp3
    private lateinit var mArrayReturnGrounp: ArrayList<ResponsePickingReturnGroupedItem>
    private lateinit var mSharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPickingNewFluxoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initViewModel()
        setupRecyclerView()
        setupToolbar()
        setupObservables()
        setupObservablesRead()
        readItem()
        clickButton()
        setupDataWedge()
        mViewModel.getItensPicking2()
        extensionVisibleProgress(mBinding.progressBarAddPicking2, false)
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
            this, PickingViewModelNewFluxo.Picking1ViewModelFactory(
                PickingRepository()
            )
        )[PickingViewModelNewFluxo::class.java]
    }

    private fun setupToolbar() {
        mSharedPreferences = CustomSharedPreferences(this)
        val name = mSharedPreferences.getString(CustomSharedPreferences.NAME_USER) ?: ""
        mBinding.toolbarPicking2.apply {
            subtitle = getVersionNameToolbar()
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }


    private fun setupRecyclerView() {
        mediaSonsMp3 = CustomMediaSonsMp3()
        mToast = CustomSnackBarCustom()
        mAlert = CustomAlertDialogCustom()
    }

    private fun setupObservables() {
        /**Validar button enable*/
        mViewModel.mSucessPickingReturnShows.observe(this) { returnGrounp ->
            mArrayReturnGrounp = returnGrounp
            mBinding.buttonfinishpickin2.isEnabled = !returnGrounp.isEmpty()
        }
        mViewModel.mErrorPickingShow.observe(this) { messageError ->
            mAlert.alertMessageErrorSimples(this, messageError)
        }
        mViewModel.mValidProgressInitShow.observe(this) { progress ->
            mBinding.progressBarInitPicking2.isVisible = progress
        }
        mViewModel.mErrorAllShow.observe(this) { errorAll ->
            mAlert.alertMessageErrorSimples(this, errorAll)
        }
    }


    //-----------------------------------------LEITURA------------------------------------------------>
    private fun readItem() {
        hideKeyExtensionActivity(mBinding.editPicking2)
        mBinding.editPicking2.extensionSetOnEnterExtensionCodBarras {
            sendReading(mBinding.editPicking2.text.toString())
        }
    }

    private fun sendReading(qrCode: String) {
        if (qrCode != "") {
            mViewModel.reandingData(qrCode)
            clearText()
        }
    }

    private fun clearText() {
        mBinding.editPicking2.setText("")
        mBinding.editPicking2.text!!.clear()
        mBinding.editPicking2.requestFocus()
    }

    /**LEITURA -->*/
    private fun setupObservablesRead() {
        mViewModel.mSucessPickingReadShow.observe(this) {
            mToast.toastCustomSucess(this, "Inserido!")
            mediaSonsMp3.somSucess(this)
            mViewModel.getItensPicking2()
            clearText()
        }
        mViewModel.mErrorReadingPickingShow.observe(this) { messageError ->
            mAlert.alertMessageErrorSimples(this, messageError)
            clearText()
        }

        mViewModel.mValidProgressEditShow.observe(this) { progressEdit ->
            mBinding.progressBarAddPicking2.isVisible = progressEdit
        }
    }

    private fun clickButton() {
        mBinding.buttonfinishpickin2.setOnClickListener {
            CustomMediaSonsMp3().somClick(this)
            val intent = Intent(this, PickingActivityFinish::class.java)
            startActivity(intent)
            extensionSendActivityanimation()

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
            Log.e("PICKING NEW FLUXO", "Recebido onNewIntent --> $scanData")
            sendReading(scanData.toString())
            clearText()
        }
    }

    override fun onBackPressed() {
        finish()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
