package com.documentos.wms_beirario.ui.consultacodbarras

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.databinding.ActivityConsultaCodBarrasBinding
import com.documentos.wms_beirario.ui.consultacodbarras.fragments.EnderecoFragment
import com.documentos.wms_beirario.ui.consultacodbarras.fragments.ProdutoFragment
import com.documentos.wms_beirario.ui.consultacodbarras.fragments.VolumeFragment
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.example.br_coletores.viewModels.scanner.ObservableObject
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class ConsultaCodBarrasActivity : AppCompatActivity(),Observer {

    private val mViewModel: ConsultaCodBarrasViewModel by viewModel()
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private var mIdArmazem: Int = 0
    private lateinit var mToken: String
    private lateinit var mBinding: ActivityConsultaCodBarrasBinding
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityConsultaCodBarrasBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mSharedPreferences = CustomSharedPreferences(this)
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        intentFilter.addCategory(DWInterface.DATAWEDGE_SEND_ACTION)
        registerReceiver(receiver, intentFilter)
    }

    override fun onResume() {
        super.onResume()
        initData()
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
        UIUtil.hideKeyboard(this)
        mViewModel.visibilityProgress(mBinding.progress, visibility = false)
        initToolbar()
        getShared()
        setupObservables()

    }

    private fun initToolbar() {
        mBinding.apply {
            toolbar.setNavigationOnClickListener {
                onBackPressed()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }
    }


    private fun getShared() {
        mToken = mSharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        mIdArmazem = mSharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
    }


    private fun initData() {
        mBinding.editCodBarras.requestFocus()
        mBinding.editCodBarras.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == 10036 || keyCode == 103 || keyCode == 102) && event.action == KeyEvent.ACTION_UP) {
                    sendReading(mBinding.editCodBarras.text.toString())
                return@OnKeyListener true
            }
            false
        })
    }

    private fun sendReading(toString: String) {
        UIUtil.hideKeyboard(this)
        AppExtensions.visibilityProgressBar(mBinding.progress, visibility = true)
        mViewModel.getCodBarras(codigoBarras = toString)
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
            AppExtensions.visibilityProgressBar(mBinding.progress, visibility = false)
            mViewModel.checkBarCode(mDados)
        })

        mViewModel.mErrorShow.observe(this, { mErrorCodBarras ->
            mViewModel.visibilityProgress(mBinding.progress, visibility = false)
            CustomAlertDialogCustom().alertMessageErrorSimples(this, mErrorCodBarras)
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
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (intent.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            mBinding.editCodBarras.setText("")
            mBinding.editCodBarras.text!!.clear()
            sendReading(scanData!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun update(o: Observable?, arg: Any?) {

    }

}


