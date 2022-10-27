package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.activitys

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityMovimentacaoEnderecos1Binding
import com.documentos.wms_beirario.repository.movimentacaoentreenderecos.MovimentacaoEntreEnderecosRepository
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.adapter.Adapter1Movimentacao
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.viewmodel.ReturnTaskViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersion
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar

class MovimentacaoEnderecosActivity1 : AppCompatActivity() {

    private lateinit var mBinding: ActivityMovimentacaoEnderecos1Binding
    private lateinit var mAdapter: Adapter1Movimentacao
    private lateinit var mViewModel: ReturnTaskViewModel
    private lateinit var mShared: CustomSharedPreferences
    private lateinit var mProgress: Dialog
    private lateinit var mDialog: CustomAlertDialogCustom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMovimentacaoEnderecos1Binding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initViewModel()
        setToolbar()
        setObservable()
        clickButtonNewTask()
        setSwipeRefreshLayout()

    }

    override fun onResume() {
        super.onResume()
        mProgress.hide()
        callApi()
        initRv()
    }

    private fun setSwipeRefreshLayout() {
        mBinding.swipeRefreshLayoutMov1.apply {
            setColorSchemeColors(this@MovimentacaoEnderecosActivity1.getColor(R.color.color_default))
            setOnRefreshListener {
                mBinding.imageLottie.visibility = View.INVISIBLE
                mBinding.progressBarInitMovimentacao1.isVisible = true
                initRv()
                callApi()
                isRefreshing = false
            }
        }
    }

    private fun callApi() {
        mViewModel.returnTaskMov(filterUser = true)
    }

    private fun initViewModel() {
        mDialog = CustomAlertDialogCustom()
        mProgress = CustomAlertDialogCustom().progress(
            this,
            getString(R.string.create_new_task)
        )
        mShared = CustomSharedPreferences(this)
        mBinding.imageLottie.visibility = View.INVISIBLE
        mBinding.txtListEmply.visibility = View.INVISIBLE
        mViewModel = ViewModelProvider(
            this, ReturnTaskViewModel.Mov1ViewModelFactory(
                MovimentacaoEntreEnderecosRepository()
            )
        )[ReturnTaskViewModel::class.java]

    }

    private fun setToolbar() {
        mBinding.toolbarMov1.subtitle = "[${getVersion()}]"
        mBinding.toolbarMov1.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = getVersionNameToolbar()
        }
    }

    private fun initRv() {
        mAdapter = Adapter1Movimentacao { itemClicked ->
            //FALTA ENVIAR ID TAREFA COMO NULL -->
            val intent = Intent(this, MovimentacaoEnderecosActivity2::class.java)
            intent.putExtra("CLIQUE_TAREFA", itemClicked)
            startActivity(intent)
            extensionSendActivityanimation()
        }

        mBinding.rvMovimentacao1.apply {
            layoutManager = LinearLayoutManager(this@MovimentacaoEnderecosActivity1)
            adapter = mAdapter
        }
    }

    private fun setObservable() {
        //DEFINE OS ITENS DA RECYCLERVIEW ->
        mViewModel.mSucessShow.observe(this) { listTask ->
            if (listTask.isEmpty()) {
                mBinding.imageLottie.visibility = View.VISIBLE
                mBinding.txtListEmply.visibility = View.VISIBLE
            } else {
                mBinding.imageLottie.visibility = View.INVISIBLE
                mBinding.txtListEmply.visibility = View.INVISIBLE
                mAdapter.submitList(listTask)
            }
        }
        //ERRO ->
        mViewModel.mErrorShow.observe(this) { messageError ->
            mProgress.hide()
            mDialog.alertMessageErrorSimples(this, message = messageError)
        }
        //VALIDA PROGRESSBAR -->
        mViewModel.mValidProgressShow.observe(this) { validProgress ->
            mBinding.progressBarInitMovimentacao1.isVisible = validProgress
        }

        /** RESPOSTA DE NOVA TAREFA CRIADA COM SUCESSO -->*/
        mViewModel.mcreateNewTskShow.observe(this) { newIdTask ->
            mProgress.hide()
            if (newIdTask.idTarefa.isNotEmpty()) {
                //FALTA ENVIAR CLICK COMO NULL -->
                val intent = Intent(this, MovimentacaoEnderecosActivity3::class.java)
                intent.putExtra("ID_NOVA_TAREFA", newIdTask)
                startActivity(intent)
                extensionSendActivityanimation()
            }
        }
    }

    private fun clickButtonNewTask() {
        mBinding.buttonNewTask.setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed({
                mViewModel.newTask()
            }, 1000)
            mProgress.show()
        }
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
