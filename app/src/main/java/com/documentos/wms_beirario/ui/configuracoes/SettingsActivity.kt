package com.documentos.wms_beirario.ui.configuracoes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.documentos.wms_beirario.databinding.ActivitySettingsBinding
import com.documentos.wms_beirario.ui.bluetooh.BluetoohPrinterActivity
import com.documentos.wms_beirario.ui.bluetooh.BluettohLIbrary
import com.documentos.wms_beirario.ui.configuracoes.temperature.ControlActivity
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension


class SettingsActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivitySettingsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        setupToolbar()
        click()
    }

    private fun setupToolbar() {
        mBinding.toolbarSetting.apply {
            setNavigationOnClickListener {
                onBackTransitionExtension()
            }
        }
    }

    private fun click() {
        //ENVIANDO PARA CONFIGURAÇAO TEMPERATURA -->
        mBinding.buttonTemperatura.setOnClickListener {
            startActivity(Intent(this, ControlActivity::class.java))
            extensionSendActivityanimation()
        }

        mBinding.buttonPrinter.setOnClickListener {
            startActivity(Intent(this, BluetoohPrinterActivity::class.java))
            extensionSendActivityanimation()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}