package com.documentos.wms_beirario.ui.documentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityDocumentationBinding
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation

class DocumentationActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityDocumentationBinding
    private lateinit var mOnBoardAdapter: OnBoardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityDocumentationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        intDocumentation()

    }

    private fun intDocumentation() {
        val list = mutableListOf<Int>()
        for (i in 0..10) {
            list.add(R.drawable.beirariologo)
        }
        mOnBoardAdapter = OnBoardAdapter(list)
        mBinding.viewPager.adapter = mOnBoardAdapter
        mBinding.indicatorPag.setViewPager(mBinding.viewPager)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}