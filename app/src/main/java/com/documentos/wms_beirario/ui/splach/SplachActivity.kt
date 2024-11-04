package com.documentos.wms_beirario.ui.splach

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.documentos.wms_beirario.databinding.SplachActivityBinding
import com.documentos.wms_beirario.ui.login.LoginActivity
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.getVersion

class SplachActivity : AppCompatActivity() {

    private lateinit var mBinding: SplachActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = SplachActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        /**FUNCAO ONDE E CONFIG O TIME QUE A SPLACH IRA FICAR NO INICIO DO APP -->*/
        delaySplach(delay = 3200)
    }

    private fun delaySplach(delay: Long) {
        mBinding.txtVersion.text = "Versão \n${getVersion()}"
        CustomMediaSonsMp3().somInit(this)
        Handler(Looper.getMainLooper()).postDelayed(this::startaLogin, delay)
    }

    private fun startaLogin() {
        startActivity(Intent(applicationContext, LoginActivity::class.java))
    }
}