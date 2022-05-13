package com.documentos.wms_beirario.ui.picking.activitys

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityPicking1Binding
import com.documentos.wms_beirario.model.picking.PickingResponse1
import com.documentos.wms_beirario.model.picking.ResponsePickingReturnGroupedItem
import com.documentos.wms_beirario.repository.picking.PickingRepository
import com.documentos.wms_beirario.ui.picking.viewmodel.PickingViewModel1
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import java.util.*


class PickingActivity1 : AppCompatActivity(), Observer {

    private lateinit var mViewModel: PickingViewModel1
    private lateinit var mBinding: ActivityPicking1Binding
    private lateinit var mIntentData: PickingResponse1
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mediaSonsMp3: CustomMediaSonsMp3
    private lateinit var mArrayReturnGrounp: ArrayList<ResponsePickingReturnGroupedItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPicking1Binding.inflate(layoutInflater)
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
            this, PickingViewModel1.Picking1ViewModelFactory(
                PickingRepository()
            )
        )[PickingViewModel1::class.java]
    }

    private fun setupToolbar() {
        mBinding.toolbarPicking2.apply {
            subtitle = "[${getVersion()}]"
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
        mViewModel.mErrorAllShow.observe(this, { errorAll ->
            mAlert.alertMessageErrorSimples(this, errorAll)
        })
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
            mediaSonsMp3.somSucess(this)
        }
        mViewModel.mErrorReadingPickingShow.observe(this) { messageError ->
            mAlert.alertMessageErrorSimples(this, messageError)
        }

        mViewModel.mValidProgressEditShow.observe(this, { progressEdit ->
            mBinding.progressBarAddPicking2.isVisible = progressEdit
        })
    }

    private fun clickButton() {
        mBinding.buttonfinishpickin2.setOnClickListener {
            CustomMediaSonsMp3().somClick(this)
            val intent = Intent(this, PickingActivity2::class.java)
            intent.putExtra("DATA_PICK", mArrayReturnGrounp)
            Log.e("picking1", "clickButton --> $mArrayReturnGrounp ")
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
            Log.e("PICKING 1", "onNewIntent --> $scanData")
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
