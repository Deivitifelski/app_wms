package com.documentos.wms_beirario.ui.unmountingVolumes.activity

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityUnMountingVolumes2Binding
import com.documentos.wms_beirario.databinding.LayoutCustomFinishMovementAdressBinding
import com.documentos.wms_beirario.model.desmontagemVol.RequestDisassamblyVol
import com.documentos.wms_beirario.model.desmontagemVol.ResponseUnmonting2Item
import com.documentos.wms_beirario.model.desmontagemVol.UnmountingVolumes1Item
import com.documentos.wms_beirario.repository.desmontagemvolumes.DisassemblyRepository
import com.documentos.wms_beirario.ui.unmountingVolumes.adapter.Disassambly2Adapter
import com.documentos.wms_beirario.ui.unmountingVolumes.viewModel.ViewModelInmounting2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarras
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import java.util.*

class UnMountingVolumesActivity2 : AppCompatActivity(), Observer {

    private lateinit var mBinding: ActivityUnMountingVolumes2Binding
    private lateinit var mAdapter: Disassambly2Adapter
    private lateinit var mToast: CustomSnackBarCustom
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
    }

    override fun onResume() {
        super.onResume()
        mProgress.hide()
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun setupToolbar() {
        mBinding.unMonting2.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initIntent() {
        try {
            val mGetIntent: Intent = intent
            if (mGetIntent.extras != null) {
                val data = mGetIntent.getSerializableExtra("ITEM_CLICK_1") as UnmountingVolumes1Item
                mIntent = data
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

    private fun initAdapter() {
        mBinding.rvUnMonting2.apply {
            layoutManager = LinearLayoutManager(this@UnMountingVolumesActivity2)
            adapter = mAdapter
        }
    }

    private fun setObservables() {
        mViewModel.mProgressShow.observe(this) { progress ->
            if (progress) mProgress.show() else mProgress.hide()
//            mBinding.progressMonting2.isVisible = progress
        }

        mViewModel.mSucessShow.observe(this) { listSucess ->
            try {
                mAdapter.update(listSucess)
            } catch (e: Exception) {
                mErrorToast("Erro ao receber dados da API:\n${e.cause}")
            }
        }

        mViewModel.mErrorAllShow.observe(this) { error ->
            mErrorToast(error)
        }
        mViewModel.mErrorHttpShow.observe(this) { error ->
            mErrorToast(error)
        }

        //SUCESS LETURA -->
        mViewModel.mSucessReandingFinishShow.observe(this) {
            try {
                mBinding.progressMonting2.isVisible = true
                initAdapter()
                initData()
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
        mAlert = CustomAlertDialogCustom()
        /**CLICK NO ITEM -->*/
        mAdapter = Disassambly2Adapter { itemClick ->
            alertUnMountingVolFinish(itemClick = itemClick)
        }
    }

    /**VALIDAR LEITURA !!!!!!!!!!!!!!!!!!!!!!!!!!!-->*/
    private fun alertUnMountingVolFinish(itemClick: ResponseUnmonting2Item) {
        CustomMediaSonsMp3().somAlerta(this)
        mAlertFinish = AlertDialog.Builder(this)
        val mBindingAlert = LayoutCustomFinishMovementAdressBinding.inflate(layoutInflater)
        mAlertFinish.setView(mBindingAlert.root)
        val mShow = mAlertFinish.show()
        mBindingAlert.editQrcodeCustom.requestFocus()
        mBindingAlert.txtInf.text =
            "Destino para: ${itemClick.idProduto} - ${itemClick.quantidadeVolumes}"
        //Recebendo a leitura Coletor Finalizar Tarefa -->
        mBindingAlert.progressEdit.visibility = View.INVISIBLE
        mBindingAlert.editQrcodeCustom.extensionSetOnEnterExtensionCodBarras {
            val qrcode = mBindingAlert.editQrcodeCustom.text.toString()
            if (qrcode.isNotEmpty()) {
                mBindingAlert.progressEdit.visibility = View.VISIBLE
                sendReadingAlertDialog(itemClick, qrcode.trim())
                mShow.dismiss()
            } else {
                vibrateExtension(500)
                CustomSnackBarCustom().toastCustomError(this, "Campo Vazio!")
            }
            mBindingAlert.progressEdit.visibility = View.INVISIBLE
        }
        mAlertFinish.setOnDismissListener { it.dismiss() }
        mBindingAlert.buttonCancelCustom.setOnClickListener {
            mBindingAlert.progressEdit.visibility = View.INVISIBLE
            CustomMediaSonsMp3().somClick(this)
            mShow.dismiss()
        }
    }

    private fun sendReadingAlertDialog(itemClick: ResponseUnmonting2Item, scanData: String) {
        mViewModel.postReanding(
            body = RequestDisassamblyVol(
                idEndereco = mIntent.idEndereco,
                numeroSerie = scanData
            )
        )
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
            if (mAlertFinish.create().isShowing) {
                readingAndress(scanData.toString())
            } else {
                Toast.makeText(this, "Clique em um item para finalizar!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readingAndress(scanData: String) {
        mViewModel.postReanding(
            body = RequestDisassamblyVol(
                idEndereco = mIntent.idEndereco,
                numeroSerie = scanData
            )
        )
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