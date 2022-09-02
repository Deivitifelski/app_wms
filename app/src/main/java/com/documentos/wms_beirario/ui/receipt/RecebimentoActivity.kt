package com.documentos.wms_beirario.ui.receipt

import ReceiptRepository
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.hideKeyExtensionActivity
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
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
    private lateinit var mAlertDialogCustom: CustomAlertDialogCustom
    private var mAlert: android.app.AlertDialog? = null
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mProgressAlert: ProgressBar
    private lateinit var mSons: CustomMediaSonsMp3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRecebimentoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initViewModel()
        mSons = CustomMediaSonsMp3()
        mProgressAlert = ProgressBar(this)
        initRv()
        setupViews()
        setupToolbar()
        setupObservables()
        setupClickGrupButton()
        mBinding.txtRespostaFinalizar.visibility = View.INVISIBLE
    }

    override fun onStart() {
        super.onStart()
        initDataWedge()
        setupDataWedge()
        clearEdit()
    }

    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun initViewModel() {
        mAlertDialogCustom = CustomAlertDialogCustom()
        mViewModel = ViewModelProvider(
            this, ReceiptViewModel.ReceiptViewModelFactory(
                ReceiptRepository()
            )
        )[ReceiptViewModel::class.java]
    }

    private fun initRv() {
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


    private fun setupToolbar() {
        mBinding.toolbarRec.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = getVersionNameToolbar()
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
            delay(400)
            mValidCall = false
            mIdTarefaReceipt = null
            mAdapterNoPointed.submitList(listOf())
            mAdapterPointed.submitList(listOf())
            mBinding.txtRespostaFinalizar.visibility = View.INVISIBLE
            mBinding.buttonGroupReceipt.visibility = View.INVISIBLE
            mBinding.buttonclear.isEnabled = false
            mBinding.buttonFinish.isEnabled = false
            mBinding.progressInit.isVisible = false
            mBinding.editRec.hint = getString(R.string.reading_danfe_et)
        }
    }

    private fun setTxtButtons() {
        mBinding.buttonPonted.text = getString(R.string.ponted_receipt, mListPonted)
        mBinding.buttonNoPonted.text = getString(R.string.no_ponted_receipt, mListNoPonted)
    }


    private fun pushData(QrCodeReading: String) {
        mViewModel.mReceiptPost1(PostReciptQrCode1(QrCodeReading))
    }


    private fun setupObservables() {
        /**SUCESSO PRIMEIRA LEITURA 01-->*/
        mViewModel.mSucessPostCodBarrasShow1.observe(this) { listReceipt ->
            clearEdit()
            mBinding.editRec.hint = getString(R.string.reading_number_et)
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
            mAlertDialogCustom.alertMessageErrorSimples(this, messageError)
            clearEdit()
        }
        /**VALID PROGRESS -->*/
        mViewModel.mProgressValidShow.observe(this) { validProgress ->
            mBinding.progressRec.isVisible = validProgress
        }

        mViewModel.mErrorAllShow.observe(this) { errorAll ->
            mAlertDialogCustom.alertMessageErrorSimples(this, errorAll)
        }

        /**SUCESSO NA SEGUNDA LEITURA,APOS LER UM ENDEREÇO VALIDO 02 --->*/
        mViewModel.mSucessPostCodBarrasShow2.observe(this) { listREading2 ->
            clearEdit()
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
                mSons.somSucess(this)
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
            mProgressAlert.isVisible = false
            mAlert?.hide()
            clickButtonClear()
            mAlertDialogCustom.alertMessageSucess(this, messageFinish)
        }
        mViewModel.mErrorFinishShow.observe(this) { errorFinish ->
            mProgressAlert.isVisible = false
            mAlert?.hide()
            mAlertDialogCustom.alertMessageErrorSimples(this, errorFinish)
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
        mAlert = AlertDialog.Builder(this).create()
        mSons.somAtencao(this)
        mAlert?.setCancelable(false)
        val binding = LayoutCustomFinishMovementAdressBinding.inflate(LayoutInflater.from(this))
        mAlert?.setView(binding.root)
        mAlert?.create()
        binding.editQrcodeCustom.requestFocus()
        binding.editQrcodeCustom.setText("")
        binding.progressEdit.isVisible = false
        binding.txtCustomAlert.text = mMessageReading3
        val mButtonCancelar = binding.buttonCancelCustom
        mProgressAlert = binding.progressEdit
        mProgressAlert.isVisible = false
        hideKeyExtensionActivity(binding.editQrcodeCustom)
        mButtonCancelar.setOnClickListener {
            binding.progressEdit.isVisible = false
            CustomMediaSonsMp3().somClick(this)
            mBinding.buttonFinish.isEnabled = true
            mAlert?.dismiss()
        }
        mAlert?.show()
        Log.e(
            "ALERTA DIALOG -->",
            "TEXT DO EDIT TEXT DO ALERTA = ${binding.editQrcodeCustom.text.toString()}"
        )
    }

    private fun clearEdit() {
        mBinding.editRec.setText("")
        mBinding.editRec.text?.clear()
        mBinding.editRec.requestFocus()
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
            if (mAlert?.isShowing == true) {
                mIdConference?.let { idConf ->
                    mViewModel.postReceipt3(
                        mIdTarefaConferencia = idConf,
                        PostReceiptQrCode3(scanData.toString())
                    )
                }
                mProgressAlert.isVisible = true
                clearEdit()
            } else {
                sendData(scanData.toString())
                clearEdit()
            }
        }
    }

    private fun sendData(scanData: String) {
        if (scanData != "") {
            if (!mValidCall) {
                pushData(scanData)
                clearEdit()
            } else {
                if (mIdTarefaReceipt == null) {
                    mViewModel.mReceiptPost2(
                        null,
                        PostReceiptQrCode2(scanData)
                    )
                } else {
                    mViewModel.mReceiptPost2(
                        mIdTarefaReceipt,
                        PostReceiptQrCode2(scanData)
                    )
                }
                clearEdit()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}