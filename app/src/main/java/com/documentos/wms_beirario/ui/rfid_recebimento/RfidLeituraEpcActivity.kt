package com.documentos.wms_beirario.ui.rfid_recebimento

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityRfidLeituraEpcBinding

class RfidLeituraEpcActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRfidLeituraEpcBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRfidLeituraEpcBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}