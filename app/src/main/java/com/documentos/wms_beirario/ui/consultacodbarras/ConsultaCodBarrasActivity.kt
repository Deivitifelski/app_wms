package com.documentos.wms_beirario.ui.consultacodbarras

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityConsultaCodBarrasBinding
import com.documentos.wms_beirario.repository.consultacodbarras.ConsultaCodBarrasRepository
import com.documentos.wms_beirario.ui.consultacodbarras.fragments.EnderecoFragment
import com.documentos.wms_beirario.ui.consultacodbarras.fragments.ProdutoFragment
import com.documentos.wms_beirario.ui.consultacodbarras.fragments.VolumeFragment
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarras
import com.documentos.wms_beirario.utils.extensions.extensionVisibleProgress
import com.documentos.wms_beirario.utils.extensions.getVersion
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

class ConsultaCodBarrasActivity : AppCompatActivity(), Observer {

    private lateinit var mViewModel: ConsultaCodBarrasViewModel
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private var mIdArmazem: Int = 0
    private lateinit var mToken: String
    private lateinit var mBinding: ActivityConsultaCodBarrasBinding
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityConsultaCodBarrasBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initToolbar()
        initConst()
        initViewModel()
        setupObservables()
    }

    private fun initConst() {
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
        mSharedPreferences = CustomSharedPreferences(this)
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    override fun onResume() {
        super.onResume()
        initDataWead()
        UIUtil.hideKeyboard(this)
        extensionVisibleProgress(mBinding.progress, false)
        getShared()
        initData()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this, ConsultaCodBarrasViewModel.ConsultaCodBarrasVmfACTORY(
                ConsultaCodBarrasRepository()
            )
        )[ConsultaCodBarrasViewModel::class.java]
    }

    private fun initDataWead() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun initToolbar() {
        mBinding.apply {
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
            toolbar.subtitle = "COD.BARRAS [${getVersion()}]"
        }
    }


    private fun getShared() {
        mToken = mSharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        mIdArmazem = mSharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
    }


    private fun initData() {
        mBinding.editCodBarras.requestFocus()
        mBinding.editCodBarras.extensionSetOnEnterExtensionCodBarras {
            sendCod(mBinding.editCodBarras.text.toString())
        }
    }

    private fun sendCod(mQrCode: String) {
        try {
            if (mQrCode.isNotEmpty()) {
                pushQrCode(mQrCode)
                mBinding.editCodBarras.setText("")
                mBinding.editCodBarras.text?.clear()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun pushQrCode(qrCode: String) {
        UIUtil.hideKeyboard(this)
        mViewModel.getCodBarras(codigoBarras = qrCode)
        editFocus()
    }

    private fun editFocus() {
        mBinding.apply {
            editCodBarras.requestFocus()
            editCodBarras.setText("")
        }
    }

    private fun setupObservables() {
        mViewModel.mSucessShow.observe(this, { mDados ->
            mViewModel.checkBarCode(mDados)
        })

        mViewModel.mErrorShow.observe(this, { mErrorCodBarras ->
            mAlert.alertMessageErrorSimples(this, mErrorCodBarras, 2000)
        })
        /**RESPONSE DAS VALIDAÇOES DA LEITURA -->*/
        mViewModel.mResponseCheckEndereco.observe(this, { endereco ->
            val bundle = bundleOf("ENDERECO" to endereco)
            supportFragmentManager.commit {
                replace<EnderecoFragment>(R.id.fragment_container_view_cod_barras, args = bundle)
                setReorderingAllowed(true)
                /**addToBackStack ele estando acionado ele faz o desligamento da instancia do fragment*/
//                addToBackStack(null)
            }
        })
        mViewModel.mResponseCheckProduto.observe(this, { produto ->
            val bundle = bundleOf("PRODUTO" to produto)
            supportFragmentManager.commit {
                replace<ProdutoFragment>(R.id.fragment_container_view_cod_barras, args = bundle)
                setReorderingAllowed(true)
            }
        })
        mViewModel.mResponseCheckVolume.observe(this, { volume ->
            val bundle = bundleOf("VOLUME" to volume)
            supportFragmentManager.commit {
                replace<VolumeFragment>(R.id.fragment_container_view_cod_barras, args = bundle)
                setReorderingAllowed(true)
            }
        })

        mViewModel.mProgressShow.observe(this, { progress ->
            mBinding.progress.isVisible = progress
        })

        mViewModel.mErrorAllShow.observe(this, { errorAll ->
            mAlert.alertMessageErrorSimples(this, errorAll, 2000)
        })
    }


    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            Log.e("CODIGO DE BARRAS", "onNewIntent --> $scanData ")
            sendCod(scanData!!)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

}
