package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.activitys

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityMovimentacaoEnderecos2Binding
import com.documentos.wms_beirario.databinding.LayoutCustomFinishMovementAdressBinding
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementAddTask
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementFinishAndress
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementResponseModel1
import com.documentos.wms_beirario.repository.movimentacaoentreenderecos.MovimentacaoEntreEnderecosRepository
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.adapter.Adapter2Movimentacao
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.viewmodel.EndMovementViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.*
import java.util.*

class MovimentacaoEnderecosActivity2 : AppCompatActivity(), Observer {

    private lateinit var mBinding: ActivityMovimentacaoEnderecos2Binding
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
    private lateinit var mTarefaClicada: MovementResponseModel1
    private var mAlert: android.app.AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMovimentacaoEnderecos2Binding.inflate(layoutInflater)
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
        initEditAddTask()
        setupSwipe()
        setupDataWedge()

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
                    intent.getSerializableExtra("CLIQUE_TAREFA") as MovementResponseModel1
                mTarefaClicada = objetoClick
                callApi(mTarefaClicada)
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
        mShared = CustomSharedPreferences(this)
        mProgress = CustomAlertDialogCustom().progress(this)
        mProgress.hide()
        mAlertDialogCustom = CustomAlertDialogCustom()
        mediaSonsMp3 = CustomMediaSonsMp3()
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
            layoutManager = LinearLayoutManager(this@MovimentacaoEnderecosActivity2)
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
    private fun callApi(mTarefaClicada: MovementResponseModel1) {
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
            callApi(mTarefaClicada)
            setRecyclerView()
            swipe.isRefreshing = false
        }
    }


    /**ADICIONANDO NOVA TAREFA -->*/
    private fun initEditAddTask() {
        hideKeyExtensionActivity(mBinding.editMov2)
        mBinding.editMov2.extensionSetOnEnterExtensionCodBarras {
            AppExtensions.visibilityProgressBar(mBinding.progressBarAddTarefa, visibility = true)
            mViewModel.addTask(
                MovementAddTask(
                    mTarefaClicada.idTarefa,
                    mBinding.editMov2.text.toString()
                )
            )
            clearText()
        }
    }

    private fun clearText() {
        mBinding.editMov2.setText("")
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
            mAlert?.dismiss()
            AppExtensions.visibilityProgressBar(mBinding.progressBarAddTarefa, false)
            mAlertDialogCustom.alertMessageErrorSimples(this, messageErro)
        }

        mViewModel.mErrorAllShow.observe(this) { error ->
            mAlert?.dismiss()
            AppExtensions.visibilityProgressBar(mBinding.progressBarAddTarefa, false)
            mAlertDialogCustom.alertMessageErrorSimples(this, error)
        }

        /**RESPOSTA SUCESSO PARA CRIAR RECYCLERVIEW -->*/
        mViewModel.mSucessShow.observe(this) { list ->
            mProgress.hide()
            mAdapter.submitList(list)
            if (list.isEmpty()) {
                mBinding.txtSizeList.text = "0"
                mBinding.linearInfo.visibility = View.INVISIBLE
            } else {
                mBinding.txtSizeList.text = list.size.toString()
                mBinding.linearInfo.visibility = View.VISIBLE
            }
        }

        /**RESPOSTA AO ADICIONAR TAREFAS -->*/
        mViewModel.mSucessAddTaskShow.observe(this) {
            mediaSonsMp3.somSucess(this)
            AppExtensions.visibilityProgressBar(mBinding.progressBarAddTarefa, false)
            callApi(mTarefaClicada)
            setRecyclerView()
            mProgress.hide()
        }

        mViewModel.mErrorAddTaskShow.observe(this) { messageError ->
            vibrateExtension(500)
            AppExtensions.visibilityProgressBar(mBinding.progressBarAddTarefa, false)
            mAlertDialogCustom.alertMessageErrorCancelFalse(this, messageError)
            mProgress.hide()
        }

        /**RESPOSTA AO FINALIZAR TAREFAS -->*/
        mViewModel.mSucessFinishShow.observe(this) {
            mAlert?.dismiss()
            mediaSonsMp3.somSucess(this)
            mBinding.buttonfinish.isEnabled = false
            callApi(mTarefaClicada)
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
        mBindingAlert.progressEdit.visibility = View.INVISIBLE
        hideKeyExtensionActivity(mBindingAlert.editQrcodeCustom)
        //Recebendo a leitura Coletor Finalizar Tarefa -->
        mBindingAlert.editQrcodeCustom.addTextChangedListener { qrcode ->
            mAlert?.dismiss()
        }
        mBindingAlert.editQrcodeCustom.setText("")
        mBindingAlert.editQrcodeCustom.requestFocus()

        mBindingAlert.buttonCancelCustom.setOnClickListener {
            mediaSonsMp3.somClick(this)
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

    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            scanData.let { qrCode ->
                if (mAlert!!.isShowing) {
                    readingAlert(qrCode.toString())
                } else {
                    readingAndress(qrCode.toString())
                }
            }
        }
    }

    private fun readingAlert(scan: String) {
        mViewModel.finishMovemet(
            MovementFinishAndress(
                idTarefa = mTarefaClicada.idTarefa,
                codigoBarrasEndereco = scan
            )
        )
    }

    private fun readingAndress(scan: String) {
        mViewModel.addTask(
            MovementAddTask(
                mTarefaClicada.idTarefa,
                scan
            )
        )
        clearText()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}
