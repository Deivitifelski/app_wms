package com.documentos.wms_beirario.ui.armazenagem

import ArmazenagemAdapter
import ArmazenagemResponse
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityArmazenagemBinding
import com.documentos.wms_beirario.repository.armazenagem.ArmazenagemRepository
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarras
import com.documentos.wms_beirario.utils.extensions.getVersion
import java.util.*

class ArmazenagemActivity : AppCompatActivity(), Observer {

    private lateinit var mBinding: ActivityArmazenagemBinding
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mViewModel: ArmazenagemViewModel
    private lateinit var mAdapter: ArmazenagemAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArmazenagemBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setupDataWedge()
        initConst()
        initToolbar()
        initViewModel()
        setRecyclerView()
        setObservables()
        initScan()
        initSwipe()
    }

    override fun onResume() {
        super.onResume()
        initData()
        setRecyclerView()
        initDataWedge()
    }

    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    /**INIT VIEWMODEL -->*/
    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this, ArmazenagemViewModel.ArmazenagemViewModelFactory(
                ArmazenagemRepository()
            )
        )[ArmazenagemViewModel::class.java]
    }

    /**CHAMADA VIEWMODEL -->*/
    private fun initData() {
        mViewModel.getArmazenagem()
    }

    private fun initScan() {
        mBinding.editTxtArmazem01.extensionSetOnEnterExtensionCodBarras {
            readingAndress(mBinding.editTxtArmazem01.text.toString())
        }
    }

    private fun initSwipe() {
        val swipe = mBinding.swipeMov1
        swipe.setColorSchemeColors(getColor(R.color.color_default))
        swipe.setOnRefreshListener {
            mBinding.progressBarInitArmazenagem1.isVisible = true
            mBinding.linearInf.isVisible = false
            setRecyclerView()
            initData()
            mBinding.imageLottieArmazenagem1.playAnimation()
            swipe.isRefreshing = false
        }
    }

    private fun readingAndress(qrCode: String) {
        val qrcodeLido = mAdapter.procurarDestino(qrCode)
        if (qrcodeLido == null) {
            mBinding.progressBarEditArmazenagem1.isVisible = true
            Handler(Looper.getMainLooper()).postDelayed({
                mAlert.alertMessageErrorCancelFalse(
                    this,
                    "Leia um endereço válido!", 2000
                )
                mBinding.progressBarEditArmazenagem1.isVisible = false
                clearEdit()
            }, 600)
        } else {
            abrirArmazem2(qrcodeLido)
        }
    }

    private fun abrirArmazem2(mQrcodeLido: ArmazenagemResponse) {
        val intent = Intent(this, ArmazenagemActivity2::class.java)
        intent.putExtra("ARMAZEM_SEND", mQrcodeLido)
        startActivity(intent)
        extensionSendActivityanimation()
    }

    private fun clearEdit() {
        mBinding.editTxtArmazem01.setText("")
        mBinding.editTxtArmazem01.requestFocus()
    }

    /**RESPONSE VIEWMODEL -->*/
    private fun setObservables() {
        mViewModel.mSucessShow.observe(this) { response ->
            if (response.isEmpty()) {
                mBinding.linearInf.isVisible = false
                mBinding.imageLottieArmazenagem1.visibility = View.VISIBLE
                mBinding.txtArmazem.visibility = View.VISIBLE
            } else {
                mBinding.linearInf.isVisible = true
                mBinding.txtArmazem.visibility = View.INVISIBLE
                mBinding.imageLottieArmazenagem1.visibility = View.INVISIBLE
                mAdapter.update(response)
            }
        }

        mViewModel.mErrorHttpShow.observe(this) { error ->
            if (error.contains("n?o tem permiss?o para esse tipo de tarefa")) {
                mAlert.alertErroInitBack(applicationContext, this, error.toString())
            } else {
                mAlert.alertMessageErrorSimples(this, error, 2000)
            }
        }

        mViewModel.mErrorAllShow.observe(this) { error ->
            mAlert.alertMessageErrorSimples(this, error, 2000)
        }

        mViewModel.mProgressShow.observe(this) { progress ->
            mBinding.progressBarInitArmazenagem1.isVisible = progress
        }

    }

    private fun initToolbar() {
        mBinding.toolbarArmazenagem1.setNavigationOnClickListener {
            onBackPressed()
        }
        mBinding.toolbarArmazenagem1.subtitle = "[${getVersion()}]"
    }

    private fun initConst() {
        mBinding.linearInf.isVisible = false
        mBinding.editTxtArmazem01.requestFocus()
        mBinding.txtArmazem.isVisible = false
        mBinding.progressBarEditArmazenagem1.isVisible = false
        mBinding.imageLottieArmazenagem1.isVisible = false
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    private fun setRecyclerView() {
        mAdapter = ArmazenagemAdapter()
        mBinding.rvArmazenagem.apply {
            layoutManager = LinearLayoutManager(this@ArmazenagemActivity)
            adapter = mAdapter
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
            readingAndress(scanData.toString())
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





