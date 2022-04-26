package com.documentos.wms_beirario.ui.reimpressao

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.documentos.wms_beirario.databinding.ActivityReimpressaoBinding

class ReimpressaoActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityReimpressaoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityReimpressaoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
}