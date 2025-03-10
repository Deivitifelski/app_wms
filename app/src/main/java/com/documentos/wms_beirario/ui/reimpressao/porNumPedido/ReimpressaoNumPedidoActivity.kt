package com.documentos.wms_beirario.ui.reimpressao.porNumPedido

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityReimpressaoNumPedidoBinding
import com.documentos.wms_beirario.repository.reimpressao.ReimpressaoRepository
import com.documentos.wms_beirario.ui.reimpressao.dialogFragment.DialogReimpressaoDefault
import com.documentos.wms_beirario.ui.reimpressao.dialogFragment.adapterDefault.AdapterReimpressaoDefaultReanding
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarras
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.Observable
import java.util.Observer

class ReimpressaoNumPedidoActivity : AppCompatActivity(), Observer {

    private val TAG = "ReimpressaoNumPedidoActivity"
    private lateinit var mBinding: ActivityReimpressaoNumPedidoBinding
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mAdapter: AdapterReimpressaoDefaultReanding
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mDialog: Dialog
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private lateinit var mViewModel: ReimpressaoPorPedidoiewModel
    private var initialized = false
    private var mIdTarefa: String? = null
    private var mSequencialTarefa: Int? = null
    private var mNumeroSerie: String? = null
    private var mIdInventarioAbastecimentoItem: String? = null
    private var mIdOrdemMontagemVolume: String? = null
    private lateinit var token: String
    private var idArmazem: Int = 0
    private lateinit var sharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityReimpressaoNumPedidoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        setupToolbar()
        initConst()
        initViewModel()
        setupDataWedge()
        setObservables()
        setupEdit()
        initRv()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            ReimpressaoPorPedidoiewModel.ReimpressaoPedidoViewModelFactory(ReimpressaoRepository())
        )[ReimpressaoPorPedidoiewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    override fun onRestart() {
        super.onRestart()
        mDialog.hide()
    }

    private fun setupToolbar() {
        mBinding.toolbar5.apply {
            subtitle = getVersionNameToolbar()
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun initRv() {
        mBinding.rvNumPedido.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@ReimpressaoNumPedidoActivity)
        }
    }

    private fun setupEdit() {
        mBinding.editQrcodeRequest.extensionSetOnEnterExtensionCodBarras {
            if (mBinding.editQrcodeRequest.text.toString().isNotEmpty()) {
                sendData(mBinding.editQrcodeRequest.text.toString())
                clearEdit()
            } else {
                mErrorToast("Campo Vazio!")
            }
        }
    }

    private fun sendData(scanData: String) {
        try {
            mDialog.show()
            mViewModel.getNumPedido(scanData, idArmazem, token)
            clearEdit()
        } catch (e: Exception) {
            clearEdit()
            mErrorToast(e.toString())
        }
    }


    private fun initConst() {
        mDialog = CustomAlertDialogCustom().progress(this)
        mDialog.hide()
        mAdapter = AdapterReimpressaoDefaultReanding { itemClick ->
            mDialog.show()
            lifecycleScope.launch {
                mIdTarefa = itemClick.idTarefa
                mNumeroSerie = itemClick.numeroSerie
                mSequencialTarefa = itemClick.sequencialTarefa
                mIdInventarioAbastecimentoItem = itemClick.idInventarioAbastecimentoItem
                mIdOrdemMontagemVolume = itemClick.idOrdemMontagemVolume
                mViewModel.getZpls(itemClick, idArmazem, token)
            }
        }
        mBinding.editQrcodeRequest.requestFocus()
        mToast = CustomSnackBarCustom()
        mAlert = CustomAlertDialogCustom()
        sharedPreferences = CustomSharedPreferences(this)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)

    }

    private fun setupDataWedge() {
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
    }

    private fun mErrorToast(msg: String) {
        vibrateExtension(500)
        mToast.toastCustomError(this, msg)
    }

    private fun setObservables() {
        mViewModel.mSucessShow.observe(this) { sucess ->
            mDialog.hide()
            clearEdit()
            if (sucess.isEmpty()) {
                mAlert.alertMessageErrorSimples(
                    this,
                    getString(R.string.reimpressao_information),
                    2000
                )
            } else {
                UIUtil.hideKeyboard(this)
                mAdapter.submitList(sucess)
            }
        }
        mViewModel.mErrorAllShow.observe(this) { error ->
            mDialog.hide()
            mAlert.alertMessageErrorSimples(this, error, 2000)
        }

        mViewModel.mErrorHttpShow.observe(this) { error ->
            mDialog.hide()
            mAlert.alertMessageErrorSimples(this, error, 2000)
        }

        mViewModel.mSucessZplsShows.observe(this) { sucessZpl ->
            try {
                mDialog.hide()
                DialogReimpressaoDefault(
                    sucessZpl,
                    mIdTarefa,
                    mSequencialTarefa,
                    mNumeroSerie,
                    mIdInventarioAbastecimentoItem,
                    mIdOrdemMontagemVolume,
                    idArmazem,
                    token
                ).show(
                    supportFragmentManager,
                    "DIALOG_REIMPRESSAO"
                )
            } catch (e: Exception) {
                mError("Erro ao criar tela de reimpressoes!")
            }
        }
    }

    private fun mError(msg: String) {
        vibrateExtension(500)
        mToast.toastCustomError(this, msg)
    }

    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            Log.e("REIMPRESSAO REQUISIÇAO", "onNewIntent --> $scanData")
            sendData(scanData!!)
            clearEdit()
        }
    }

    private fun clearEdit() {
        mBinding.editQrcodeRequest.setText("")
        mBinding.editQrcodeRequest.text!!.clear()
        mBinding.editQrcodeRequest.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        mDialog.dismiss()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
        mDialog.dismiss()
    }
}