package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.activitys

import android.app.Dialog
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
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityMovimentacaoEnderecos1Binding
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.RequestAddProductMov3
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.RequestReadingAndressMov2
import com.documentos.wms_beirario.repository.movimentacaoentreenderecos.MovimentacaoEntreEnderecosRepository
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.adapter.Adapter1Movimentacao
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.viewmodel.ReturnTaskViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import java.util.*

class MovimentacaoEnderecosActivity1 : AppCompatActivity(), java.util.Observer {
    //1099 card

    private lateinit var mBinding: ActivityMovimentacaoEnderecos1Binding
    private lateinit var mAdapter: Adapter1Movimentacao
    private lateinit var mViewModel: ReturnTaskViewModel
    private lateinit var mShared: CustomSharedPreferences
    private lateinit var mProgress: Dialog
    private lateinit var mDialog: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mediaSonsMp3: CustomMediaSonsMp3
    private lateinit var mVibrar: CustomAlertDialogCustom
    private val TAG = "Nova Movimentacao -->"
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private var mEndVisual: String = ""
    private var mCliqueChip: Boolean = false
    private var mIdEndereço: Int? = null
    private var mIdTarefa: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMovimentacaoEnderecos1Binding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupDataWedge()
        initDataWedge()
        initViewModel()
        setToolbar()
        setObservable()
        clickFinishTask()
        setSwipeRefreshLayout()
        callApi()
        initRv()
        clickChip()
        clearEdit(mBinding.editMov)

    }

    override fun onResume() {
        super.onResume()
        hideKeyExtensionActivity(mBinding.editMov)
    }

    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    override fun update(p0: Observable?, p1: Any?) {}
    private fun setupDataWedge() {
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
    }

    private fun setSwipeRefreshLayout() {
        mBinding.swipeRefreshLayoutMov1.apply {
            setColorSchemeColors(this@MovimentacaoEnderecosActivity1.getColor(R.color.color_default))
            setOnRefreshListener {
                mBinding.imageLottie.visibility = View.INVISIBLE
                mBinding.progressBarInitMovimentacao1.isVisible = true
                initRv()
                callApi()
                isRefreshing = false
            }
        }
    }

    private fun callApi() {
        mViewModel.returnTaskMov()
    }

    private fun initViewModel() {
        mToast = CustomSnackBarCustom()
        mediaSonsMp3 = CustomMediaSonsMp3()
        mVibrar = CustomAlertDialogCustom()
        mDialog = CustomAlertDialogCustom()
        mProgress = CustomAlertDialogCustom().progress(
            this,
            getString(R.string.create_new_task)
        )
        mProgress.hide()
        mShared = CustomSharedPreferences(this)
        mBinding.imageLottie.visibility = View.INVISIBLE
        mBinding.chipAnddress.visibility = View.INVISIBLE
        mViewModel = ViewModelProvider(
            this, ReturnTaskViewModel.Mov1ViewModelFactory(
                MovimentacaoEntreEnderecosRepository()
            )
        )[ReturnTaskViewModel::class.java]

    }

    private fun setToolbar() {
        mBinding.toolbarMov1.subtitle = "[${getVersion()}]"
        mBinding.toolbarMov1.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = getVersionNameToolbar()
        }
    }

    private fun initRv() {
        mAdapter = Adapter1Movimentacao()
        mBinding.rvMovimentacao1.apply {
            layoutManager = LinearLayoutManager(this@MovimentacaoEnderecosActivity1)
            adapter = mAdapter
        }
    }

    //CLIQUE NO CHIP ----------------------------------------------------------->
    private fun clickChip() {
        mBinding.chipAnddress.setOnCloseIconClickListener {
            mDialog.alertMessageAtencaoOptionAction(
                context = this,
                message = getString(R.string.opao_mov1_dialog),
                actionYes = {
                    mBinding.chipAnddress.visibility = View.GONE
                    mCliqueChip = false
                    mBinding.editMov.hint = getString(R.string.reading_anddress_mov1)
                    clearEdit(mBinding.editMov)
                },
                actionNo = {
                    mToast.snackBarSimplesBlack(mBinding.root, "Endereço $mEndVisual mantido!")
                    clearEdit(mBinding.editMov)
                }
            )
        }
    }

    private fun setObservable() {
        //DEFINE OS ITENS DA RECYCLERVIEW ->
        mViewModel.mSucessShow.observe(this) { listTask ->
            mIdTarefa = listTask.idTarefa.toString()
            if (listTask.itens.isNullOrEmpty()) {
                mBinding.imageLottie.visibility = View.VISIBLE
                mBinding.chipAnddress.visibility = View.VISIBLE
            } else {
                if (mEndVisual.isNotEmpty()) {
                    mBinding.chipAnddress.text = mEndVisual
                } else {
                    mBinding.chipAnddress.visibility = View.GONE
                }
                mBinding.imageLottie.visibility = View.INVISIBLE
                mAdapter.submitList(listTask.itens)

            }
        }
        //ERRO ->
        mViewModel.mErrorShow.observe(this) { messageError ->
            mProgress.hide()
            mDialog.alertMessageErrorSimples(this, message = messageError)
            clearEdit(mBinding.editMov)
        }
        //VALIDA PROGRESSBAR -->
        mViewModel.mValidProgressShow.observe(this) { validProgress ->
            mBinding.progressBarInitMovimentacao1.isVisible = validProgress
        }

        /**RESPOSTA LEITURA DO ENDEREÇO ----------------------->*/
        mViewModel.mReadingAndress2Show.observe(this) { responseReading ->
            clearEdit(mBinding.editMov)
            if (responseReading.enderecoVisual.isNullOrEmpty()) {
                mBinding.chipAnddress.visibility = View.GONE
            } else {
                mIdEndereço = responseReading.idEndereco
                mBinding.editMov.hint = "Leia um produto:"
                mCliqueChip = true
                mEndVisual = responseReading.enderecoVisual
                mBinding.chipAnddress.apply {
                    visibility = View.VISIBLE
                    text = responseReading.enderecoVisual
                }
            }
        }

        /**RESPOSTA ADIDIONAR PRODUTO -->*/
        mViewModel.mAddProductMov3Show.observe(this) { response ->
            mToast.toastCustomSucess(this, response)
            mediaSonsMp3.somSucess(this)
            mViewModel.returnTaskMov()
            clearEdit(mBinding.editMov)
        }
    }

    private fun clickFinishTask() {
        mBinding.buttonNewTask.setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed({
//                mViewModel.newTask()
            }, 1000)
            mProgress.show()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            if (scanData != null) {
                if (!mCliqueChip) {
                    readingAnddress02(scanData.trim())
                    clearEdit(mBinding.editMov)
                } else {
                    addProduct(scanData.trim())
                    clearEdit(mBinding.editMov)
                }
            }
        }
    }

    /**ENVIANDO BODY ADICIONA TAREFA -->*/
    private fun addProduct(scanData: String) {
        val body = RequestAddProductMov3(
            codBarras = scanData,
            idTarefa = mIdTarefa,
            idEndOrigem = mIdEndereço!!
        )
        mViewModel.addProductMov3(body = body)
    }

    private fun readingAnddress02(scanData: String?) {
        if (!scanData.isNullOrEmpty()) {
            mViewModel.readingAndres2(RequestReadingAndressMov2(codEndOrigem = scanData))
            clearEdit(mBinding.editMov)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mProgress.dismiss()
    }
}
