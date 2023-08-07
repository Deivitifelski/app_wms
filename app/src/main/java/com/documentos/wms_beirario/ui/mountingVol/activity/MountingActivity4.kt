package com.documentos.wms_beirario.ui.mountingVol.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityMounting4Binding
import com.documentos.wms_beirario.databinding.LayoutAlertSucessCustomBinding
import com.documentos.wms_beirario.model.mountingVol.RequestMounting5
import com.documentos.wms_beirario.model.mountingVol.ResponseAndressMonting3Item
import com.documentos.wms_beirario.model.mountingVol.ResponseMounting2Item
import com.documentos.wms_beirario.repository.mountingvol.MountingVolRepository
import com.documentos.wms_beirario.ui.mountingVol.adapters.AdapterMountingProd4
import com.documentos.wms_beirario.ui.mountingVol.viewmodels.MountingVolViewModel4
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

class MountingActivity4 : AppCompatActivity(), Observer {

    private lateinit var mBinding: ActivityMounting4Binding
    private lateinit var mProgress: Dialog
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mViewModel: MountingVolViewModel4
    private lateinit var mIntenResponse3: ResponseAndressMonting3Item
    private lateinit var mIntenResponse2: ResponseMounting2Item
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAdapter: AdapterMountingProd4
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var token: String
    private var idArmazem: Int = 0
    private lateinit var sharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMounting4Binding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initCons()
        setToolbar()
        clickEditHideKey()
        setupDataWedge()
        initViewModel()
        setObserver()
        setEdit()
    }

    override fun onResume() {
        super.onResume()
        initDataWedge()
        setupRecyclerView()
        callApi()
    }

    private fun clickEditHideKey() {
        hideKeyExtensionActivity(mBinding.editMounting4)
        mBinding.editMounting4.setOnClickListener {
            showKeyExtensionActivity(mBinding.editMounting4)
        }
    }

    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun setEdit() {
        mBinding.editMounting4.extensionSetOnEnterExtensionCodBarras {
            sendData(mBinding.editMounting4.text.toString())
        }
    }

    private fun sendData(scan: String) {
        try {
            if (scan.isNullOrEmpty()) {
                mBinding.editLayoutMounting4.shake {
                    mErroToastExtension(this, "Preencha o campo!")
                }
            } else {
                val qrCode = mAdapter.searchItem(scan)
                if (qrCode != null) {
                    val getBody = RequestMounting5(
                        mIntenResponse3.idEnderecoOrigem,
                        mIntenResponse2.idOrdemMontagemVolume,
                        qrCode.idProduto
                    )
                    mViewModel.addProdEan5(body = getBody, idArmazem, token)
                } else {
                    mAlert.alertMessageErrorSimples(this, "Leia um EAN válido!")
                }
            }
        } catch (e: Exception) {
            mToast.toastCustomError(this, "Erro inesperado!\n$e")
        } finally {
            clearEdit()
        }
    }

    private fun setObserver() {
        mViewModel.apply {
            mShowShow.observe(this@MountingActivity4) { sucess ->
                if (sucess.isNotEmpty()) {
                    sucess.forEach {
                        Log.e("mounting4", "EAN --> ${it.EAN} ")
                    }
                    mAdapter.submitList(sucess)
                } else {
                    alertMessageSucess("Todos produtos finalizados!")
                }
            }
            mErrorShow.observe(this@MountingActivity4) { error ->
                mAlert.alertMessageErrorSimples(this@MountingActivity4, error)
            }
            mValidaProgressShow.observe(this@MountingActivity4) { progress ->
                if (progress) {
                    mProgress.show()
                } else {
                    mProgress.hide()
                }
            }
            /**
             * RESPOSTA DA BIPAGEM SUCESSO -->
             */
            mShowShow5.observe(this@MountingActivity4) { sucessAdd5 ->
                mSonsMp3.somSucess(this@MountingActivity4)
                setupRecyclerView()
                callApi()
            }
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
            MountingVolViewModel4.Mounting4ViewModelFactory(MountingVolRepository())
        )[MountingVolViewModel4::class.java]
    }

    private fun setToolbar() {
        mBinding.toolbarMounting4.apply {
            title = mIntenResponse3.enderecoVisual
            subtitle = getVersionNameToolbar()
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun callApi() {
        mViewModel.getProd(
            mIntenResponse2.idOrdemMontagemVolume,
            mIntenResponse3.idEnderecoOrigem.toString(),
            idArmazem,
            token
        )
    }

    private fun initCons() {
        sharedPreferences = CustomSharedPreferences(this)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        mBinding.editMounting4.requestFocus()
        try {
            if (intent.extras != null) {
                val responseAndressMonting3Item =
                    intent.getSerializableExtra("DATA_MOUNTING3") as ResponseAndressMonting3Item
                val responseMounting2Item =
                    intent.getSerializableExtra("DATA_MOUNTING2") as ResponseMounting2Item
                mIntenResponse3 = responseAndressMonting3Item
                mIntenResponse2 = responseMounting2Item
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
        mAdapter = AdapterMountingProd4()
        mBinding.apply {
            rvMounting4.apply {
                layoutManager = LinearLayoutManager(this@MountingActivity4)
                adapter = mAdapter
            }
        }
    }

    private fun clearEdit() {
        mBinding.editMounting4.apply {
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

    /**
     * MODAL QUANDO FINALIZOU TODOS OS ITENS -->
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