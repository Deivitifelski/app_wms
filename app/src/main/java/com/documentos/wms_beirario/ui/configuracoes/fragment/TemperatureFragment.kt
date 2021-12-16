package com.documentos.wms_beirario.ui.configuracoes.fragment

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.FragmentTemperatureBinding
import com.documentos.wms_beirario.utils.extensions.onBackTransition
import com.documentos.wms_beirario.ui.configuracoes.MenuActivity
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.example.coletorwms.constants.CustomSnackBarCustom


class TemperatureFragment : Fragment() {

    var TAG = "CONTROL ------------->"
    private var velocidade = "1"
    private var temperatura = "20"

    companion object {
        var settings = ""
    }

    private var mBinding: FragmentTemperatureBinding? = null
    val binding get() = mBinding!!
    private lateinit var mDialog : Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentTemperatureBinding.inflate(layoutInflater)
        setupViews()
        mDialog = CustomAlertDialogCustom().progress(requireContext(),"Salvando as Configurações")
        mDialog.hide()
        return binding.root
    }

    private fun setupViews() {
        /**CONFIGURACAO VELOCIDADE -->*/
        mBinding!!.sbVelocidade.setOnSeekBarChangeListener(
            object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                    mBinding!!.tvVelocidade.text = mBinding!!.sbVelocidade.progress.toString()
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    Log.d("TAG", "comecou a mudar" + mBinding!!.sbVelocidade.progress.toString())
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    velocidade = mBinding!!.sbVelocidade.progress.toString()
                }
            }
        )

        /**CONFIGURACAO TEMPERATURA -->*/
        mBinding!!.sbTemperatura.setOnSeekBarChangeListener(
            object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                    mBinding!!.tvTemperatura.text = mBinding!!.sbTemperatura.progress.toString()
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    Log.d(TAG, "comecou a mudar" + mBinding!!.sbTemperatura.progress.toString())
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    temperatura = mBinding!!.sbTemperatura.progress.toString()
                }
            }
        )

        /**click button salvar -->*/
        mBinding!!.btSalvarConfig.setOnClickListener {
            mDialog.show()
            changePrinterSettings()
            Handler().postDelayed({
                mDialog.hide()
                CustomSnackBarCustom().toastCustomSucess(
                    requireContext(),
                    getString(R.string.salvas_com_sucess)
                )
                requireActivity().onBackTransition()
            }, 3000)
        }
    }

    private fun changePrinterSettings() {
        val printerConnection = PrinterConnection()
        settings = "^XA\n" +
                "^PR$velocidade,$velocidade,$velocidade\n" + "~SD$temperatura\n" + "^XZ"
        printerConnection.printZebra(settings, MenuActivity.applicationPrinterAddress)
        Log.d(TAG, "mandou pra impressora$settings")

    }
}
