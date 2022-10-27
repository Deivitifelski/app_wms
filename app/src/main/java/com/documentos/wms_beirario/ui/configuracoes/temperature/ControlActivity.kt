package com.documentos.wms_beirario.ui.configuracoes.temperature

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.lifecycle.lifecycleScope
import com.documentos.appwmsbeirario.ui.configuracoes.temperature.BaseActivity
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityControlBinding
import com.documentos.wms_beirario.ui.bluetooh.BluetoohPrinterActivity
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ControlActivity : BaseActivity() {
    private var mSpeed: String = "5"
    private var mTemperature: String = "14.0"
    private var service: BluetoothService? = null
    private lateinit var writer: BluetoothWriter

    companion object {
        var mSettings = ""
    }

    private lateinit var mBinding: ActivityControlBinding
    private lateinit var mPrinter: PrinterConnection
    private lateinit var mAlert: CustomAlertDialogCustom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityControlBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mAlert = CustomAlertDialogCustom()
        initProgress()
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
                val seekBar = "${mBinding.sbTemperatura.progress.toString().toInt()}.0"
                mBinding.tvTemperatura.text = seekBar
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.d("TEMPERATURA", "comecou a mudar" + mBinding.sbTemperatura.progress.toString())
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                mTemperature = "${mBinding.sbTemperatura.progress}.0"
            }
        }
        )

        mBinding.btSalvarConfig.setOnClickListener { changePrinterSettings() }
    }

    override fun onStart() {
        super.onStart()
        verificationsBluetooh()
    }

    override fun onResume() {
        super.onResume()
        mPrinter = PrinterConnection()
    }

    /**VERIFICA SE JA TEM IMPRESSORA CONECTADA!!--->*/
    private fun verificationsBluetooh() {
        if (BluetoohPrinterActivity.STATUS != "CONNECTED") {
            mAlert.alertSelectPrinter(this, activity = this)
        } else {
            setupPrinterConect()
            initConfigPrinter()
        }
    }

    private fun initConfigPrinter() {
        service = BluetoothClassicService.getDefaultInstance()
        writer = BluetoothWriter(service)
    }

    private fun initProgress() {
        if (SetupNamePrinter.mSpeed.isNotEmpty() || SetupNamePrinter.mTemp.isNotEmpty()) {
            mSpeed = SetupNamePrinter.mSpeed
            mTemperature = SetupNamePrinter.mTemp
            mBinding.tvTemperatura.text = "$mTemperature.0"
            mBinding.tvVelocidade.text = mSpeed
            mBinding.sbVelocidade.progress = mSpeed.toInt()
            mBinding.sbTemperatura.progress = mTemperature.toInt()
        } else {
            mBinding.tvTemperatura.text = mTemperature
            mBinding.tvVelocidade.text = mSpeed
            mTemperature = "${mBinding.sbTemperatura.progress}.0"
            mSpeed = mBinding.sbVelocidade.progress.toString()
        }
    }

    private fun setupPrinterConect() {
        if (BluetoohPrinterActivity.STATUS != "CONNECTED") {
            mBinding.txtInfSetting.text = getString(R.string.no_Printer_connected)
            mBinding.btSalvarConfig.isEnabled = false
        } else {
            mBinding.btSalvarConfig.isEnabled = true
            mBinding.txtInfSetting.apply {
                text = getString(
                    R.string.connected_with_printer,
                    BluetoohPrinterActivity.NAME_DEVICE_CONNECTED
                )
                setTextColor(getColor(R.color.green_verde_padrao))
            }
        }
    }


    private fun setupToolbar() {
        mBinding.toolbarTemperatura.setOnClickListener {
            onBackTransitionExtension()
        }
    }

    private fun changePrinterSettings() {
        SetupNamePrinter.mTemp = mBinding.sbTemperatura.progress.toString()
        SetupNamePrinter.mSpeed = mBinding.sbVelocidade.progress.toString()
        mSettings = "^XA\n" +
                "^PR$mSpeed,$mSpeed,$mSpeed\n" + "~SD$mTemperature\n" + "^XZ"

        try {
            lifecycleScope.launch(Dispatchers.Default) {
                writer.write(mSettings)
            }
            CustomSnackBarCustom().snackBarSucess(
                this@ControlActivity,
                mBinding.root,
                getString(R.string.salvas_com_sucess)
            )
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