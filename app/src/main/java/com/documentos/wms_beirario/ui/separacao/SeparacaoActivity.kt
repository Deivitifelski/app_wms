package com.documentos.wms_beirario.ui.separacao

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.documentos.wms_beirario.databinding.ActivitySeparacaoBinding

class SeparacaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeparacaoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySeparacaoBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }


}