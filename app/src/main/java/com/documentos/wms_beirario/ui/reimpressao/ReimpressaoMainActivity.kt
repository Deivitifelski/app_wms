package com.documentos.wms_beirario.ui.reimpressao

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.documentos.wms_beirario.databinding.ActivityReimpressaoMainBinding
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.ui.reimpressao.porNf.ReimpressaoNfActivity
import com.documentos.wms_beirario.ui.reimpressao.porNumPedido.ReimpressaoNumPedidoActivity
import com.documentos.wms_beirario.ui.reimpressao.porNumRequest.ReimpressaoNumRequestActivity
import com.documentos.wms_beirario.ui.reimpressao.porNumSerie.ReimpressaoNumSerieActivity
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersion

class ReimpressaoMainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityReimpressaoMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityReimpressaoMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        clickButtons()
        setupToolbar()
        observConectPrint()
    }

    private fun observConectPrint() {
        if (SetupNamePrinter.applicationPrinterAddress.isEmpty()){
            CustomAlertDialogCustom().alertSelectPrinter(this)
        }
    }

    private fun setupToolbar() {
        mBinding.toolbar4.apply {
            subtitle = "[${getVersion()}]"
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun clickButtons() {
        mBinding.buttonNumSerie.setOnClickListener {
            startActivity(Intent(this, ReimpressaoNumSerieActivity::class.java))
            extensionSendActivityanimation()
        }

        mBinding.buttonNumPedido.setOnClickListener {
            startActivity(Intent(this, ReimpressaoNumPedidoActivity::class.java))
            extensionSendActivityanimation()
        }

        mBinding.buttonPorRequisicao.setOnClickListener {
            startActivity(Intent(this, ReimpressaoNumRequestActivity::class.java))
            extensionSendActivityanimation()
        }

        mBinding.buttonPorNf.setOnClickListener {
            startActivity(Intent(this, ReimpressaoNfActivity::class.java))
            extensionSendActivityanimation()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

}