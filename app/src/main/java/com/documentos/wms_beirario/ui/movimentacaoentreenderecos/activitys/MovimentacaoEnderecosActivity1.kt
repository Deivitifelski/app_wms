package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.activitys

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
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
import com.documentos.wms_beirario.databinding.LayoutCustomFinishMovementAdressBinding
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.BodyCancelMov5
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.RequestAddProductMov3
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.RequestBodyFinalizarMov4
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.RequestReadingAndressMov2
import com.documentos.wms_beirario.repository.movimentacaoentreenderecos.MovimentacaoEntreEnderecosRepository
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.adapter.Adapter1Movimentacao
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.viewmodel.ReturnTaskViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import java.util.*

class MovimentacaoEnderecosActivity1 : AppCompatActivity(), Observer {
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
    private var mAlert: android.app.AlertDialog? = null
    private val receiver = DWReceiver()
    private var initialized = false
    private var mEndVisual: String = ""
    private var mCliqueChip: Boolean = false
    private var mIdEndereço: Int? = null
    private var mIdTarefa: String? = null

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
        clickCancel()
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
                mBinding.txtInfEmplyTask.visibility = View.INVISIBLE
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
        mBinding.buttonCancelTask.isEnabled = false
        mBinding.buttonFinishTask.isEnabled = false
        mToast = CustomSnackBarCustom()
        mediaSonsMp3 = CustomMediaSonsMp3()
        mVibrar = CustomAlertDialogCustom()
        mDialog = CustomAlertDialogCustom()
        mProgress = CustomAlertDialogCustom().progress(this, getString(R.string.finish_task))
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

    /**CLIQUE NO CHIP -->*/
    private fun clickChip() {
        mBinding.chipAnddress.setOnCloseIconClickListener {
            mDialog.alertMessageAtencaoOptionAction(
                context = this,
                message = getString(R.string.opao_mov1_dialog),
                actionYes = {
                    mBinding.chipAnddress.visibility = View.GONE
                    mCliqueChip = false
                    mBinding.editLayout.hint = getString(R.string.reading_anddress_mov1)
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
        /**ERRO --------------------->*/
        mViewModel.mErrorShow.observe(this) { messageError ->
            mAlert?.dismiss()
            mProgress.hide()
            mDialog.alertMessageErrorSimples(this, message = messageError)
            clearEdit(mBinding.editMov)
        }

        /**VALIDA PROGRESSBAR -->*/
        mViewModel.mValidProgressShow.observe(this) { validProgress ->
            mBinding.progressBarInitMovimentacao1.isVisible = validProgress
        }

        /**RESPONSE GET TAREFAS -->*/
        mViewModel.mSucessShow.observe(this) { responseTask ->
            if (responseTask.idTarefa != null) {
                mBinding.txtTotalMov.text = "Total: ${responseTask.itens.size}"
                mBinding.txtInfEmplyTask.visibility = View.INVISIBLE
                mIdTarefa = responseTask.idTarefa
                mBinding.buttonFinishTask.isEnabled = true
                mBinding.buttonCancelTask.isEnabled = true
                mAdapter.submitList(responseTask.itens)
                mBinding.apply {
                    buttonCancelTask.isEnabled = true
                    buttonFinishTask.isEnabled = true
                }
            } else {
                mBinding.txtTotalMov.text = "Total: 0"
                mBinding.txtInfEmplyTask.visibility = View.VISIBLE
                mAdapter.submitList(null)
                mIdTarefa = null
                mBinding.apply {
                    buttonCancelTask.isEnabled = false
                    buttonFinishTask.isEnabled = false
                }
            }
            if (mEndVisual.isNotEmpty()) {
                mBinding.chipAnddress.text = mEndVisual
            } else {
                mBinding.chipAnddress.visibility = View.GONE
            }

        }

        /**RESPOSTA LEITURA DO ENDEREÇO ----------------------->*/
        mViewModel.mReadingAndress2Show.observe(this) { responseReading ->
            clearEdit(mBinding.editMov)
            if (responseReading.enderecoVisual.isNullOrEmpty()) {
                mBinding.chipAnddress.visibility = View.GONE
            } else {
                mIdEndereço = responseReading.idEndereco
                mBinding.editLayout.hint = getString(R.string.reading_product)
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

        /**RESPOSTA FINALIZAR -->*/
        mViewModel.finishTaskShow.observe(this) {
            mProgress.dismiss()
            clearEdit(mBinding.editMov)
            mAlert?.dismiss()
            mDialog.alertMessageSucessAction(context = this,
                message = "Tarefa finalizada com sucesso!",
                action = {
                    mBinding.chipAnddress.visibility = View.GONE
                    mCliqueChip = false
                    mBinding.editLayout.hint = getString(R.string.reading_anddress_mov1)
                    clearEdit(mBinding.editMov)
                    mProgress.dismiss()
                    mViewModel.returnTaskMov()
                }
            )
        }
        /**RESPONSE CANCELAR TAREFA -->*/
        mViewModel.cancelTaskShow.observe(this) { response ->
            mCliqueChip = false
            mBinding.editLayout.hint = getString(R.string.reading_anddress_mov1)
            mBinding.chipAnddress.visibility = View.GONE
            mToast.toastCustomSucess(this, response.result)
            mediaSonsMp3.somLeituraConcluida(this)
            mViewModel.returnTaskMov()
        }
    }

    /**CLIQUE BUTTONS FINALIZAR -->*/
    private fun clickFinishTask() {
        mBinding.buttonFinishTask.setOnClickListener {
            alertFinish()
        }
    }

    /**CLIQUE BUTTON CANCELAR TAREFA -->*/
    private fun clickCancel() {
        mBinding.buttonCancelTask.setOnClickListener {
            mDialog.alertMessageAtencaoOptionAction(
                context = this,
                message = "Deseja cancelar a tarefa?",
                actionYes = {
                    if (!mIdTarefa.isNullOrEmpty()) {
                        mViewModel.cancelTask(BodyCancelMov5(idTarefa = mIdTarefa!!))
                    }
                },
                actionNo = {
                    clearEdit(mBinding.editMov)
                }
            )
        }
    }

    /**
     * DIALOG QUE REALIDA A LEITURA PARA FINALIZAR A MOVIMENTAÇAO -->
     */
    private fun alertFinish() {
        mAlert = android.app.AlertDialog.Builder(this).create()
        mAlert?.setCancelable(false)
        val mBindingAlert =
            LayoutCustomFinishMovementAdressBinding.inflate(LayoutInflater.from(this))
        mAlert?.setView(mBindingAlert.root)
        mAlert?.create()
        mAlert?.show()
        mBindingAlert.progressEdit.visibility = View.INVISIBLE
        hideKeyExtensionActivity(mBindingAlert.editQrcodeCustom)
        mBindingAlert.editQrcodeCustom.setText("")
        mBindingAlert.editQrcodeCustom.requestFocus()
        mBindingAlert.buttonCancelCustom.setOnClickListener {
            mProgress.dismiss()
            mAlert?.dismiss()
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            if (scanData != null) {
                clearEdit(mBinding.editMov)
                if (mAlert?.isShowing == true) {
                    mAlert?.dismiss()
                    sendReandingFinish05(scanData.trim())
                    mProgress.show()
                } else {
                    if (!mCliqueChip) {
                        readingAnddress02(scanData.trim())
                        clearEdit(mBinding.editMov)
                    } else {
                        addProduct03(scanData.trim())
                        clearEdit(mBinding.editMov)
                    }
                }
            }
        }
    }

    /**FINALIZAÇÃO ->*/
    private fun sendReandingFinish05(qrCode: String) {
        val body = RequestBodyFinalizarMov4(
            codBarras = qrCode,
            idTarefa = mIdTarefa!!
        )
        mViewModel.finishTask4(body = body)
    }

    /**ENVIANDO BODY ADICIONA TAREFA -->*/
    private fun addProduct03(scanData: String) {
        val body = RequestAddProductMov3(
            codBarras = scanData,
            idTarefa = mIdTarefa,
            idEndOrigem = mIdEndereço
        )
        mViewModel.addProductMov3(body = body)
    }

    private fun readingAnddress02(scanData: String) {
        mViewModel.readingAndres2(RequestReadingAndressMov2(codEndOrigem = scanData))
        clearEdit(mBinding.editMov)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mProgress.dismiss()
        unregisterReceiver(receiver)
    }
}
