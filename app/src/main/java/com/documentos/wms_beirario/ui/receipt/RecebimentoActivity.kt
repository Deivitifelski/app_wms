package com.documentos.wms_beirario.ui.receipt

import ReceiptRepository
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
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
import com.documentos.wms_beirario.utils.extensions.toastError
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Observable
import java.util.Observer

class RecebimentoActivity : AppCompatActivity(), Observer {

    private lateinit var mAdapterPointed: AdapterPointed
    private lateinit var mAdapterNoPointed: AdapterNoPointer
    private lateinit var binding: ActivityRecebimentoBinding
    private lateinit var mViewModel: ReceiptViewModel
    private var mIdTarefaReceipt: String? = null
    private var mListPonted: Int? = 0
    private var mValidCall: Boolean = false
    private var mListNoPonted: Int? = 0
    private var mMessageReading3: String = ""
    private var mIdConference: String? = null
    private lateinit var mAlertDialogCustom: CustomAlertDialogCustom
    private var mAlert: AlertDialog? = null
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mProgressAlert: ProgressBar
    private lateinit var mSons: CustomMediaSonsMp3
    private lateinit var token: String
    private var idArmazem: Int = 0
    private lateinit var sharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecebimentoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        hideKeyExtensionActivity(binding.editRec)
        mSons = CustomMediaSonsMp3()
        mProgressAlert = ProgressBar(this)
        initRv()
        setupViews()
        setupToolbar()
        setupObservables()
        setupClickGrupButton()
        binding.txtRespostaFinalizar.visibility = View.INVISIBLE
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
        sharedPreferences = CustomSharedPreferences(this)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        mAdapterNoPointed = AdapterNoPointer()
        mAdapterPointed = AdapterPointed()
        binding.rvPonted.apply {
            layoutManager = LinearLayoutManager(this@RecebimentoActivity)
            adapter = mAdapterPointed
        }
        binding.rvNoPonted.apply {
            layoutManager = LinearLayoutManager(this@RecebimentoActivity)
            adapter = mAdapterNoPointed
        }
    }


    private fun setupToolbar() {
        binding.toolbarRec.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = getVersionNameToolbar()
        }
    }

    private fun setupViews() {
        binding.apply {
            buttonGroupReceipt.visibility = View.INVISIBLE
            buttonclear.isEnabled = false
            buttonFinish.isEnabled = false
        }
    }

    private fun setupClickGrupButton() {
        binding.buttonGroupReceipt.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) when (checkedId) {
                R.id.button_no_ponted -> {
                    setTxtButtons()
                    binding.rvPonted.visibility = View.INVISIBLE
                    binding.rvNoPonted.visibility = View.VISIBLE
                }

                R.id.button_ponted -> {
                    setTxtButtons()
                    binding.rvPonted.visibility = View.VISIBLE
                    binding.rvNoPonted.visibility = View.INVISIBLE
                }
            }
        }

        binding.buttonclear.setOnClickListener {
            clickButtonClear()
        }
        binding.buttonFinish.setOnClickListener {
            alertFinish()
        }
    }

    /**
     * CLICK BUTTON LIMPAR TELA -->
     */
    private fun clickButtonClear() {
        binding.progressInit.isVisible = true
        lifecycleScope.launch {
            delay(400)
            mValidCall = false
            mIdTarefaReceipt = null
            mAdapterNoPointed.submitList(listOf())
            mAdapterPointed.submitList(listOf())
            binding.txtRespostaFinalizar.visibility = View.INVISIBLE
            binding.buttonGroupReceipt.visibility = View.INVISIBLE
            binding.buttonclear.isEnabled = false
            binding.buttonFinish.isEnabled = false
            binding.progressInit.isVisible = false
            binding.editRec.hint = getString(R.string.reading_danfe_et)
        }
    }

    private fun setTxtButtons() {
        binding.buttonPonted.text = getString(R.string.ponted_receipt, mListPonted)
        binding.buttonNoPonted.text = getString(R.string.no_ponted_receipt, mListNoPonted)
    }


    private fun pushData(QrCodeReading: String) {
        mViewModel.mReceiptPost1(PostReciptQrCode1(QrCodeReading), idArmazem, token)
    }


    private fun setupObservables() {
        /**SUCESSO PRIMEIRA LEITURA 01-->*/
        mViewModel.mSucessPostCodBarrasShow1.observe(this) { listReceipt ->
            clearEdit()
            binding.editRec.hint = getString(R.string.reading_number_et)
            setSizeListSubmit(listReceipt)
            mIdConference = listReceipt.idTarefaConferencia
            mIdTarefaReceipt = listReceipt.idTarefaRecebimento
            if (listReceipt.idTarefaConferencia != null && listReceipt.idTarefaRecebimento == null) {
                setSucessViews()
                binding.txtRespostaFinalizar.visibility = View.VISIBLE
                binding.buttonFinish.isEnabled = true
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
            binding.progressRec.isVisible = validProgress
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
                binding.buttonFinish.isEnabled = true
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
        binding.rvNoPonted.visibility = View.VISIBLE
        binding.buttonclear.isEnabled = true
        binding.buttonNoPonted.isChecked = true
        binding.buttonGroupReceipt.visibility = View.VISIBLE
        binding.rvPonted.visibility = View.INVISIBLE
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
            this.binding.buttonFinish.isEnabled = true
            mAlert?.dismiss()
        }
        mAlert?.show()
        Log.e(
            "ALERTA DIALOG -->",
            "TEXT DO EDIT TEXT DO ALERTA = ${binding.editQrcodeCustom.text.toString()}"
        )
    }

    private fun clearEdit() {
        binding.editRec.setText("")
        binding.editRec.text?.clear()
        binding.editRec.requestFocus()
    }

    private fun setupDataWedge() {
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
    }


    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            if (binding.progressRec.isVisible) {
                CustomMediaSonsMp3().somAtencao(this)
                toastError(this, "Aguarde a responsta do servidor")
            } else {
                if (mAlert?.isShowing == true) {
                    mIdConference?.let { idConf ->
                        mViewModel.postReceipt3(
                            mIdTarefaConferencia = idConf,
                            PostReceiptQrCode3(scanData.toString()),
                            idArmazem = idArmazem,
                            token = token
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
                        PostReceiptQrCode2(scanData),
                        idArmazem, token
                    )
                } else {
                    mViewModel.mReceiptPost2(
                        mIdTarefaReceipt,
                        PostReceiptQrCode2(scanData),
                        idArmazem, token
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