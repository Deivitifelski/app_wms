package com.documentos.wms_beirario.ui.consultaAuditoria

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.ActivityAuditoriaBinding
import com.documentos.wms_beirario.repository.consultaAuditoria.AuditoriaRepository
import com.documentos.wms_beirario.ui.consultaAuditoria.DialogFragment.DialogFragmentAuditoriaEstantes
import com.documentos.wms_beirario.ui.consultaAuditoria.adapter.AuditoriaAdapter1
import com.documentos.wms_beirario.ui.consultaAuditoria.viewModel.AuditoriaViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

class AuditoriaActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityAuditoriaBinding
    private lateinit var mDialog: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mAdapter: AuditoriaAdapter1
    private lateinit var mViewModel: AuditoriaViewModel
    private var mIdAuditoria: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityAuditoriaBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        setupToolbar()
        initConst()
        setupRv()
        setupEdit()
        setupButton()
        setObservable()
        buttonEnable(mBinding.butonConsultarAuditoria, false)
    }

    override fun onResume() {
        super.onResume()
        mBinding.progressAuditoria.isVisible = false
        mBinding.editAuditoria01.requestFocus()
    }

    private fun setupToolbar() {
        mBinding.toolbarAuditoria.apply {
            subtitle = getVersionNameToolbar()
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setupRv() {
        mBinding.rvAuditoria01.apply {
            layoutManager = LinearLayoutManager(this@AuditoriaActivity)
            adapter = mAdapter
        }
    }

    private fun initConst() {
        mViewModel = ViewModelProvider(
            this, AuditoriaViewModel.Auditoria1ViewModelFactory(
                AuditoriaRepository()
            )
        )[AuditoriaViewModel::class.java]

        mAdapter = AuditoriaAdapter1 { itemClick ->
            mIdAuditoria = itemClick.id
            mViewModel.getReceiptEstantes2(mIdAuditoria.toString())
        }

        mDialog = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    private fun setupEdit() {
        mBinding.editAuditoria01.changedEditText { buttonEnableChanged(mBinding.editAuditoria01.text.toString()) }
        mBinding.editAuditoria01.extensionSetOnEnterExtensionCodBarras {
            if (mBinding.editAuditoria01.text.isNullOrEmpty()) {
                mBinding.editLayoutNumAuditoria.shake {
                    toastError(this, "Preencha o campo!")
                }
            } else {
                sendData(mBinding.editAuditoria01.text.toString())
            }
            clearEdit()
        }
    }

    private fun buttonEnableChanged(toString: String) {
        if (toString.isEmpty()) {
            buttonEnable(mBinding.butonConsultarAuditoria, visibility = false)
        } else {
            buttonEnable(mBinding.butonConsultarAuditoria, visibility = true)
        }
    }


    private fun setupButton() {
        mBinding.butonConsultarAuditoria.setOnClickListener {
            mBinding.progressAuditoria.isVisible = true
            sendData(mBinding.editAuditoria01.text.toString())
            UIUtil.hideKeyboard(this)
        }
    }

    private fun sendData(idString: String) {
        mViewModel.getReceipt1(idString)
        clearEdit()
    }

    private fun clearEdit() {
        mBinding.editAuditoria01.text?.clear()
        mBinding.editAuditoria01.setText("")
    }

    private fun setObservable() {
        mViewModel.mSucessAuditoriaShow.observe(this) { sucess ->
            try {
                if (sucess.isEmpty()) {
                    toastError(this, "Erro\nAuditoria não encontrada!")
                } else {
                    mAdapter.update(sucess)
                }
            } catch (e: Exception) {
                toastError(this, "Error ao receber lista!")
            }
        }

        mViewModel.mErrorAuditoriaShow.observe(this) { error ->
            mDialog.alertMessageErrorSimples(this, error)
        }

        mViewModel.mErrorAllShow.observe(this) { error ->
            mDialog.alertMessageErrorSimples(this, error)
        }

        mViewModel.mValidProgressEditShow.observe(this) { progress ->
            mBinding.progressAuditoria.isVisible = progress
        }
        /**RESPONSE ESTANTES -->*/
        mViewModel.mSucessAuditoriaEstantesShow.observe(this) { sucessEstantes ->
            DialogFragmentAuditoriaEstantes(sucessEstantes, mIdAuditoria).show(
                supportFragmentManager,
                "ESTANTES"
            )
        }
        mViewModel.mErrorAuditoriaEstanteshow.observe(this) { errorEstantes ->
            mDialog.alertMessageErrorSimples(this, errorEstantes)
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)

    }
}