package com.documentos.wms_beirario.ui.volumeMovement

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityVolumeMovementBinding

class VolumeMovementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVolumeMovementBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolumeMovementBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}