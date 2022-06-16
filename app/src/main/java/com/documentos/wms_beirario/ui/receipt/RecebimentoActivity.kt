package com.documentos.wms_beirario.ui.receipt

import ReceiptRepository
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityRecebimentoBinding
import com.documentos.wms_beirario.databinding.LayoutCustomFinishMovementAdressBinding
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode2
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode3
import com.documentos.wms_beirario.model.recebimento.request.PostReciptQrCode1
import com.documentos.wms_beirario.model.recebimento.response.ReceiptDoc1
import com.documentos.wms_beirario.ui.receipt.adapter.AdapterNoPointer
import com.documentos.wms_beirario.ui.receipt.adapter.AdapterPointed
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class RecebimentoActivity : AppCompatActivity(), Observer {

    private lateinit var mAdapterPointed: AdapterPointed
    private lateinit var mAdapterNoPointed: AdapterNoPointer
    private lateinit var mBinding: ActivityRecebimentoBinding
    private lateinit var mViewModel: ReceiptViewModel
    private var mIdTarefaReceipt: String? = null
    private var mListPonted: Int? = 0
    private var mValidCall: Boolean = false
    private var mListNoPonted: Int? = 0
    private var mMessageReading3: String = ""
    private var mIdConference: String? = null
    private lateinit var mDialog: Dialog
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRecebimentoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
        initViewModel()
        mDialog = CustomAlertDialogCustom().progress(this)
        setupEditText()
        setupRecyclerViews()
        setupViews()
        setupToolbar()
        setupObservables()
        setupClickGrupButton()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this, ReceiptViewModel.ReceiptViewModelFactory(
                ReceiptRepository()
            )
        )[ReceiptViewModel::class.java]
    }

    private fun setupRecyclerViews() {
        mAdapterNoPointed = AdapterNoPointer()
        mAdapterPointed = AdapterPointed()
        mBinding.rvPonted.apply {
            layoutManager = LinearLayoutManager(this@RecebimentoActivity)
            adapter = mAdapterPointed
        }
        mBinding.rvNoPonted.apply {
            layoutManager = LinearLayoutManager(this@RecebimentoActivity)
            adapter = mAdapterNoPointed
        }
    }

    override fun onResume() {
        super.onResume()
        mDialog.hide()
        initDataWead()
        mBinding.txtRespostaFinalizar.visibility = View.INVISIBLE
        mBinding.progressEditRec.isVisible = false
    }

    private fun initDataWead() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun setupToolbar() {
        mBinding.toolbarRec.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = "[${getVersion()}]"
        }
    }

    private fun setupViews() {
        mBinding.apply {
            buttonGroupReceipt.visibility = View.INVISIBLE
            buttonclear.isEnabled = false
            buttonFinish.isEnabled = false
        }
    }

    private fun setupClickGrupButton() {
        mBinding.buttonGroupReceipt.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) when (checkedId) {
                R.id.button_no_ponted -> {
                    setTxtButtons()
                    mBinding.rvPonted.visibility = View.INVISIBLE
                    mBinding.rvNoPonted.visibility = View.VISIBLE
                }
                R.id.button_ponted -> {
                    setTxtButtons()
                    mBinding.rvPonted.visibility = View.VISIBLE
                    mBinding.rvNoPonted.visibility = View.INVISIBLE
                }

            }
        }

        mBinding.buttonclear.setOnClickListener {
            clickButtonClear()
        }
        mBinding.buttonFinish.setOnClickListener {
            alertFinish()
        }
    }

    /**
     * CLICK BUTTON LIMPAR TELA -->
     */
    private fun clickButtonClear() {
        mBinding.progressInit.isVisible = true
        lifecycleScope.launch {
            delay(600)
            mValidCall = false
            mIdTarefaReceipt = null
            mAdapterNoPointed.submitList(listOf())
            mAdapterPointed.submitList(listOf())
            mBinding.txtRespostaFinalizar.visibility = View.INVISIBLE
            mBinding.buttonGroupReceipt.visibility = View.INVISIBLE
            mBinding.buttonclear.isEnabled = false
            mBinding.buttonFinish.isEnabled = false
            mBinding.progressInit.isVisible = false
        }
    }

    private fun setTxtButtons() {
        mBinding.buttonPonted.text = getString(R.string.ponted_receipt, mListPonted)
        mBinding.buttonNoPonted.text = getString(R.string.no_ponted_receipt, mListNoPonted)
    }

    /**LEITURA QRCODE------------------------->*/
    private fun setupEditText() {
        mBinding.editRec.requestFocus()
        mBinding.editRec.extensionSetOnEnterExtensionCodBarras {
            val qrcodeReading = mBinding.editRec.text.toString()
            if (qrcodeReading != "") {
                if (!mValidCall) {
                    mBinding.progressEditRec.isVisible = true
                    pushData(qrcodeReading)
                    clearEdit()
                } else {
                    if (mIdTarefaReceipt == null) {
                        mViewModel.mReceiptPost2(
                            null,
                            PostReceiptQrCode2(qrcodeReading)
                        )
                    } else {
                        mViewModel.mReceiptPost2(
                            mIdTarefaReceipt,
                            PostReceiptQrCode2(qrcodeReading)
                        )
                    }
                    clearEdit()
                }
            }
        }

    }

    private fun pushData(QrCodeReading: String) {
        mViewModel.mReceiptPost1(PostReciptQrCode1(QrCodeReading))
        clearEdit()
    }


    private fun setupObservables() {
        /**SUCESSO PRIMEIRA LEITURA 01-->*/
        mViewModel.mSucessPostCodBarrasShow1.observe(this) { listReceipt ->
            setSizeListSubmit(listReceipt)
            mIdConference = listReceipt.idTarefaConferencia
            mIdTarefaReceipt = listReceipt.idTarefaRecebimento
            if (listReceipt.idTarefaConferencia != null && listReceipt.idTarefaRecebimento == null) {
                setSucessViews()
                mBinding.txtRespostaFinalizar.visibility = View.VISIBLE
                mBinding.buttonFinish.isEnabled = true
                mMessageReading3 = listReceipt.mensagem
                alertFinish()
                setTxtButtons()
                mValidCall = true
            } else {
                mValidCall = true
                CustomAlertDialogCustom().alertMessageAtencao(this, listReceipt.mensagem)
                setTxtButtons()
                setSucessViews()
                mAdapterPointed.submitList(listReceipt.numerosSerieApontados)
                mAdapterNoPointed.submitList(listReceipt.numerosSerieNaoApontados)
            }
        }
        /**ERROR PRIMEIRA LEITURA -->*/
        mViewModel.mErrorShow.observe(this) { messageError ->
            clearEdit()
            mDialog.hide()
            vibrateExtension(500)
            CustomAlertDialogCustom().alertMessageErrorSimples(this, messageError, 2000)
        }
        /**VALID PROGRESS -->*/
        mViewModel.mProgressValidShow.observe(this) { validProgress ->
            if (validProgress) {
                mBinding.progressEditRec.visibility = View.VISIBLE
            }
            mBinding.progressEditRec.visibility = View.INVISIBLE
        }

        mViewModel.mErrorAllShow.observe(this, { errorAll ->
            CustomAlertDialogCustom().alertMessageErrorSimples(this, errorAll, 2000)
        })

        /**SUCESSO NA SEGUNDA LEITURA,APOS LER UM ENDEREÇO VALIDO 02 --->*/
        mViewModel.mSucessPostCodBarrasShow2.observe(this) { listREading2 ->
            mIdConference = listREading2.idTarefaConferencia
            mIdTarefaReceipt = listREading2.idTarefaRecebimento
            /**caso 2 -> SE NAO TIVER ITENS PARA APONTAR -->*/
            if (listREading2.numerosSerieNaoApontados.isEmpty()) {
                setSizeListSubmit(listREading2)
                mBinding.buttonFinish.isEnabled = true
                mMessageReading3 = listREading2.mensagem
                mAdapterNoPointed.submitList(listREading2.numerosSerieNaoApontados)
                mAdapterPointed.submitList(listREading2.numerosSerieApontados)
                setTxtButtons()
                alertFinish()
            } else {
                CustomMediaSonsMp3().somSucess(this)
                setSizeListSubmit(listREading2)
                mAdapterNoPointed.submitList(listREading2.numerosSerieNaoApontados)
                mAdapterPointed.submitList(listREading2.numerosSerieApontados)
                setTxtButtons()
            }
        }
        /**
         *  SUCESSO AO FINALIZAR RECEBIMENTO -> FALTA VALIDAR PARA ENCERRAR!!!
         */
        mViewModel.mSucessPostCodBarrasShow3.observe(this) { messageFinish ->
            mDialog.hide()
            vibrateExtension(500)
            clickButtonClear()
            CustomAlertDialogCustom().alertMessageSucess(this, messageFinish)
        }
    }

    private fun setSizeListSubmit(listREading2: ReceiptDoc1) {
        mListNoPonted = listREading2.numerosSerieNaoApontados.size
        mListPonted = listREading2.numerosSerieApontados.size
    }

    private fun setSucessViews() {
        vibrateExtension(500)
        mBinding.rvNoPonted.visibility = View.VISIBLE
        mBinding.buttonclear.isEnabled = true
        mBinding.buttonNoPonted.isChecked = true
        mBinding.buttonGroupReceipt.visibility = View.VISIBLE
        mBinding.rvPonted.visibility = View.INVISIBLE
    }

    private fun alertFinish() {
        CustomMediaSonsMp3().somAtencao(this)
        val mAlert = AlertDialog.Builder(this)
        mAlert.setCancelable(false)
        val binding = LayoutCustomFinishMovementAdressBinding.inflate(LayoutInflater.from(this))
        mAlert.setView(binding.root)
        val mShow = mAlert.show()
        binding.editQrcodeCustom.requestFocus()
        binding.progressEdit.isVisible = false
        binding.txtCustomAlert.text = mMessageReading3
        val mButtonCancelar = binding.buttonCancelCustom
        hideKeyExtensionActivity(binding.editQrcodeCustom)
        binding.editQrcodeCustom.addTextChangedListener { qrCodeReading ->
            if (qrCodeReading.toString() != "") {
                binding.progressEdit
                Handler(Looper.getMainLooper()).postDelayed({
                    mIdConference?.let { idConf ->
                        mViewModel.postReceipt3(
                            mIdTarefaConferencia = idConf,
                            PostReceiptQrCode3(qrCodeReading.toString())
                        )
                    }
                    mShow.dismiss()
                }, 200)
                binding.editQrcodeCustom.setText("")
                binding.editQrcodeCustom.requestFocus()
            }
        }
        mButtonCancelar.setOnClickListener {
            binding.progressEdit.isVisible = false
            CustomMediaSonsMp3().somClick(this)
            mBinding.buttonFinish.isEnabled = true
            mShow.dismiss()
        }
        mShow.create()
    }

    override fun update(p0: Observable?, p1: Any?) {}

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            Log.e("RECEBIMENTO -->", "Dados recebidos New Intent: $scanData")
            pushData(scanData!!)
            clearEdit()
        }
    }

    private fun clearEdit() {
        mBinding.editRec.setText("")
        mBinding.editRec.text?.clear()
        mBinding.editRec.requestFocus()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        mDialog.dismiss()
    }
}