package com.documentos.wms_beirario.ui.separacao.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivitySeparacao1Binding
import com.documentos.wms_beirario.model.separation.RequestSeparationArraysAndares1
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.ui.separacao.adapter.AdapterAndares
import com.documentos.wms_beirario.ui.separacao.viewModel.SeparacaoViewModel1
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*


class SeparacaoActivity1 : AppCompatActivity() {

    private lateinit var mBinding: ActivitySeparacao1Binding
    private val TAG = "TESTE DE ITENS SEPARAÇAO -------->"
    private lateinit var mAdapterEstantes: AdapterAndares
    private lateinit var mViewModel: SeparacaoViewModel1
    private lateinit var mShared: CustomSharedPreferences
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private val mResponseBack =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val mGetResult =
                    result.data!!.getSerializableExtra("ARRAY_BACK") as RequestSeparationArraysAndares1
                mAdapterEstantes.setCkeckBox(mGetResult.andares)
                validadCheckAllReturn(mGetResult.andares.size)
            }
        }

    private fun validadCheckAllReturn(size: Int) {
        mBinding.selectAllEstantes.isChecked = mAdapterEstantes.mList.size == size
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivitySeparacao1Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        clickButton()
        initViewModel()
        initConst()
        setToolbar()
        initRv()
        callApi()
        setupObservables()
        setAllCheckBox()
        validateButton()
    }

    override fun onResume() {
        super.onResume()
        callApi()
        validateButton()
    }

    /**
     * BUSCA OS ANDARES  -->
     */
    private fun callApi() {
        mViewModel.apply {
            getItensAndares()
        }
    }


    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this, SeparacaoViewModel1.SeparacaoItensViewModelFactory(
                SeparacaoRepository()
            )
        )[SeparacaoViewModel1::class.java]
    }

    private fun setToolbar() {
        mBinding.buttonNext.isEnabled = false
        mBinding.toolbarSeparation.apply {
            subtitle = "[${getVersion()}]"
            setNavigationOnClickListener {
                onBackTransitionExtension()
            }
        }
    }

    //VALIDA SE A LISTA DO BANCO E A LISTA SELECIONADA SÃO IGUAIS MARCA O CHECK ALL -->
    private fun validadCheckAll() {
        mBinding.selectAllEstantes.isChecked =
            mAdapterEstantes.mListEstantesCheck.size == mAdapterEstantes.mList.size
    }

    private fun initConst() {
        mShared = CustomSharedPreferences(this)
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    /**
     * CLIQUES NOS CHECKBOX QUE SELECIONA TODOS OS ITENS -->
     */
    private fun setAllCheckBox() {
        mBinding.selectAllEstantes.setOnClickListener {
            if (mBinding.selectAllEstantes.isChecked) {
                mAdapterEstantes.selectAll()
            } else {
                mAdapterEstantes.unSelectAll()
            }
        }
    }

    /**
     * INICIANDO OS ADAPTER -->
     */
    private fun initRv() {
        mAdapterEstantes = AdapterAndares {
            validadCheckAll()
            validateButton()
        }

        mBinding.apply {
            rvSeparationEstanteItems.apply {
                layoutManager = LinearLayoutManager(this@SeparacaoActivity1)
                adapter = mAdapterEstantes
            }
        }
    }

    //VALIDA BUTTON DE AVANÇAR -->
    private fun validateButton() {
        mBinding.buttonNext.isEnabled =
            mAdapterEstantes.mListEstantesCheck.isNotEmpty()
    }


    private fun setupObservables() {
        mViewModel.mValidaProgressShow.observe(this) { validProgress ->
            mBinding.progress.isVisible = validProgress
        }
        //ANDARES -->
        mViewModel.mShowShow.observe(this) { itensCheckBox ->
            if (itensCheckBox.isEmpty()) {
                vibrateExtension(500)
                mBinding.apply {
                    txtInf.visibility = View.VISIBLE
                    selectAllEstantes.isEnabled = false
                }
                initRv()
            } else {
                mBinding.txtInf.visibility = View.GONE
                mAdapterEstantes.update(itensCheckBox)
            }
        }

        mViewModel.mErrorShow.observe(this) { message ->
            mAlert.alertMessageErrorSimples(this, message)
        }
    }

    /**
     * CLICK BUTTON -->
     */
    private fun clickButton() {
        mBinding.buttonNext.setOnClickListener {
            val intent = Intent(this, SeparacaoActivity2::class.java)
            intent.putExtra(
                "ARRAYS_AND_EST", RequestSeparationArraysAndares1(
                    mAdapterEstantes.mListEstantesCheck
                )
            )
            mResponseBack.launch(intent)
            extensionSendActivityanimation()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}