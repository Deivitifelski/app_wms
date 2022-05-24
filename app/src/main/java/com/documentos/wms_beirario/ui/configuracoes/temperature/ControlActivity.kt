package com.documentos.wms_beirario.ui.configuracoes.temperature

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.lifecycle.lifecycleScope
import com.documentos.appwmsbeirario.ui.configuracoes.temperature.BaseActivity
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityControlBinding
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ControlActivity : BaseActivity() {
    private var mSpeed = "1"
    private var mTemperature = "20"

    companion object {
        var mSettings = ""
    }

    private lateinit var mBinding: ActivityControlBinding
    private lateinit var mDialog: Dialog
    private lateinit var mPrinter: PrinterConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityControlBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mDialog = CustomAlertDialogCustom().progress(this, getString(R.string.salving_settings))
        mDialog.hide()
        setupPrinterConect()
        setupToolbar()
        mBinding.sbVelocidade.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seek: SeekBar,
                progress: Int, fromUser: Boolean
            ) {
                mBinding.tvVelocidade.text = mBinding.sbVelocidade.progress.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.d("VELOCIDADE", "comecou a mudar" + mBinding.sbVelocidade.progress.toString())
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                mSpeed = mBinding.sbVelocidade.progress.toString()
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
                Log.d("TEMPERATURA", "comecou a mudar" + mBinding.sbTemperatura.progress.toString())
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                mTemperature = mBinding.sbTemperatura.progress.toString()
            }
        }
        )

        mBinding.btSalvarConfig.setOnClickListener { changePrinterSettings() }
    }

    private fun setupPrinterConect() {
        if (SetupNamePrinter.mNamePrinterString.isEmpty()) {
            mBinding.txtInfSetting.text = getString(R.string.no_Printer_connected)
            mBinding.btSalvarConfig.isEnabled = false
        } else {
            mBinding.btSalvarConfig.isEnabled = true
            mBinding.txtInfSetting.text =
                getString(
                    R.string.connected_with_printer,
                    SetupNamePrinter.mNamePrinterString
                )
        }
    }


    private fun setupToolbar() {
        mBinding.toolbarTemperatura.setOnClickListener {
            onBackTransitionExtension()
        }
    }

    private fun changePrinterSettings() {
        mPrinter = PrinterConnection(SetupNamePrinter.mNamePrinterString)
        mSettings = "^XA\n" +
                "  ^POI\n" +
                "  ^ Logo\n" +
                "  ^FWB\n" +
                "  ^FO45,30^A0,35,25^FB430,7,,L,0^FDETIQUETA DE TESTE DE TEMPERATURA^FS\n" +
                "  ^FWN\n" +
                "  ^ Codigo de barras\n" +
                "  ^FO70,590^BY3,3,0,50^BC,160,Y,N,N,A^FD000000000000000000000000000000000^FS\n" +
                "  ^ QRcode\n" +
                "  ^FO600,25^BY4,2.0,10^BQN,2,7^FDMM,A000000000000000000000000000000000^FS\n" +
                "  ^ Linha cabeçalho\n" +
                "  ^FO107,15^GB0,545,3^FS\n" +
                "  ^ Black NF\n" +
                "  ^LRY^FO115,355^GB0,120,48^FS^LRN\n" +
                "  ^LRY^FO167,355^GB0,210,130^FS^LRN\n" +
                "  ^ Quadrado destinatario\n" +
                "  ^Top\n" +
                "  ^FO310,25^GB0,545,6^FS\n" +
                "  ^Right\n" +
                "  ^FO310,25^GB270,0,6^FS\n" +
                "  ^Middle\n" +
                "  ^FO455,25^GB0,545,3^FS\n" +
                "  ^Left\n" +
                "  ^FO310,570^GB275,0,6^FS\n" +
                "  ^Bottom\n" +
                "  ^FO580,25^GB0,545,6^FS\n" +
                "  ^FWB\n" +
                "  ^FO130,380^A0,30,30^FR^FDRODO^FS\n" +
                "  ^FO173,395^A0,25,25^FR^FDNOTA FISCAL^FS\n" +
                "  ^FO125,80^AO,35,20^FB265,1,,L,0^FD ${"Temp -> " + mTemperature} ^FS\n" +
                "  ^FO160,80^AO,15,18^FB165,1,,L,0^FD ^FS\n" +
                "  ^FO180,80^AO,15,18^FB165,1,,L,0^FD ^FS\n" +
                "  ^FO200,80^AO,15,18^FB165,1,,L,0^FD ^FS\n" +
                "  ^FO220,80^AO,15,18^FB165,1,,L,0^FD ^FS\n" +
                "  ^FO240,80^AO,15,18^FB165,1,,L,0^FD ^FS\n" +
                "  ^FO260,80^AO,35,20^FB265,1,,L,0^FD ${"Veloc -> " + mSpeed} ^FS\n" +
                "  ^FO197,390^A0,45,50^FR^FD000000^FS\n" +
                "  ^FO235,395^A0,35,45^FR^FD0/0^FS\n" +
                "  ^FO272,370^A0,25,29^FR^FD^FS\n" +
                "  ^FO325,405^AD,20,10^FDDestinatario:^FS\n" +
                "  ^FO345,25^AD,20,10^FB535,7,,L,0^FR^FD^FS\n" +
                "  ^FO465,20^AO,20,10^FB540,7,,L,0^FDPed.Cli.:^FS\n" +
                "  ^FO490,100^A0,30,17^FB140,7,,L,0^FDAgen: ^FS\n" +
                "  ^FO490,277^A0,27,22^FB285,7,,L,0^FDDIST: ^FS\n" +
                "  ^FO525,25^A0,65,20^FB535,7,,L,0^FDSKU:^FS\n" +
                "  ^FO522,35^A0,19,18^FB480,7,,L,0^FD^FS\n" +
                "  ^FO595,30^A0,30,35^FB535,7,,L,0^FDREM: 0000000 0/0^FS\n" +
                "  ^FO635,30^A0,50,56^FB535,7,,L,0^FDPED: 00000000^FS\n" +
                "  ^FO725,250^A0,20,35^FB315,7,,L,0^FR^FDEMISSAO: 00/00/00^FS\n" +
                "  ^FWN\n" +
                "  ^XZ"

        try {
            mDialog.show()
            lifecycleScope.launch {
                delay(800)
                mPrinter.sendZplBluetooth(mSettings, null)
                CustomSnackBarCustom().snackBarSucess(
                    this@ControlActivity,
                    mBinding.root,
                    getString(R.string.salvas_com_sucess)
                )
                mDialog.hide()
            }
        } catch (e: Exception) {
            CustomSnackBarCustom().snackBarErrorAction(
                mBinding.root,
                e.toString()
            )
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}