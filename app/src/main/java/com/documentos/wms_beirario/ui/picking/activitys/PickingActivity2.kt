package com.documentos.wms_beirario.ui.picking.activitys

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityPicking2Binding
import com.documentos.wms_beirario.model.picking.PickingRequest1
import com.documentos.wms_beirario.repository.picking.PickingRepository
import com.documentos.wms_beirario.ui.picking.adapters.PickingAdapter2
import com.documentos.wms_beirario.ui.picking.viewmodel.PickingViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarras
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import java.util.*


class PickingActivity2 : AppCompatActivity(), Observer {

    private lateinit var mBinding: ActivityPicking2Binding
    private lateinit var mAdapter: PickingAdapter2
    private var mIdArea: Int = 0
    private var mNameArea: String = ""
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mediaSonsMp3: CustomMediaSonsMp3
    private lateinit var mViewModel: PickingViewModel2
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityPicking2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        getIntentExtas()
        initToolbar()
        initViewModel()
        initConst()
        initObserver()
        lerItem()
        finalizarPicking()
        setupDataWedge()

    }

    private fun getIntentExtas() {
        try {
            if (intent.extras != null)
                mIdArea = intent.getIntExtra(ID_AREA, 0)
            mNameArea = intent.getStringExtra("NAME_AREA").toString()
        } catch (e: Exception) {
            mErrorToast(e.toString())
        }
    }

    private fun initConst() {
        mBinding.editPicking2.requestFocus()
        mToast = CustomSnackBarCustom()
        mAlert = CustomAlertDialogCustom()
        mediaSonsMp3 = CustomMediaSonsMp3()
        mBinding.buttonfinalizarpickin2.isEnabled = false
        mBinding.progressBarInitPicking2.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
        initRecyclerView()
        initGetData()
        validadButton()
    }

    private fun initRecyclerView() {
        mAdapter = PickingAdapter2()
        mBinding.rvPicking2.apply {
            layoutManager = LinearLayoutManager(this@PickingActivity2)
            adapter = mAdapter
        }
    }

    private fun initToolbar() {
        mBinding.toolbarPicking2.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            title = "PICKING | $mNameArea"
            subtitle = getVersionNameToolbar()
        }
    }

    private fun validadButton() {
        mViewModel.getItensPickingFinishValidadButton()
    }

    private fun initGetData() {
        mViewModel.getItensPicking2(mIdArea)
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            PickingViewModel2.Picking2ViewModelFactory(PickingRepository())
        )[PickingViewModel2::class.java]
    }

    private fun lerItem() {
        mBinding.editPicking2.extensionSetOnEnterExtensionCodBarras {
            if (mBinding.editPicking2.toString() != "") {
                sendData(mBinding.editPicking2.text.toString().trim())
                clearEdit()
            } else {
                mErrorToast(getString(R.string.edit_emply))
            }
        }
    }

    private fun clearEdit() {
        mBinding.editPicking2.setText("")
        mBinding.editPicking2.text?.clear()
        mBinding.editPicking2.requestFocus()
    }

    private fun sendData(scan: String) {
        mViewModel.getItensPickingReanding2(
            idArea = mIdArea,
            pickingRepository = PickingRequest1(scan)
        )
        clearEdit()
    }

    private fun initObserver() {
        /**RESPOSTAS DO PRIMEIRO GET PARA TRAZER TAREFAS DAS AREAS -->*/
        mViewModel.mSucessPickingReturnShows.observe(this) { list ->
            if (list.isEmpty()) {
                mBinding.txt.visibility = View.GONE
                mBinding.txtInformativoPicking2.isVisible = true
            } else {
                mBinding.txt.visibility = View.VISIBLE
                mBinding.txtInformativoPicking2.isVisible = false
                mAdapter.update(list)
            }
        }
        mViewModel.mErrorAllShow.observe(this) { errorAll ->
            mAlert.alertMessageErrorSimples(this, errorAll)
        }
        mViewModel.mErrorPickingShow.observe(this) { errorGetPicking ->
            mAlert.alertMessageErrorSimples(this, errorGetPicking)
        }
        mViewModel.mValidProgressInitShow.observe(this) { progressInit ->
            if (progressInit)
                mBinding.progressBarInitPicking2.visibility = View.VISIBLE
            else mBinding.progressBarInitPicking2.visibility = View.GONE
        }
        /**RESPOSTAS DA LEITURA -->*/
        mViewModel.mSucessPickingReadShow.observe(this) {
            mediaSonsMp3.somSucess(this)
            initGetData()
            initRecyclerView()
            vibrateExtension(500)
        }

        mViewModel.mErrorReadingPickingShow.observe(this) { erroReanding ->
            mAlert.alertMessageErrorSimples(this, erroReanding)
        }

        /**FAZ O GET DA TELA FINAL PARA VER SE CONTEM ITENS PARA HABILITAR O BUTTON-->*/
        mViewModel.mSucessShow.observe(this) { sucessValidaButton ->
            mBinding.buttonfinalizarpickin2.isEnabled = sucessValidaButton.isNotEmpty()

        }

        mViewModel.mErrorShow.observe(this) { sucessValidaButton ->
            mToast.toastCustomError(this, "Erro ao validar button!\n$sucessValidaButton")
        }
    }


    /**BUTTON FINALIZAR PICKING -->*/
    private fun finalizarPicking() {
        mBinding.buttonfinalizarpickin2.setOnClickListener {
            val intent = Intent(this, PickingActivityFinish::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun mErrorToast(msg: String) {
        mToast.toastCustomError(this, msg)
        vibrateExtension(500)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
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
            sendData(scanData.toString())
            Log.e("PICKING 2", "Recebido onNewIntent --> ${scanData.toString()}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    companion object {
        const val ID_AREA = "id_area_picking_1"
    }
}