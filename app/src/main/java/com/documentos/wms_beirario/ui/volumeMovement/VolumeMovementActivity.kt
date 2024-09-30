package com.documentos.wms_beirario.ui.volumeMovement

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityVolumeMovementBinding
import com.documentos.wms_beirario.databinding.LayoutCustomFinishMovementAdressBinding
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.BodyCancelMov5
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.RequestBodyFinalizarMov4
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.ResponseMovParesAvulso1
import com.documentos.wms_beirario.repository.movimentacaoentreenderecos.MovimentacaoEntreEnderecosRepository
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.adapter.Adapter1Movimentacao
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.viewmodel.ReturnTaskViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.clearEdit
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarrasString
import com.documentos.wms_beirario.utils.extensions.getVersion
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.somSucess
import com.documentos.wms_beirario.utils.extensions.toastSucess
import java.util.Observable
import java.util.Observer

class VolumeMovementActivity : AppCompatActivity(), Observer {

    private lateinit var binding: ActivityVolumeMovementBinding
    private lateinit var viewModel: ReturnTaskViewModel
    private lateinit var adapterVolumes: Adapter1Movimentacao
    private lateinit var sharedPreferences: CustomSharedPreferences
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private lateinit var dialog: CustomAlertDialogCustom
    private var dialogFinishTask: android.app.AlertDialog? = null
    private var initialized = false
    private var idTask: String? = ""
    private lateinit var token: String
    private var idArmazem: Int? = null
    private lateinit var progress: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolumeMovementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupConst()
        initDataWedge()
        setToolbar()
        setupViewModel()
        setupRecyclerView()
        setupDataWedge()
        clickCancel()
        callApi()
        observeViewModel()
        clickFinishTask()
        setupEdit()

    }

    private fun setupEdit() {
        binding.editMov.extensionSetOnEnterExtensionCodBarrasString {cod ->
            if (cod.isNotEmpty()) {
                sendAddVolume(cod)
            }
        }
    }

    private fun callApi() {
        progress.show()
        viewModel.returnTaskMov(idArmazem!!, token)
    }

    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun setupConst() {
        dialog = CustomAlertDialogCustom()
        progress = CustomAlertDialogCustom().progress(this)
        progress.hide()
        sharedPreferences = CustomSharedPreferences(this)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN)!!
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        if (token.isEmpty() || idArmazem == null) {
            dialog.alertMessageErrorSimplesAction(
                context = this,
                message = "Ocorreu um erro ao receber dados do usuário. Tente novamente.",
                action = { finish() })
        }
    }

    override fun update(p0: Observable?, p1: Any?) {}
    private fun setupDataWedge() {
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this, ReturnTaskViewModel.Mov1ViewModelFactory(
                MovimentacaoEntreEnderecosRepository()
            )
        )[ReturnTaskViewModel::class.java]
    }


    private fun setToolbar() {
        binding.toolbarMov1.subtitle = "[${getVersion()}]"
        binding.toolbarMov1.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = getVersionNameToolbar()
        }
    }

    private fun setupRecyclerView() {
        adapterVolumes = Adapter1Movimentacao()
        binding.rvMovimentacaoVol.apply {
            layoutManager = LinearLayoutManager(this@VolumeMovementActivity)
            adapter = adapterVolumes
        }
    }

    private fun clickCancel() {
        binding.buttonCancelTask.setOnClickListener {
            dialog.alertMessageAtencaoOptionAction(
                context = this,
                message = "Deseja cancelar a tarefa?",
                actionYes = {
                    if (!idTask.isNullOrEmpty()) {
                        viewModel.cancelTask(
                            BodyCancelMov5(idTarefa = idTask!!),
                            idArmazem!!,
                            token
                        )
                    }
                },
                actionNo = {
                    clearEdit(binding.editMov)
                }
            )
        }
    }

    private fun clickFinishTask() {
        binding.buttonFinishTask.setOnClickListener {
            alertFinish()
        }
    }

    private fun alertFinish() {
        dialogFinishTask = android.app.AlertDialog.Builder(this).create()
        dialogFinishTask?.setCancelable(false)
        val bindingAlert =
            LayoutCustomFinishMovementAdressBinding.inflate(LayoutInflater.from(this))
        dialogFinishTask?.setView(bindingAlert.root)
        dialogFinishTask?.create()
        dialogFinishTask?.show()
        bindingAlert.progressEdit.visibility = View.INVISIBLE
        bindingAlert.editQrcodeCustom.extensionSetOnEnterExtensionCodBarrasString {cod ->
            if (cod.isNotEmpty()) {
                dialogFinishTask?.dismiss()
                progress.show()
                sendFinishTask(cod.trim())
            }
        }
        bindingAlert.editQrcodeCustom.setText("")
        bindingAlert.editQrcodeCustom.requestFocus()
        bindingAlert.buttonCancelCustom.setOnClickListener {
            progress.dismiss()
            dialogFinishTask?.dismiss()
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    private fun sendFinishTask(qrCode: String) {
        val body = RequestBodyFinalizarMov4(
            codBarras = qrCode,
            idTarefa = idTask!!
        )
        viewModel.finishTask4(body = body, idArmazem!!, token)
    }

    private fun sendAddVolume(qrCode: String) {
        viewModel.sendAddVolume(idTask?:"", qrCode, token, idArmazem!!)
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            if (scanData != null) {
                clearEdit(binding.editMov)
                if (dialogFinishTask?.isShowing == true) {
                    dialogFinishTask?.dismiss()
                    progress.show()
                    sendFinishTask(scanData.trim())
                } else {
                    progress.show()
                    sendAddVolume(scanData.trim())
                }
                clearEdit(binding.editMov)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.apply {
            sucessAddVol()
            errorAddVol()
            responseTask()
            errorDefault()
            responseCancelTask()
            responseFinishTask()
        }
    }

    private fun ReturnTaskViewModel.responseFinishTask() {
        finishTaskShow.observe(this@VolumeMovementActivity) {
            progress.dismiss()
            clearEdit(binding.editMov)
            dialogFinishTask?.dismiss()
            dialog.alertMessageSucessAction(context = this@VolumeMovementActivity,
                message = "Tarefa finalizada com sucesso!",
                action = {
                    binding.txtRegTotalMov.visibility = View.INVISIBLE
                    binding.txtQntTotalMov.visibility = View.INVISIBLE
                    clearEdit(binding.editMov)
                    progress.dismiss()
                    viewModel.returnTaskMov(idArmazem!!, token)
                }
            )
            clearEdit(binding.editMov)
        }
    }

    private fun ReturnTaskViewModel.responseCancelTask() {
        cancelTaskShow.observe(this@VolumeMovementActivity) {
            progress.hide()
            binding.txtRegTotalMov.visibility = View.INVISIBLE
            binding.txtQntTotalMov.visibility = View.INVISIBLE
            toastSucess(this@VolumeMovementActivity, it.result)
            somSucess(this@VolumeMovementActivity)
            viewModel.returnTaskMov(idArmazem!!, token)
            clearEdit(binding.editMov)
        }
    }

    private fun ReturnTaskViewModel.errorDefault() {
        mErrorShow.observe(this@VolumeMovementActivity) { error ->
            progress.hide()
            dialog.alertMessageErrorSimples(this@VolumeMovementActivity, error)
        }
    }

    private fun ReturnTaskViewModel.sucessAddVol() {
        sucessAddVolumeShow.observe(this@VolumeMovementActivity) {
            progress.hide()
            somSucess(this@VolumeMovementActivity)
            clearEdit(binding.editMov)
            callApi()
        }
    }

    private fun ReturnTaskViewModel.errorAddVol() {
        errorAddVolumeShow.observe(this@VolumeMovementActivity) { error ->
            progress.hide()
            dialog.alertMessageErrorSimples(this@VolumeMovementActivity, error)
            clearEdit(binding.editMov)
        }
    }

    private fun ReturnTaskViewModel.responseTask() {
        mSucessShow.observe(this@VolumeMovementActivity) { responseTask ->
            progress.hide()
            if (responseTask.idTarefa != null) {
                setTotalizadores(responseTask)
                binding.txtInfo.visibility = View.INVISIBLE
                idTask = responseTask.idTarefa
                adapterVolumes.submitList(responseTask.itens)
                binding.apply {
                    buttonCancelTask.isEnabled = true
                    buttonFinishTask.isEnabled = true
                }
            } else {
                binding.txtInfo.visibility = View.VISIBLE
                adapterVolumes.submitList(null)
                idTask = null
                binding.apply {
                    buttonCancelTask.isEnabled = false
                    buttonFinishTask.isEnabled = false
                }
            }
        }
    }

    private fun setTotalizadores(responseTask: ResponseMovParesAvulso1) {
        if (responseTask.idTarefa != null) {
            binding.txtRegTotalMov.visibility = View.VISIBLE
            binding.txtQntTotalMov.visibility = View.VISIBLE
            binding.txtRegTotalMov.text = "Registros: ${responseTask.itens.size}"
            var qnt = 0
            responseTask.itens.forEach {
                qnt += it.quantidade
            }
            binding.txtQntTotalMov.text = "Total lido: $qnt"
        } else {
            binding.txtRegTotalMov.visibility = View.INVISIBLE
            binding.txtQntTotalMov.visibility = View.INVISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ObservableObject.instance.deleteObserver(this)
        progress.dismiss()
        unregisterReceiver(receiver)
    }

}