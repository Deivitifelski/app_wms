package com.documentos.wms_beirario.ui.separacao.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivitySeparacao1Binding
import com.documentos.wms_beirario.model.separation.RequestSeparationArrays
import com.documentos.wms_beirario.model.separation.ResponseItemsSeparationItem
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.ui.separacao.adapter.AdapterSeparacaoAndaresItens
import com.documentos.wms_beirario.ui.separacao.adapter.AdapterSeparacaoEstantesItens
import com.documentos.wms_beirario.ui.separacao.viewModel.SeparacaoViewModel1
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersion
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension


class SeparacaoActivity1 : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivitySeparacao1Binding
    private val TAG = "TESTE DE ITENS SEPARAÇAO -------->"
    private lateinit var mAdapterEstantes: AdapterSeparacaoEstantesItens
    private lateinit var mAdapterAndares: AdapterSeparacaoAndaresItens
    private lateinit var mViewModel: SeparacaoViewModel1
    private lateinit var mShared: CustomSharedPreferences
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    val list = mutableListOf<ResponseItemsSeparationItem>()
    private val mResponseBack =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val mGetResult =
                    result.data!!.getSerializableExtra("ARRAY_BACK") as RequestSeparationArrays
                mAdapterEstantes.setCkeckBox(mGetResult.estantes)
                mAdapterAndares.setCkeckBox(mGetResult.andares)
//                val listAndares = mutableListOf<String>()
//                for (element in mGetResult.andares) {
//                    listAndares.add(element = element)
//                }
//                val listEstantes = mutableListOf<String>()
//                for (element in mGetResult.estantes) {
//                    listEstantes.add(element = element)
//                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivitySeparacao1Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
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

    private fun initConst() {
        mBinding.buttonNext.setOnClickListener(this)
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
        mAdapterEstantes = AdapterSeparacaoEstantesItens { listModel ->
            val listBoolean = mutableListOf<Boolean>()
            listModel.forEach { boolean ->
                listBoolean.add(boolean.status)
            }
            mBinding.selectAllEstantes.isChecked = !listBoolean.contains(false)
            validateButton()
        }
        mAdapterAndares = AdapterSeparacaoAndaresItens { listAndar ->
            val listBoolean = mutableListOf<Boolean>()
            listAndar.forEach { boolean ->
                listBoolean.add(boolean.status)
            }
            validateButton()
        }

        mBinding.apply {
            rvSeparationEstanteItems.apply {
                layoutManager = LinearLayoutManager(this@SeparacaoActivity1)
                adapter = mAdapterEstantes
            }
        }

    }

    private fun validateButton() {
        mBinding.buttonNext.isEnabled =
            mAdapterEstantes.mListItensClicksSelect.isNotEmpty() && mAdapterAndares.mListItensAndaresClicksSelect.isNotEmpty()
    }

    /**
     * BUSCA OS ANDARES E AS ESTANTES -->
     */
    private fun callApi() {
        mViewModel.apply {
            getItemsEstantesSeparation()
            getItemsAndaresSeparation()
        }
    }

    /**
     *CHAMADA QUE VALIDA SE HÁ ITENS PARA IR PARA OUTRA TELA -->
     */
    private fun callApi2() {
        mViewModel.postListCheck(
            RequestSeparationArrays(
                mAdapterAndares.mListItensAndaresClicksSelect,
                mAdapterEstantes.mListItensClicksSelect
            )
        )
    }


    private fun setupObservables() {
        mViewModel.mValidaTxtShow.observe(this) { validaTxt ->
            mBinding.selectAllEstantes.isVisible = validaTxt
        }
        mViewModel.mValidaProgressShow.observe(this) { validProgress ->
            mBinding.progress.isVisible = validProgress
        }
        //ANDARES -->
        mViewModel.mShowAndaresShow.observe(this) { andares ->
            if (andares.isEmpty()) {
                mBinding.txtInfAndares.visibility = View.VISIBLE
            } else {
                mBinding.txtInfAndares.visibility = View.GONE
                mAdapterAndares.update(andares)
            }
        }
        //ESTANTES -->
        mViewModel.mShowShow.observe(this) { itensCheckBox ->
            if (itensCheckBox.isEmpty()) {
                mBinding.selectAllEstantes.isEnabled = false
                mBinding.txtInfEstantes.visibility = View.VISIBLE
            } else {
                mBinding.txtInfEstantes.visibility = View.GONE
                mAdapterEstantes.update(itensCheckBox)
            }
        }

        mViewModel.mErrorShow.observe(this) { message ->
            mAlert.alertMessageErrorSimples(this, message)
        }
        /**
         * VALIDA SE HÁ LIGAÇÃO ENTRE ESTANTES E ANDARES PARA ENVIAR A OUTRA TELA -->
         */
        mViewModel.mShowShow2.observe(this) { sucess ->
            if (sucess.isEmpty()) {
                mAlert.alertMessageAtencao(
                    this,
                    "Não há tarefas entre estantes e andares selecionados!"
                )
            } else {
                val intent = Intent(this, SeparacaoActivity2::class.java)
                intent.putExtra(
                    "ARRAYS",
                    RequestSeparationArrays(
                        mAdapterAndares.mListItensAndaresClicksSelect,
                        mAdapterEstantes.mListItensClicksSelect
                    )
                )
                Log.e(
                    TAG,
                    "ENVIANDO --> ${mAdapterAndares.mListItensAndaresClicksSelect}\n${mAdapterEstantes.mListItensClicksSelect}"
                )
                Log.e(TAG, "enviando --> $intent")
                mResponseBack.launch(intent)
            }
        }
    }

    /**
     * CLICK BUTTON -->
     */
    override fun onClick(button: View?) {
        when (button) {
            mBinding.buttonNext -> {
                callApi2()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}