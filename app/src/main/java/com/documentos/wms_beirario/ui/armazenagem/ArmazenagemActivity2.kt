package com.documentos.wms_beirario.ui.armazenagem

import ArmazenagemResponse
import android.content.BroadcastReceiver
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.databinding.ActivityArmazenagem2Binding
import com.documentos.wms_beirario.model.armazenagem.ArmazemRequestFinish
import com.documentos.wms_beirario.repository.armazenagem.ArmazenagemRepository
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.alertEditText
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.registerDataWedgeReceiver
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

class ArmazenagemActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityArmazenagem2Binding
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mViewModel: ArmazenagemViewModel
    private var isLoanding = false
    private lateinit var dataIntent: ArmazenagemResponse
    private lateinit var dataWedgeReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArmazenagem2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        getDataIntent()
        initViewModel()
        setToolbar()
        initConst()
        clearEdit()
        setupEdit()
        setObservables()
    }

    override fun onResume() {
        super.onResume()
        dataWedgeReceiver = registerDataWedgeReceiver { barcodeData ->
            sendData(barcodeData)
            clearEdit()
        }
    }

    private fun setToolbar() {
        binding.toolbarArmazenagem2.apply {
            CustomMediaSonsMp3().somClick(this@ArmazenagemActivity2)
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = getVersionNameToolbar()
        }
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            ArmazenagemViewModel.ArmazenagemViewModelFactory(ArmazenagemRepository())
        )[ArmazenagemViewModel::class.java]
    }

    private fun getDataIntent() {
        try {
            if (intent != null) {
                dataIntent = intent.getParcelableExtra("ARMAZEM_SEND")!!
                binding.txtDestinoApi.text = dataIntent.visualEnderecoDestino
                binding.txtOrigemApi.text = dataIntent.visualEnderecoOrigem
            } else {
                mAlert.alertErroInitBack(this, this, getString(R.string.error_back_activity))
            }
        } catch (e: Exception) {
            mAlert.alertErroInitBack(this, this, e.toString())
        }
    }

    private fun setupEdit() {
        binding.imageCliqueFinalizar.setOnClickListener {
            alertEditText(
                title = "Atenção",
                subTitle = "Digite o endereço para finalizar",
                actionNo = {},
                actionYes = { data ->
                    sendData(data)
                    UIUtil.hideKeyboard(this)
                })
        }
    }


    private fun sendData(mQrcode: String) {
        isLoanding = true
        mViewModel.postFinish(
            ArmazemRequestFinish(
                idTarefa = dataIntent.id,
                enderecoLeitura = mQrcode,
                enderecoOrigem = dataIntent.idEnderecoOrigem.toString()
            )
        )
        clearEdit()
    }

    private fun setObservables() {
        mViewModel.progressFinalizar.observe(this) {
            clearEdit()
            binding.progressArmazenagemFinalizar.isVisible = it
        }
        mViewModel.mSucessShow2.observe(this) {
            isLoanding = false
            clearEdit()
            vibrateExtension(500)
            mAlert.alertSucessFinishBack(this, "Armazenado com sucesso!")
        }

        mViewModel.mProgressInitShow.observe(this) { progress ->
            clearEdit()
            binding.progressArmazenagemFinalizar.isVisible = progress
        }

        mViewModel.mErrorHttpShow.observe(this) { error ->
            isLoanding = false
            clearEdit()
            mAlert.alertMessageErrorSimples(this, error)
        }

        mViewModel.mErrorAllShow.observe(this) { error ->
            isLoanding = false
            clearEdit()
            mAlert.alertMessageErrorSimples(this, error)
        }
    }

    private fun initConst() {
        binding.editTxtArmazenagem02.requestFocus()
        binding.progressArmazenagemFinalizar.isVisible = false
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }


    private fun clearEdit() {
        binding.editTxtArmazenagem02.setText("")
        binding.editTxtArmazenagem02.text!!.clear()
        binding.editFocus.requestFocus()
        binding.editFocus.setText("")
    }


    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(dataWedgeReceiver)
    }

}