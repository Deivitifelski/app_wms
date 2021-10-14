package com.documentos.wms_beirario.ui.armazengem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.documentos.wms_beirario.databinding.ActivityArmazenagemBinding

class ArmazenagemActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityArmazenagemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArmazenagemBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
}