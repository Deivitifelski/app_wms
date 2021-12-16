package com.example.coletorwms.view.impressora

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.FragmentTemperatureBinding
import com.documentos.wms_beirario.ui.configuracoes.MenuActivity
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.AppExtensions

class ControlActivity() : AppCompatActivity() {
    var TAG = "CONTROL ------------->"

    private var velocidade = "1"
    private var temperatura = "20"

    companion object {
        var settings = ""
    }

    private lateinit var mBinding: FragmentTemperatureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = FragmentTemperatureBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        AppExtensions.onBackToolbar(this, mBinding.toolbarTempe)
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

        mBinding.btSalvarConfig.setOnClickListener {
            changePrinterSettings()
            Handler().postDelayed({
                onBackPressed()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }, 3000)
        }
    }

    private fun changePrinterSettings() {
        val printerConnection = PrinterConnection()
        settings = "^XA\n" +
                "^PR$velocidade,$velocidade,$velocidade\n" + "~SD$temperatura\n" + "^XZ"
        printerConnection.printZebra(settings, MenuActivity.applicationPrinterAddress)

        Log.d(TAG, "mandou pra impressora" + settings)
        CustomAlertDialogCustom().alertMessageSucess(this, "Configurações salvas com sucesso.")
    }
}