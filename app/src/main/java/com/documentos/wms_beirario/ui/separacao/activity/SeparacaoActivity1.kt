package com.documentos.wms_beirario.ui.separacao.activity

import android.os.Bundle
import android.view.View
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
//        callApi()
        setupObservables()
        setAllCheckBox()
        validateButton()
    }

    override fun onResume() {
        super.onResume()
//        callApi()
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

        mBinding.apply {
            rvSeparationEstanteItems.apply {
                layoutManager = LinearLayoutManager(this@SeparacaoActivity1)
                adapter = mAdapterEstantes
            }
        }
        mAdapterEstantes.update(DataMock.returnData())

    }

    private fun validateButton() {
        mBinding.buttonNext.isEnabled =
            mAdapterEstantes.mListEstantesCheck.isNotEmpty()
    }

    /**
     * BUSCA OS ANDARES E AS ESTANTES -->
     */
    private fun callApi() {
        mViewModel.apply {
            getItemsEstantesSeparation()
        }
    }

    private fun setupObservables() {
        mViewModel.mValidaTxtShow.observe(this) { validaTxt ->
            mBinding.selectAllEstantes.isVisible = validaTxt
        }
        mViewModel.mValidaProgressShow.observe(this) { validProgress ->
            mBinding.progress.isVisible = validProgress
        }
        //ESTANTES -->
        mViewModel.mShowShow.observe(this) { itensCheckBox ->
            if (itensCheckBox.isEmpty()) {
                mBinding.selectAllEstantes.isEnabled = false
                mBinding.txtInfEstantes.visibility = View.VISIBLE
            } else {
                mBinding.txtInfEstantes.visibility = View.GONE
                mAdapterEstantes.update(DataMock.returnData())
            }
        }

        mViewModel.mErrorShow.observe(this) { message ->
            mAlert.alertMessageErrorSimples(this, message)
        }
    }

    /**
     * CLICK BUTTON -->
     */
    override fun onClick(button: View?) {
        when (button) {
            mBinding.buttonNext -> {

            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}