package com.documentos.wms_beirario.ui.documentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.documentos.wms_beirario.databinding.ActivityDocumentationBinding
import com.documentos.wms_beirario.model.documentacao.ListImagens
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation

class DocumentationActivity() : AppCompatActivity() {

    private lateinit var mBinding: ActivityDocumentationBinding
    private lateinit var mOnBoardAdapter: OnBoardAdapter
    private lateinit var mList: ListImagens

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityDocumentationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        getImagens()
        intDocumentation()

    }

    private fun getImagens() {
        if (intent != null) {
            val imagens = intent.getSerializableExtra("LISTA_IMAGENS_DOC") as ListImagens
            mList = imagens
        }
    }


    private fun intDocumentation() {
        try {
            mOnBoardAdapter = OnBoardAdapter(mList)
            mBinding.viewPager.adapter = mOnBoardAdapter
            mBinding.indicatorPag.setViewPager(mBinding.viewPager)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Erro ao receber imagens, entre em contato com suporte!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}