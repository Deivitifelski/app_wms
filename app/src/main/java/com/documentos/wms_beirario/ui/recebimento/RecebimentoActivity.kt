package com.documentos.wms_beirario.ui.recebimento

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.ActivityRecebimentoBinding
import com.documentos.wms_beirario.databinding.LayoutCustomFinishMovementAdressBinding
import com.documentos.wms_beirario.model.recebimento.ReceiptDoc1
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode2
import com.documentos.wms_beirario.model.recebimento.request.PostReceiptQrCode3
import com.documentos.wms_beirario.model.recebimento.request.PostReciptQrCode1
import com.documentos.wms_beirario.repository.recebimento.ReceiptRepository
import com.documentos.wms_beirario.ui.recebimento.adapter.AdapterNoPointer
import com.documentos.wms_beirario.ui.recebimento.adapter.AdapterPointed
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.*
import com.example.coletorwms.constants.CustomMediaSonsMp3
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RecebimentoActivity : AppCompatActivity() {

    private lateinit var mAdapterPointed: AdapterPointed
    private lateinit var mAdapterNoPointed: AdapterNoPointer
    private lateinit var mBinding: ActivityRecebimentoBinding
    private val mRetrofitService = ServiceApi.getInstance()
    private lateinit var mViewModel: ReceiptViewModel
    private var mIdTarefaReceipt: String? = null
    private var mListPonted: Int? = 0
    private var mValidCall: Boolean = false
    private var mListNoPonted: Int? = 0
    private var mMessageReading3: String = ""
    private var mIdConference: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRecebimentoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mViewModel = ViewModelProvider(
            this, ReceiptViewModel.RecebimentoFactory(
                ReceiptRepository(mRetrofitService)
            )
        )[ReceiptViewModel::class.java]
        setupEditText()
        setupRecyclerViews()
        setupViews()
        setupToolbar()
        setupObservables()
        setupClickGrupButton()
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
        mBinding.txtRespostaFinalizar.visibility = View.INVISIBLE
        AppExtensions.visibilityProgressBar(mBinding.progressEditRec, visibility = false)
    }

    private fun setupToolbar() {
        mBinding.toolbarRec.apply {
            setNavigationOnClickListener {
                onBackTransition()
            }
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

    private fun clickButtonClear() {
        AppExtensions.visibilityProgressBar(mBinding.progressInit, visibility = true)
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
            AppExtensions.visibilityProgressBar(mBinding.progressInit, visibility = false)
        }
    }

    private fun setTxtButtons() {
        mBinding.buttonPonted.text = getString(R.string.ponted_receipt, mListPonted)
        mBinding.buttonNoPonted.text = getString(R.string.no_ponted_receipt, mListNoPonted)
    }

    /**LEITURA QRCODE------------------------->*/
    private fun setupEditText() {
        mBinding.editRec.requestFocus()
        hideKeyExtensionActivity(mBinding.editRec)
        mBinding.editRec.addTextChangedListener { qrcodeReading ->
            if (qrcodeReading.toString() != "") {
                if (!mValidCall) {
                    AppExtensions.visibilityProgressBar(mBinding.progressEditRec, visibility = true)
                    mViewModel.mReceiptPost1(PostReciptQrCode1(qrcodeReading.toString()))
                    clearEditReading()
                } else {
                    if (mIdTarefaReceipt == null) {
                        mViewModel.mReceiptPost2(
                            null,
                            PostReceiptQrCode2(qrcodeReading.toString())
                        )
                    } else {
                        mViewModel.mReceiptPost2(
                            mIdTarefaReceipt,
                            PostReceiptQrCode2(qrcodeReading.toString())
                        )
                    }
                    clearEditReading()
                }
            }
        }
    }

    private fun clearEditReading() {
        mBinding.editRec.setText("")
        mBinding.editRec.requestFocus()
    }

    private fun setupObservables() {
        /**SUCESS READING 01-->*/
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
        /**ERROR READING -->*/
        mViewModel.mErrorShow.observe(this) { messageError ->
            vibrateExtension(500)
            CustomAlertDialogCustom().alertMessageErrorSimples(this, messageError)
        }
        /**VALID PROGRESS -->*/
        mViewModel.mProgressValidShow.observe(this) { validProgress ->
            if (validProgress) {
                mBinding.progressEditRec.visibility = View.VISIBLE
            }
            mBinding.progressEditRec.visibility = View.INVISIBLE
        }
        /**READING 02 --->*/
        mViewModel.mSucessPostCodBarrasShow2.observe(this) { listREading2 ->
            mIdConference = listREading2.idTarefaConferencia
            mIdTarefaReceipt = listREading2.idTarefaRecebimento
            CustomMediaSonsMp3().somSucessReading(this)
            if (listREading2.idTarefaConferencia != null) {
                setSizeListSubmit(listREading2)
                alertFinish()
            }
            if (listREading2.numerosSerieNaoApontados.isEmpty()) {
                setSizeListSubmit(listREading2)
                mBinding.buttonFinish.isEnabled = true
                mMessageReading3 = listREading2.mensagem
                alertFinish()
                mAdapterNoPointed.submitList(listREading2.numerosSerieNaoApontados)
            } else {
                setSizeListSubmit(listREading2)
                mAdapterNoPointed.submitList(listREading2.numerosSerieNaoApontados)
                mAdapterPointed.submitList(listREading2.numerosSerieApontados)
                setTxtButtons()
            }
        }
        /**SUCESS FINISH --->*/
        mViewModel.mSucessPostCodBarrasShow3.observe(this) { messageFinish ->
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
        AppExtensions.visibilityProgressBar(binding.progressEdit, false)
        binding.txtCustomAlert.text = mMessageReading3
        val mButtonCancelar = binding.buttonCancelCustom
        hideKeyExtensionActivity(binding.editQrcodeCustom)
        binding.editQrcodeCustom.addTextChangedListener { qrCodeReading ->
            if (qrCodeReading.toString() != "") {
                AppExtensions.visibilityProgressBar(binding.progressEdit, true)
                Handler().postDelayed({
                    mIdConference?.let { idConf ->
                        mViewModel.postReceipt3(
                            mIdTarefaConferencia = idConf,
                            PostReceiptQrCode3(qrCodeReading.toString())
                        )
                    }
                    AppExtensions.visibilityProgressBar(binding.progressEdit, false)
                    mShow.dismiss()
                }, 200)
                binding.editQrcodeCustom.setText("")
                binding.editQrcodeCustom.requestFocus()
            }
        }
        mButtonCancelar.setOnClickListener {
            AppExtensions.visibilityProgressBar(binding.progressEdit, false)
            CustomMediaSonsMp3().somClick(this)
            mBinding.buttonFinish.isEnabled = true
            mShow.dismiss()
        }
        mShow.create()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }
}