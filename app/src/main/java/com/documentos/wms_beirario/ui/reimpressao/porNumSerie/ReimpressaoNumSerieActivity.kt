package com.documentos.wms_beirario.ui.reimpressao.porNumSerie

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityReimpressaoNumSerieBinding
import com.documentos.wms_beirario.repository.reimpressao.ReimpressaoRepository
import com.documentos.wms_beirario.ui.reimpressao.dialogFragment.DialogReimpressaoDefault
import com.documentos.wms_beirario.ui.reimpressao.dialogFragment.adapterDefault.AdapterReimpressaoNumSerieReanding
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

class ReimpressaoNumSerieActivity : AppCompatActivity(), Observer {

    private val TAG = "ReimpressaoNumSerieActivity"
    private lateinit var mAdapter: AdapterReimpressaoNumSerieReanding
    private lateinit var mBinding: ActivityReimpressaoNumSerieBinding
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mViewModel: ReimpressaoNumSerieViewModel
    private lateinit var mDialog: Dialog
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
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
        mBinding = ActivityReimpressaoNumSerieBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        setupToolbar()
        initConst()
        initRv()
        setupDataWedge()
        initViewModel()
        setObservables()
        setupEdit()
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

    private fun initConst() {

        sharedPreferences = CustomSharedPreferences(this)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        mAdapter = AdapterReimpressaoNumSerieReanding { itemClick ->
            mIdTarefa = itemClick.idTarefa
            mNumeroSerie = itemClick.numeroSerie
            mSequencialTarefa = itemClick.sequencialTarefa
            mIdInventarioAbastecimentoItem = itemClick.idInventarioAbastecimentoItem
            mIdOrdemMontagemVolume = itemClick.idOrdemMontagemVolume
            mViewModel.getZpls(itemClick, idArmazem, token)
        }
        mDialog = CustomAlertDialogCustom().progress(this)
        mDialog.hide()
        mBinding.editQrcodeNumserie.requestFocus()
        mToast = CustomSnackBarCustom()
        mAlert = CustomAlertDialogCustom()
    }

    private fun setupToolbar() {
        mBinding.toolbar5.apply {
            subtitle = getVersionNameToolbar()
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            ReimpressaoNumSerieViewModel.ReimpressaoNumSerieViewModelFactory(ReimpressaoRepository())
        )[ReimpressaoNumSerieViewModel::class.java]
    }

    private fun initRv() {
        mBinding.rvReimpressaonumserie.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@ReimpressaoNumSerieActivity)
        }
    }

    private fun setObservables() {
        mViewModel.mSucessShow.observe(this) { sucess ->
            clearEdit()
            mDialog.hide()
            if (sucess.isEmpty()) {
                mAlert.alertMessageErrorSimples(
                    this,
                    getString(R.string.reimpressao_information)
                )
            } else {
                UIUtil.hideKeyboard(this)
                mAdapter.submitList(sucess)
            }

        }
        mViewModel.mErrorAllShow.observe(this) { error ->
            mDialog.hide()
            mAlert.alertMessageErrorSimples(this, error)
        }

        mViewModel.mErrorHttpShow.observe(this) { error ->
            mDialog.hide()
            mAlert.alertMessageErrorSimples(this, error)
        }

        mViewModel.mSucessZplsShows.observe(this) { sucessZpl ->
            try {
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


    private fun setupDataWedge() {
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
    }

    private fun setupEdit() {
        mBinding.editQrcodeNumserie.extensionSetOnEnterExtensionCodBarras {
            sendData(mBinding.editQrcodeNumserie.text.toString().trim())
            clearEdit()
        }
    }

    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            Log.e("REIMPRESSAO NUM SERIE", "onNewIntent --> $scanData")
            sendData(scanData!!)
            clearEdit()
        }
    }

    private fun sendData(scanData: String) {
        try {
            if (scanData.isNotEmpty()) {
                mDialog.show()
                mViewModel.getNumSerie(scanData, idArmazem, token)
                clearEdit()
            } else {
                mError("Campo Vazio!")
            }
        } catch (e: Exception) {
            clearEdit()
            mError(e.toString())
        }
    }

    private fun clearEdit() {
        mBinding.editQrcodeNumserie.setText("")
        mBinding.editQrcodeNumserie.text!!.clear()
    }

    private fun mError(msg: String) {
        vibrateExtension(500)
        mToast.toastCustomError(this, msg)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        mDialog.dismiss()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}