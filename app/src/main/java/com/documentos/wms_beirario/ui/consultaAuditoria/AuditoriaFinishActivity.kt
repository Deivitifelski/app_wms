package com.documentos.wms_beirario.ui.consultaAuditoria

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityAuditoriaFinishBinding
import com.documentos.wms_beirario.model.auditoria.BodyAuditoriaFinish
import com.documentos.wms_beirario.model.auditoria.ResponseFinishAuditoriaItem
import com.documentos.wms_beirario.repository.consultaAuditoria.AuditoriaRepository
import com.documentos.wms_beirario.ui.consultaAuditoria.viewModel.AuditoriaViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarrasString
import com.documentos.wms_beirario.utils.extensions.toastError
import java.util.Observable
import java.util.Observer

class AuditoriaFinishActivity : AppCompatActivity(), Observer {

    private val TAG = "AUDITORIA FINAL"
    private lateinit var mBinding: ActivityAuditoriaFinishBinding
    private lateinit var mAlertDialog: CustomAlertDialogCustom
    private lateinit var mSons: CustomMediaSonsMp3
    private lateinit var mAuditoria: ResponseFinishAuditoriaItem
    private lateinit var mIduditoria: String
    private lateinit var mViewModel: AuditoriaViewModel2
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAuditoriaFinishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        this.setFinishOnTouchOutside(false)

        initGetIntent()
        initConst()
        setupDialogShow()
        initEdit()
        clickButtonQnt()
        setObserver()
        setupDataWedge()
        setLayout()
    }


    override fun onResume() {
        super.onResume()
        initDataWedge()
    }

    private fun setLayout() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width: Int = displayMetrics.widthPixels
        val height: Int = displayMetrics.heightPixels

        window.setLayout((width * 0.9).toInt(), (height * 0.8).toInt())

        val params = window.attributes
        params.gravity = Gravity.CENTER
        params.x = 0
        params.y = 75

        window.attributes = params
    }

    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun setObserver() {
        /**RESPOSTA DA BIPAGEM -->*/
        mViewModel.mSucessPostShow.observe(this) { sucessPost ->
            mSons.somSucess(this)
            mAlertDialog.alertMessageSucessFinishBack(
                context = this,
                this,
                "Auditoria realizado com sucesso!"
            )
        }
        mViewModel.mErrorPostShow.observe(this) { errorPost ->
            mAlertDialog.alertMessageErrorSimplesAction(this, errorPost, action = { clearEdit() })
        }
    }

    private fun initGetIntent() {
        if (intent != null) {
            val i = intent.getSerializableExtra("AUDITORIA") as ResponseFinishAuditoriaItem
            val id = intent.getStringExtra("ID_AUDITORIA")
            if (id != null) {
                mIduditoria = id
            }
            mAuditoria = i
            setText(mAuditoria)
        }
    }

    private fun initConst() {
        mViewModel = ViewModelProvider(
            this, AuditoriaViewModel2.Auditoria2ViewModelFactory(
                AuditoriaRepository()
            )
        )[AuditoriaViewModel2::class.java]
        mBinding.editAuditoriaFinish.requestFocus()
        mSons = CustomMediaSonsMp3()
        mAlertDialog = CustomAlertDialogCustom()
        mBinding.editQnt.setText(mAuditoria.quantidade.toString())
        setupChangedEdit()
    }

    private fun setupChangedEdit() {
        /**EDITANDO MANUALMENTE A QUANTIDADE -->*/
        mBinding.editQnt.doAfterTextChanged { newTxt ->
            if (newTxt.isNullOrEmpty() || newTxt.toString() == "") {
                mBinding.editQnt.setText("0")
            } else {
                if (newTxt.first().toString() == "0" && newTxt.length > 1) {
                    val txtEdit = newTxt.removeRange(0, 1)
                    mBinding.editQnt.setText(txtEdit)
                }
                mBinding.editQnt.setSelection(mBinding.editQnt.length())
            }
        }
        /**BUTTON SIMPLES ADD ++ -->*/
        mBinding.buttonAddAuditoria.setOnClickListener {
            var text = mBinding.editQnt.text.toString().toInt()
            text += 1
            mBinding.editQnt.setText(text.toString())
        }
        /**BUTTON SIMPLES REMOVER -->*/
        mBinding.buttonRemoveAuditoria.setOnClickListener {
            var text = mBinding.editQnt.text.toString().toInt()
            text -= 1
            if (text <= 0) {
                mBinding.editQnt.setText("0")
                mBinding.editQnt.setSelection(mBinding.editQnt.length())
            } else {
                mBinding.editQnt.setSelection(mBinding.editQnt.length())
                mBinding.editQnt.setText(text.toString())
            }
        }
    }

    private fun clickButtonQnt() {
        mBinding.buttonAlterarQnt.setOnClickListener {
            if (mBinding.linearAlterarQnt.visibility == View.VISIBLE) {
                mBinding.linearAlterarQnt.visibility = View.GONE
            } else {
                mBinding.linearAlterarQnt.visibility = View.VISIBLE
            }
        }
        //BUtton LImpar -->
        mBinding.buttonLimpar.setOnClickListener {
            mBinding.editQnt.setText(mAuditoria.quantidade.toString())
        }
    }

    private fun initEdit() {
        mBinding.editAuditoriaFinish.extensionSetOnEnterExtensionCodBarrasString { qrCode ->
            if (qrCode.isEmpty()) {
                toastError(this, "Campo vazio!")
            } else {
                sendScan(itemAuditoria = mAuditoria, qrCode)
            }
        }
    }

    private fun sendScan(itemAuditoria: ResponseFinishAuditoriaItem, scan: String) {
        try {
            if (scan.isNotEmpty()) {
                if (scan == mAuditoria.codBarrasEndereco) {
                    sendScanApi(item = itemAuditoria)
                } else {
                    Log.e(
                        "ERROR",
                        "CÓDIGO BIPADO: $scan || (NÃO) contem na lista"
                    )
                    mAlertDialog.alertMessageErrorSimplesAction(
                        this,
                        "Endereço não encontrado ou não Contido na auditoria selecionada!",
                        action = { clearEdit() }
                    )
                    mBinding.editAuditoriaFinish.requestFocus()
                }
            } else {
                Toast.makeText(this, "Campo Vazio!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        } finally {
            clearEdit()
        }
    }

    private fun sendScanApi(item: ResponseFinishAuditoriaItem) {
        try {
            val body = BodyAuditoriaFinish(
                mIduditoria.toInt(),
                item.estante,
                item.idEndereco.toString(),
                mBinding.editQnt.text.toString()
            )
            mViewModel.postItens(body = body)
        } catch (e: Exception) {
            mSons.somError(this)
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearEdit() {
        mBinding.editAuditoriaFinish.apply {
            setText("")
            text!!.clear()
            requestFocus()
        }
    }

    private fun setText(item: ResponseFinishAuditoriaItem) {
        mBinding.endVisual.text = item.enderecoVisual
        mBinding.grade.text = item.codigoGrade
        mBinding.sku.text = item.sku
        mBinding.quantidade.text = item.quantidade.toString()
    }

    private fun setupDialogShow() {
        mBinding.toolbarAuditoriFinish.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            title = mAuditoria.enderecoVisual
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
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            Log.e(TAG, "SCAN FINALIZAÇÃO AUDITORIA - > $scanData")
            sendScan(mAuditoria, scanData.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}