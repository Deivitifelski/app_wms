package com.documentos.wms_beirario.ui.separacao.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivitySeparacao2Binding
import com.documentos.wms_beirario.databinding.LayoutAlertSucessCustomBinding
import com.documentos.wms_beirario.model.separation.RequestSeparationArraysAndares1
import com.documentos.wms_beirario.model.separation.RequestSeparationArraysAndaresEstante3
import com.documentos.wms_beirario.model.separation.ResponseEstantes
import com.documentos.wms_beirario.model.separation.ResponseEstantesItem
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.ui.separacao.adapter.AdapterEstantes
import com.documentos.wms_beirario.ui.separacao.viewModel.SeparacaoViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*


class SeparacaoActivity2 : AppCompatActivity() {

    private lateinit var mBinding: ActivitySeparacao2Binding
    private val TAG = "TESTE DE ITENS SEPARAÇAO -------->"
    private lateinit var mAdapterEstantes: AdapterEstantes
    private lateinit var mViewModel: SeparacaoViewModel2
    private lateinit var mShared: CustomSharedPreferences
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mIntentData: RequestSeparationArraysAndares1
    private var mListEstantes = mutableListOf<String>()
    private var mGetResult: RequestSeparationArraysAndaresEstante3? = null
    private val mResponseBack =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                mGetResult =
                    result.data!!.getSerializableExtra("ARRAY_BACK") as RequestSeparationArraysAndaresEstante3
                callApi()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivitySeparacao2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        clickButton()
        initIntent()
        initViewModel()
        initConst()
        setToolbar()
        initRv()
        setupObservables()
        setAllCheckBox()
        callApi()
    }


    private fun initIntent() {
        try {
            mShared = CustomSharedPreferences(this)
            val extras = intent
            if (extras != null) {
                val data =
                    extras.getSerializableExtra("ARRAYS_AND_EST") as RequestSeparationArraysAndares1
                mIntentData = data
                Log.e("TAG", "initIntent --> $data")
            }
        } catch (e: Exception) {
            vibrateExtension(500)
            mToast.snackBarErrorSimples(mBinding.root, e.toString())
        }
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this, SeparacaoViewModel2.SeparacaoItensViewModelFactory2(
                SeparacaoRepository()
            )
        )[SeparacaoViewModel2::class.java]
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

    //VALIDA SE A LISTA DO BANCO E A LISTA SELECIONADA SÃO IGUAIS MARCA O CHECK ALL -->
    private fun validadCheckAll(lista: List<ResponseEstantesItem>) {
        val listBoolean = countBooleanListAdapter(lista)
        mBinding.selectAllEstantes.isChecked = listBoolean == lista.size
    }

    private fun validateButton(lista: List<ResponseEstantesItem>) {
        val listBoolean = countBooleanListAdapter(lista)
        mBinding.buttonNext.isEnabled = listBoolean > 0
    }

    /**
     * INICIANDO OS ADAPTER -->
     */
    private fun initRv() {
        mAdapterEstantes = AdapterEstantes { lista ->
            validadCheckAll(lista)
            validateButton(lista)
            setupListSend(lista)
        }

        mBinding.apply {
            rvSeparationEstanteItems.apply {
                layoutManager = LinearLayoutManager(this@SeparacaoActivity2)
                adapter = mAdapterEstantes
            }
        }
    }

    private fun countBooleanListAdapter(lista: List<ResponseEstantesItem>): Int {
        var listBoolean = 0
        lista.forEach {
            if (it.status) {
                listBoolean += 1
            }
        }
        return listBoolean
    }

    private fun setupListSend(lista: List<ResponseEstantesItem>) {
        mListEstantes.clear()
        lista.forEach {
            if (it.status) {
                mListEstantes.add(it.estante)
            }
        }
        mListEstantes.forEach {
            Log.e(TAG, it)
        }
    }

    /**
     * BUSCA OS ANDARES E AS ESTANTES -->
     */
    private fun callApi() {
        mViewModel.apply {
            postItensEstantes(mIntentData)
        }
    }

    private fun setupObservables() {
        //ESTANTES -->
        mViewModel.mShowShow.observe(this) { estantesComTarefas ->
            if (estantesComTarefas.isEmpty()) {
                vibrateExtension(500)
                mBinding.selectAllEstantes.isEnabled = false
                mBinding.txtInfEstantes.visibility = View.VISIBLE
                initRv()
                mAlert.alertMessageSucessAction(
                    this,
                    message = "Tarefas separação finalizadas!",
                    action = {
                        val list = mutableListOf<String>()
                        estantesComTarefas?.forEach {
                            list.add(it.andar)
                        }
                        mIntentData = RequestSeparationArraysAndares1(list)
                        onBackPressed()
                    })
            } else {
                mBinding.txtInfEstantes.visibility = View.GONE
                mAdapterEstantes.update(estantesComTarefas)
                if (mGetResult != null) {
                    mAdapterEstantes.setCkeckBox(mGetResult!!.estantes)
                }
            }
        }

        mViewModel.mValidaProgressShow.observe(this) { validProgress ->
            mBinding.progress.isVisible = validProgress
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
            val intent = Intent(this, SeparacaoActivity3::class.java)
            intent.putExtra(
                "ARRAYS_AND_EST", RequestSeparationArraysAndaresEstante3(
                    mIntentData.andares, mListEstantes
                )
            )
            mResponseBack.launch(intent)
            extensionSendActivityanimation()
        }
    }


    /**funcao que retorna a primeira tela de separacao a lista -->*/
    private fun returSeparation1() {
        val intent = Intent()
        intent.putExtra("ARRAY_BACK", mIntentData)
        Log.e("SEPARAÇAO ACTIVITY 2", "returSeparation1 --> $mIntentData ")
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        returSeparation1()
        extensionBackActivityanimation(this)
    }
}