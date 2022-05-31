package com.documentos.wms_beirario.ui.consultaAuditoria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.ActivityAuditoriaBinding
import com.documentos.wms_beirario.model.auditoria.AuditoriaResponse
import com.documentos.wms_beirario.ui.consultaAuditoria.adapter.AuditoriaAdapter_01
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

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
        setupButton()
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
                if (mBinding.editAuditoria01.text.toString() == "001") {
                    mBinding.progressAuditoria.isVisible = true
                    Handler(Looper.getMainLooper()).postDelayed({
                        mBinding.progressAuditoria.isVisible = false
                        val mList = mutableListOf<AuditoriaResponse>()
                        for (i in 0..10) {
                            mList.add(AuditoriaResponse(i, "00$i"))
                        }
                        mAdapter.update(mList)
                        clearEdit()
                        hideKeyExtensionActivity(mBinding.editAuditoria01)
                    }, 2000)

                } else {
                    mBinding.progressAuditoria.isVisible = true
                    Handler(Looper.getMainLooper()).postDelayed({
                        mBinding.progressAuditoria.isVisible = false
                        val mList = mutableListOf<AuditoriaResponse>()
                        for (i in 2..14) {
                            mList.add(AuditoriaResponse(i, "00$i"))
                        }
                        mAdapter.update(mList)
                        clearEdit()
                    }, 2000)
                    UIUtil.hideKeyboard(this)
                }
            }
        }
    }

    private fun setupButton() {
        mBinding.butonConsultarAuditoria.setOnClickListener {
            mBinding.progressAuditoria.isVisible = true
            Handler(Looper.getMainLooper()).postDelayed({
                mBinding.progressAuditoria.isVisible = false
                val mList = mutableListOf<AuditoriaResponse>()
                for (i in 2..14) {
                    mList.add(AuditoriaResponse(i, "00$i"))
                }
                mAdapter.update(mList)
                clearEdit()
            }, 2000)
            UIUtil.hideKeyboard(this)
        }
    }

    private fun clearEdit() {
        mBinding.editAuditoria01.text?.clear()
        mBinding.editAuditoria01.setText("")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)

    }
}