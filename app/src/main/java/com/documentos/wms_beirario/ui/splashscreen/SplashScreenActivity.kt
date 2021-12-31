package com.documentos.wms_beirario.ui.splashscreen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.documentos.wms_beirario.databinding.ActivitySplashScreenBinding
import com.documentos.wms_beirario.ui.login.LoginActivity
import com.documentos.wms_beirario.utils.extensions.extensionStartActivity
import com.example.coletorwms.constants.CustomMediaSonsMp3

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //CONFIGURAÇAO INICIAIS -->
        CustomMediaSonsMp3().somInit(this)
        binding.logoWritten.visibility = View.INVISIBLE
        binding.txtWms.visibility = View.INVISIBLE


        //CONFIGURAÇAO DOS DELAYS -->
        Handler(Looper.getMainLooper()).postDelayed({
            binding.logoWritten.visibility = View.VISIBLE
            binding.txtWms.visibility = View.VISIBLE
        }, 1100)

        Handler(Looper.getMainLooper()).postDelayed({
            extensionStartActivity(LoginActivity())
            finish()
        }, 2000)
    }
}