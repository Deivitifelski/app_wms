package com.documentos.wms_beirario.ui.configuracoes

import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import com.documentos.wms_beirario.databinding.ActivityControlBinding
import com.documentos.wms_beirario.utils.extensions.onBackTransition
import com.example.br_coletores.models.services.PrinterConnection

class ControlActivity : BaseActivity() {
    var TAG = "control"

    private var velocidade = "1"
    private var temperatura = "20"

    companion object {
        var settings = ""
    }

    private lateinit var mBinding: ActivityControlBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityControlBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.sbVelocidade.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seek: SeekBar,
                progress: Int, fromUser: Boolean
            ) {
                mBinding.tvVelocidade.text = mBinding.sbVelocidade.progress.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.d(TAG, "comecou a mudar" + mBinding.sbVelocidade.progress.toString())
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                velocidade = mBinding.sbVelocidade.progress.toString()
            }
        }
        )

        mBinding.sbTemperatura.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seek: SeekBar,
                progress: Int, fromUser: Boolean
            ) {
                mBinding.tvTemperatura.text = mBinding.sbTemperatura.progress.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.d(TAG, "comecou a mudar" + mBinding.sbTemperatura.progress.toString())
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                temperatura = mBinding.sbTemperatura.progress.toString()
            }
        }
        )

        mBinding.btSalvarConfig.setOnClickListener { changePrinterSettings() }
    }

    override fun onResume() {
        super.onResume()
        setupToolbar()
    }

    private fun setupToolbar() {
        mBinding.toolbarTemperatura.setOnClickListener {
            onBackTransition()
        }
    }

    private fun changePrinterSettings() {
        val printerConnection = PrinterConnection()
        settings = "^XA\n" +
                "^PR$velocidade,$velocidade,$velocidade\n" + "~SD$temperatura\n" + "^XZ"
        printerConnection.printZebra(settings, MenuActivity.applicationPrinterAddress)

        Log.d(TAG, "mandou pra impressora" + settings)
        showAlertDialogWithAutoDismiss("Configurações salvas com sucesso.")
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}