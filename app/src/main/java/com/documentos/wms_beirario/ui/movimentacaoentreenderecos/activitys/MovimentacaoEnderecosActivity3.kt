package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.activitys

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityMovimentacaoEntreEnderecosNewTask3Binding
import com.documentos.wms_beirario.databinding.LayoutCustomFinishMovementAdressBinding
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementAddTask
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementFinishAndress
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementNewTask
import com.documentos.wms_beirario.repository.movimentacaoentreenderecos.MovimentacaoEntreEnderecosRepository
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.adapter.Adapter2Movimentacao
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.viewmodel.EndMovementViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import java.util.*

class MovimentacaoEnderecosActivity3 : AppCompatActivity(), Observer {

    private lateinit var mBinding: ActivityMovimentacaoEntreEnderecosNewTask3Binding
    private val TAG = "MovimentacaoEnderecosActivity3"
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mToken: String
    private var mIdArmazem: Int = 0
    private lateinit var mAdapter: Adapter2Movimentacao
    private lateinit var mViewModel: EndMovementViewModel
    private lateinit var mShared: CustomSharedPreferences
    private lateinit var mProgress: Dialog
    private lateinit var mAlertDialogCustom: CustomAlertDialogCustom
    private lateinit var mediaSonsMp3: CustomMediaSonsMp3
    private lateinit var mNewTask: MovementNewTask
    private var mAlert: android.app.AlertDialog? = null
    private lateinit var mVibrar: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMovimentacaoEntreEnderecosNewTask3Binding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initConst()
        setRecyclerView()
        initViewModel()
        AppExtensions.visibilityProgressBar(mBinding.progressBarAddTarefa, visibility = false)
        getShared()
        getIntentClick()
        callApiGetNumDoc()
        setupObservable()
        setToolbar()
        clickButtonFinish()
        setupSwipe()
        setupDataWedge()
        editSend()

    }

    override fun onResume() {
        super.onResume()
        initDataWedge()
    }

    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun getIntentClick() {
        try {
            if (intent != null) {
                val objetoClick =
                    intent.getSerializableExtra("ID_NOVA_TAREFA") as MovementNewTask
                mNewTask = objetoClick
                callApi(mNewTask)
            }

        } catch (e: Exception) {
            mAlertDialogCustom.alertErroInitBack(
                this,
                this,
                "Erro ao receber dados!\nTente novamente."
            )
        }
    }

    private fun callApiGetNumDoc() {
        mViewModel.returnTaskMov(filterUser = true)
    }

    private fun initConst() {
        mToast = CustomSnackBarCustom()
        mBinding.editMov2.requestFocus()
        mShared = CustomSharedPreferences(this)
        mProgress = CustomAlertDialogCustom().progress(this)
        mProgress.hide()
        mAlertDialogCustom = CustomAlertDialogCustom()
        mediaSonsMp3 = CustomMediaSonsMp3()
        mVibrar = CustomAlertDialogCustom()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this, EndMovementViewModel.Mov2ViewModelFactory(
                MovimentacaoEntreEnderecosRepository()
            )
        )[EndMovementViewModel::class.java]
    }

    private fun setToolbar() {
        mBinding.toolbarMov2.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = getVersionNameToolbar()
        }
    }

    private fun setRecyclerView() {
        mAdapter = Adapter2Movimentacao()
        mBinding.rvMov2.apply {
            layoutManager = LinearLayoutManager(this@MovimentacaoEnderecosActivity3)
            adapter = mAdapter
        }
    }

    private fun getShared() {
        mShared.apply {
            mToken = getString(CustomSharedPreferences.TOKEN).toString()
            mIdArmazem = getInt(CustomSharedPreferences.ID_ARMAZEM)
        }
    }

    /**VERIFICA SE VAI TRAZER ITENS DA TAREFA CLICADA OU SE FOI CRIADA UMA TAREFA NOVA -->*/
    private fun callApi(mTarefaClicada: MovementNewTask) {
        mViewModel.getTaskItemClick(mTarefaClicada.idTarefa)
    }

    private fun setupSwipe() {
        val swipe = mBinding.swipeMov1
        swipe.setColorSchemeColors(this.getColor(R.color.color_default))
        swipe.setOnRefreshListener {
            mBinding.apply {
                txtDoc.text = "-"
                txtSizeList.text = "-"
            }
            callApiGetNumDoc()
            callApi(mNewTask)
            setRecyclerView()
            swipe.isRefreshing = false
        }
    }


    private fun clearText() {
        mBinding.editMov2.setText("")
        mBinding.editMov2.text?.clear()
        mBinding.editMov2.requestFocus()
    }


    private fun setupObservable() {
        /**TRÁS O NUMERO DO DOCUMENTO GERADO -->*/
        mViewModel.mSucessBuscaDocTaskShow.observe(this) { documento ->
            mBinding.txtDoc.text = documento
        }
        /**RESPOSTA MOSTRAR PROGRESSBAR -->*/
        mViewModel.mValidProgressShow.observe(this) { progressBar ->
            if (progressBar) {
                mProgress.show()
            } else {
                mProgress.hide()
            }
        }
        /**RESPOSTA DE ERRO AO TRAZER AS TAREFAS -->*/
        mViewModel.mErrorShow.observe(this) { messageErro ->
            mProgress.hide()
            mAlert?.dismiss()
            mAlertDialogCustom.alertMessageErrorSimplesAction(this, messageErro, action = {
                clearText()
            })
        }

        mViewModel.mErrorAllShow.observe(this) { error ->
            clearText()
            mProgress.hide()
            mAlert?.dismiss()
            AppExtensions.visibilityProgressBar(mBinding.progressBarAddTarefa, false)
            mAlertDialogCustom.alertMessageErrorSimples(this, error)
        }

        /**RESPOSTA SUCESSO PARA CRIAR RECYCLERVIEW -->*/
        mViewModel.mSucessShow.observe(this) { list ->
            mProgress.hide()
            mAdapter.submitList(list)
            if (list.isEmpty()) {
                mBinding.buttonfinish.isEnabled = false
                mBinding.txtSizeList.text = "0"
                mBinding.linearInfo.visibility = View.INVISIBLE
            } else {
                mBinding.buttonfinish.isEnabled = true
                mBinding.txtSizeList.text = list.size.toString()
                mBinding.linearInfo.visibility = View.VISIBLE
            }
        }

        /**RESPOSTA AO ADICIONAR TAREFAS -->*/
        mViewModel.mSucessAddTaskShow.observe(this) {
            mProgress.hide()
            clearText()
            AppExtensions.visibilityProgressBar(mBinding.progressBarAddTarefa, false)
            callApi(mNewTask)
            setRecyclerView()
            mediaSonsMp3.somSucess(this)
            mProgress.hide()
        }

        mViewModel.mErrorAddTaskShow.observe(this) { messageError ->
            mProgress.hide()
            clearText()
            vibrateExtension(500)
            AppExtensions.visibilityProgressBar(mBinding.progressBarAddTarefa, false)
            mAlertDialogCustom.alertMessageErrorCancelFalse(this, messageError)
            mProgress.hide()
        }

        /**RESPOSTA AO FINALIZAR TAREFAS -->*/
        mViewModel.mSucessFinishShow.observe(this) {
            mProgress.hide()
            mAlert?.dismiss()
            mBinding.buttonfinish.isEnabled = false
            callApi(mNewTask)
            setRecyclerView()
            mProgress.hide()
            mAlertDialogCustom.alertSucessFinishBack(
                this,
                getString(R.string.finish_sucess)
            )
        }
    }

    private fun clickButtonFinish() {
        mBinding.buttonfinish.setOnClickListener {
            mediaSonsMp3.somAlerta(this)
            alertFinish()
        }
    }

    /**
     * DIALOG QUE REALIDA A LEITURA PARA FINALIZAR A MOVIMENTAÇAO -->
     */
    private fun alertFinish() {
        mAlert = android.app.AlertDialog.Builder(this).create()
        mAlert?.setCancelable(false)
        val mBindingAlert =
            LayoutCustomFinishMovementAdressBinding.inflate(LayoutInflater.from(this))
        mAlert?.setView(mBindingAlert.root)
        mAlert?.create()
        mAlert?.show()
        mBindingAlert.progressEdit.visibility = View.INVISIBLE
        hideKeyExtensionActivity(mBindingAlert.editQrcodeCustom)
        mBindingAlert.editQrcodeCustom.setText("")
        mBindingAlert.editQrcodeCustom.requestFocus()

        mBindingAlert.buttonCancelCustom.setOnClickListener {
            mAlert?.dismiss()
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    private fun setupDataWedge() {
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
    }

    private fun editSend() {
        mBinding.editMov2.extensionSetOnEnterExtensionCodBarrasString { digitado ->
            if (digitado.isNotEmpty()) {
                readingAndress(digitado)
            } else {
                Toast.makeText(this, "Campo vazio!", Toast.LENGTH_SHORT).show()
                mVibrar.vibrar(this)
            }
            clearText()
        }
    }

    /**
     * VERIFICA SE O MODAL DE FINALIZAÇÃO ESTÁ SENDO MOSTRADO,CASO SIM CHAMA OUTRA ROTA,
     * CASO O MODAL DE CARREGAMNETO ESTIVER SHOW MOSTRA MSG,SÓ LIBERA PARA NOVA BIPAGEM APÓS
     * RESPOSTA DO SERVIDOR!
     */
    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            if (mProgress.isShowing) {
                mediaSonsMp3.somAlerta(this)
                mToast.toastCustomError(this, "Aguarde resposta do servidor!")
            } else {
                if (mAlert?.isShowing == true) {
                    mProgress.show()
                    Log.w(TAG, "FINALIZANDO TAREFA **!")
                    readingAlert(scanData.toString())
                    clearText()
                } else {
                    mProgress.show()
                    Log.w(TAG, "ADICIONANDO TAREFA **!")
                    readingAndress(scanData.toString())
                    clearText()
                }
            }
        }
    }

    private fun readingAlert(scan: String) {
        mAlert?.dismiss()
        mViewModel.finishMovemet(
            MovementFinishAndress(
                idTarefa = mNewTask.idTarefa,
                codigoBarrasEndereco = scan
            )
        )
    }

    private fun readingAndress(scan: String) {
        mViewModel.addTask(
            MovementAddTask(
                mNewTask.idTarefa,
                scan
            )
        )

    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mProgress.dismiss()
    }
}
