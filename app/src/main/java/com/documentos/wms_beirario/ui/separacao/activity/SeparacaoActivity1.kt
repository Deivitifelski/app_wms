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
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivitySeparacao1Binding
import com.documentos.wms_beirario.model.separation.RequestSeparationArraysAndares1
import com.documentos.wms_beirario.model.separation.ResponseSeparation1
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.ui.separacao.adapter.AdapterAndares
import com.documentos.wms_beirario.ui.separacao.filter.FilterSeparationActivity
import com.documentos.wms_beirario.ui.separacao.viewModel.SeparacaoViewModel1
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension


class SeparacaoActivity1 : AppCompatActivity() {

    private lateinit var binding: ActivitySeparacao1Binding
    private val TAG = "TESTE DE ITENS SEPARAÇAO -------->"
    private lateinit var mAdapterEstantes: AdapterAndares
    private lateinit var mViewModel: SeparacaoViewModel1
    private lateinit var token: String
    private var idArmazem: Int = 0
    private lateinit var sharedPreferences: CustomSharedPreferences
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


    private val responseFilter =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data =
                    result.data!!.getSerializableExtra("FILTERS") as RequestSeparationArraysAndares1
                Log.e(TAG, "chefou aqui:$data")
            }
        }

    private fun validadCheckAllReturn() {
        binding.selectAllEstantes.isChecked =
            mAdapterEstantes.mList.size == mAdapterEstantes.mListEstantesCheck.size
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySeparacao1Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        clickButton()
        initViewModel()
        initConst()
        setToolbar()
        initRv()
        setupObservables()
        setAllCheckBox()
        callApi()
        clickFilter()
    }

    private fun clickFilter() {
        binding.filterSeparation.setOnClickListener {
            val intent = Intent(this, FilterSeparationActivity::class.java)
            responseFilter.launch(intent)
        }
    }

    /**
     * BUSCA OS ANDARES  -->
     */
    private fun callApi() {
        mViewModel.apply {
            getItensAndares(idArmazem, token)
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
        binding.buttonNext.isEnabled = false
        binding.toolbarSeparation.apply {
            subtitle = getVersionNameToolbar()
            setNavigationOnClickListener {
                onBackTransitionExtension()
            }
        }
    }

    private fun initConst() {
        sharedPreferences = CustomSharedPreferences(this)
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
    }

    /**
     * CLIQUES NOS CHECKBOX QUE SELECIONA TODOS OS ITENS -->
     */
    private fun setAllCheckBox() {
        binding.selectAllEstantes.setOnClickListener {
            if (binding.selectAllEstantes.isChecked) {
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
        binding.apply {
            rvSeparationEstanteItems.apply {
                layoutManager = LinearLayoutManager(this@SeparacaoActivity1)
                adapter = mAdapterEstantes
            }
        }
    }

    //VALIDA SE A LISTA DO BANCO E A LISTA SELECIONADA SÃO IGUAIS MARCA O CHECK ALL -->
    private fun validadCheckAll(lista: List<ResponseSeparation1>) {
        val listBoolean = countBooleanListAdapter(lista)
        binding.selectAllEstantes.isChecked = listBoolean == lista.size
    }

    private fun validateButton(lista: List<ResponseSeparation1>) {
        val listBoolean = countBooleanListAdapter(lista)
        binding.buttonNext.isEnabled = listBoolean > 0
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
            binding.progress.isVisible = validProgress
        }
        //ANDARES -->
        mViewModel.mShowShow.observe(this) { itensCheckBox ->
            if (itensCheckBox.isEmpty()) {
                binding.apply {
                    txtInf.visibility = View.VISIBLE
                    view2.visibility = View.GONE
                    linearInfTotal.visibility = View.GONE
                    selectAllEstantes.visibility = View.INVISIBLE
                    buttonNext.isEnabled = false
                    linearInf.visibility = View.INVISIBLE
                    view.visibility = View.INVISIBLE
                }
                initRv()
            } else {
                binding.view2.visibility = View.VISIBLE
                binding.linearInfTotal.visibility = View.VISIBLE
                binding.linearInf.visibility = View.VISIBLE
                binding.selectAllEstantes.visibility = View.VISIBLE
                binding.txtInf.visibility = View.GONE
                binding.view.visibility = View.VISIBLE
                setTotalTxt(itensCheckBox)
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

    private fun setTotalTxt(list: List<ResponseSeparation1>?) {
        try {
            var totalAddress = 0
            list?.forEach {
                totalAddress += it.quantidadeEnderecos
            }
            var totalVolumes = 0
            list?.forEach {
                totalVolumes += it.quantidadeVolumes
            }
            binding.apply {
                txtTotalAddress.text = totalAddress.toString()
                txtTotalVolumes.text = totalVolumes.toString()
            }
        } catch (e: Exception) {
            mToast.toastCustomError(this, getString(R.string.error_calculate_total))
        }
    }

    /**
     * CLICK BUTTON -->
     */
    private fun clickButton() {
        binding.buttonNext.setOnClickListener {
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