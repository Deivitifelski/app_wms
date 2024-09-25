package com.documentos.wms_beirario.ui.rfid_recebimento.detalhesEpc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityDetalheCodigoEpcBinding

class DetalheCodigoEpcActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalheCodigoEpcBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalheCodigoEpcBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}