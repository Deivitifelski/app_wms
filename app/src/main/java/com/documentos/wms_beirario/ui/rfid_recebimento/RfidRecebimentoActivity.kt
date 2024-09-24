package com.documentos.wms_beirario.ui.rfid_recebimento

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityRfidRecebimentoBinding

class RfidRecebimentoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRfidRecebimentoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRfidRecebimentoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}