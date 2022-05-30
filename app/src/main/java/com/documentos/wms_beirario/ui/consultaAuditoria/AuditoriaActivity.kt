package com.documentos.wms_beirario.ui.consultaAuditoria

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.ActivityAuditoriaBinding
import com.documentos.wms_beirario.ui.consultaAuditoria.adapter.AuditoriaAdapter_01
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*

class AuditoriaActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityAuditoriaBinding
    private lateinit var mDialog: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mAdapter: AuditoriaAdapter_01

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityAuditoriaBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        setupToolbar()
        initConst()
        setupRv()
        setupEdit()
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
        mAdapter = AuditoriaAdapter_01()
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
                mSucessToastExtension(this, "OK")
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)

    }
}