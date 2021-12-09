package com.documentos.wms_beirario.ui.etiquetagem

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.documentos.wms_beirario.databinding.ActivityEtiquetagemBinding
import com.documentos.wms_beirario.extensions.onBackTransition

class EtiquetagemActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityEtiquetagemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityEtiquetagemBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

    }


}