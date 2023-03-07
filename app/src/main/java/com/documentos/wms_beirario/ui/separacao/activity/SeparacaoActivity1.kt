package com.documentos.wms_beirario.ui.separacao.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivitySeparacao1Binding
import com.documentos.wms_beirario.model.separation.RequestSeparationArraysAndares1
import com.documentos.wms_beirario.model.separation.ResponseSeparation1
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.ui.separacao.adapter.AdapterAndares
import com.documentos.wms_beirario.ui.separacao.viewModel.SeparacaoViewModel1
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension


class SeparacaoActivity1 : AppCompatActivity() {

    private lateinit var mBinding: ActivitySeparacao1Binding
    private val TAG = "TESTE DE ITENS SEPARAÇAO -------->"
    private lateinit var mAdapterEstantes: AdapterAndares
    private lateinit var mViewModel: SeparacaoViewModel1
    private lateinit var mShared: CustomSharedPreferences
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private var mListAndares = mutableListOf<String>()
    private var mGetResult: RequestSeparationArraysAndares1? = null
    private val mResponseBack =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                mGetResult =
                    result.data!!.getSerializableExtra("ARRAY_BACK") as RequestSeparationArraysAndares1
                callApi()
            }
        }

    private fun validadCheckAllReturn() {
        mBinding.selectAllEstantes.isChecked =
            mAdapterEstantes.mList.size == mAdapterEstantes.mListEstantesCheck.size
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
        setupObservables()
        setAllCheckBox()
        callApi()
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
            subtitle = getVersionNameToolbar()
            setNavigationOnClickListener {
                onBackTransitionExtension()
            }
        }
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
        mAdapterEstantes = AdapterAndares { lista ->
            validadCheckAll(lista)
            validateButton(lista)
            setupListSend(lista)
        }
        mBinding.apply {
            rvSeparationEstanteItems.apply {
                layoutManager = LinearLayoutManager(this@SeparacaoActivity1)
                adapter = mAdapterEstantes
            }
        }
    }

    //VALIDA SE A LISTA DO BANCO E A LISTA SELECIONADA SÃO IGUAIS MARCA O CHECK ALL -->
    private fun validadCheckAll(lista: List<ResponseSeparation1>) {
        val listBoolean = countBooleanListAdapter(lista)
        mBinding.selectAllEstantes.isChecked = listBoolean == lista.size
    }

    private fun validateButton(lista: List<ResponseSeparation1>) {
        val listBoolean = countBooleanListAdapter(lista)
        mBinding.buttonNext.isEnabled = listBoolean > 0
    }


    private fun countBooleanListAdapter(lista: List<ResponseSeparation1>): Int {
        var listBoolean = 0
        lista.forEach {
            if (it.status) {
                listBoolean += 1
            }
        }
        return listBoolean
    }

    private fun setupListSend(lista: List<ResponseSeparation1>) {
        mListAndares.clear()
        lista.forEach {
            if (it.status) {
                mListAndares.add(it.andar)
            }
        }
        mListAndares.forEach {
            Log.e(TAG, it)
        }
    }


    private fun setupObservables() {
        mViewModel.mValidaProgressShow.observe(this) { validProgress ->
            mBinding.progress.isVisible = validProgress
        }
        //ANDARES -->
        mViewModel.mShowShow.observe(this) { itensCheckBox ->
            if (itensCheckBox.isEmpty()) {
                mBinding.apply {
                    txtInf.visibility = View.VISIBLE
                    selectAllEstantes.visibility = View.INVISIBLE
                    buttonNext.isEnabled = false
                    linearInf.visibility = View.INVISIBLE
                    view.visibility = View.INVISIBLE
                }
                initRv()
            } else {
                mBinding.linearInf.visibility = View.VISIBLE
                mBinding.selectAllEstantes.visibility = View.VISIBLE
                mBinding.txtInf.visibility = View.GONE
                mBinding.view.visibility = View.VISIBLE
                mAdapterEstantes.update(itensCheckBox)
                if (mGetResult != null) {
                    mAdapterEstantes.setCkeckBox(mGetResult!!.andares)
                }
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
                    mListAndares
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