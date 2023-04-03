package com.documentos.wms_beirario.ui.boardingConference

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityBoardingconferenceBinding

class BoardingconferenceActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityBoardingconferenceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBoardingconferenceBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
}