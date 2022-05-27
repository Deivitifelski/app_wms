package com.documentos.wms_beirario.ui.consultaAuditoria

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.documentos.wms_beirario.databinding.ActivityAuditoriaBinding
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersion

class AuditoriaActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityAuditoriaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityAuditoriaBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        setupToolbar()
    }

    private fun setupToolbar() {
        mBinding.toolbarAuditoria.apply {
            subtitle = getVersion()
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)

    }
}