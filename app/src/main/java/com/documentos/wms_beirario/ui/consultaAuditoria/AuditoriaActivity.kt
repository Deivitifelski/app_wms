package com.documentos.wms_beirario.ui.consultaAuditoria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.ActivityAuditoriaBinding
import com.documentos.wms_beirario.repository.consultaAuditoria.AuditoriaRepository
import com.documentos.wms_beirario.ui.consultaAuditoria.adapter.AuditoriaAdapter_01
import com.documentos.wms_beirario.ui.consultaAuditoria.viewModel.AuditoriaViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

class AuditoriaActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityAuditoriaBinding
    private lateinit var mDialog: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mAdapter: AuditoriaAdapter_01
    private lateinit var mViewModel: AuditoriaViewModel

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
    }

    override fun onResume() {
        super.onResume()
        mBinding.progressAuditoria.isVisible = false
    }

    private fun setupToolbar() {
        mBinding.toolbarAuditoria.apply {
            subtitle = getVersion()
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
            this, AuditoriaViewModel.Auditoria_1ViewModelFactory(
                AuditoriaRepository()
            )
        )[AuditoriaViewModel::class.java]

        mAdapter = AuditoriaAdapter_01 { itemClick ->
            val intent = Intent(this, AuditoriaActivity2::class.java)
            extensionSendActivityanimation()
            startActivity(intent)
        }

        mDialog = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    private fun setupEdit() {
        mBinding.editAuditoria01.extensionSetOnEnterExtensionCodBarras {
            if (mBinding.editAuditoria01.text.isNullOrEmpty()) {
                mBinding.editLayoutNumAuditoria.shake {
                    mErroToastExtension(this, "Preencha o campo!")
                }
            } else {
                sendData(mBinding.editAuditoria01.text.toString())
            }
            clearEdit()
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
            if (sucess == null) {
                mErroToastExtension(this, "Auditoria não encontrada!")
            } else {
                mAdapter.update(sucess)
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
    }


    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)

    }
}