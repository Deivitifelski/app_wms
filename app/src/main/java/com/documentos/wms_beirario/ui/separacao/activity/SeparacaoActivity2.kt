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
import com.documentos.wms_beirario.databinding.ActivitySeparacao2Binding
import com.documentos.wms_beirario.model.separation.RequestSeparationArraysAndares1
import com.documentos.wms_beirario.model.separation.RequestSeparationArraysAndaresEstante3
import com.documentos.wms_beirario.model.separation.ResponseEstantesItem
import com.documentos.wms_beirario.model.separation.filtros.BodyEstantesFiltro
import com.documentos.wms_beirario.model.separation.filtros.ItemDocTrans
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
    private lateinit var viewModel: SeparacaoViewModel2
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var token: String
    private var ideArmazem: Int = 0
    private lateinit var listDoc: ItemDocTrans
    private lateinit var listTrans: ItemDocTrans
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var intentData: RequestSeparationArraysAndares1
    private var mListEstantes = mutableListOf<String>()
    private var getResult: RequestSeparationArraysAndaresEstante3? = null
    private val mResponseBack =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    getResult =
                        result.data!!.getSerializableExtra("ARRAY_BACK") as RequestSeparationArraysAndaresEstante3
                    listDoc = result.data!!.getSerializableExtra("DOC") as ItemDocTrans
                    listTrans = result.data!!.getSerializableExtra("TRANS") as ItemDocTrans
                    callApi()
                } catch (e: Exception) {
                    alertDefaulError(
                        this,
                        message = "Ocorreu um erro ao receber os dados",
                        onClick = { finish() })
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivitySeparacao2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        clickButton()
        initViewModel()
        initConst()
        initIntent()
        setToolbar()
        initRv()
        setupObservables()
        setAllCheckBox()
    }


    private fun initIntent() {
        try {
            sharedPreferences = CustomSharedPreferences(this)
            token = sharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
            ideArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
            val extras = intent
            if (extras != null) {
                val data =
                    extras.getSerializableExtra("ARRAYS_AND_EST") as RequestSeparationArraysAndares1
                listDoc = extras.getSerializableExtra("DOC") as ItemDocTrans
                listTrans = extras.getSerializableExtra("TRANS") as ItemDocTrans
                intentData = data
                Log.e(
                    "Recebendo tela 1",
                    "DOCUMENTOS:${listDoc.items}\nTRANSPORTADORA:${listTrans.items}"
                )
                callApi()
                Log.e("TAG", "initIntent --> $data")
            }
        } catch (e: Exception) {
            vibrateExtension(500)
            mToast.snackBarErrorSimples(mBinding.root, e.toString())
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
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
        sharedPreferences = CustomSharedPreferences(this)
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
        val body = BodyEstantesFiltro(
            listatransportadoras = listTrans.items,
            listatiposdocumentos = listDoc.items,
            listaandares = intentData.andares
        )
        viewModel.postItensEstantes(body, ideArmazem, token)

    }

    private fun setupObservables() {
        //ESTANTES -->
        viewModel.mShowShow.observe(this) { estantesComTarefas ->
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
                            list.add(it.estante)
                        }
                        intentData = RequestSeparationArraysAndares1(list)
                        onBackPressed()
                    })
            } else {
                mBinding.txtInfEstantes.visibility = View.GONE
                mAdapterEstantes.update(estantesComTarefas)
                if (getResult != null) {
                    mAdapterEstantes.setCkeckBox(getResult!!.estantes)
                }
            }
        }

        viewModel.mValidaProgressShow.observe(this) { validProgress ->
            mBinding.progress.isVisible = validProgress
        }

        viewModel.mErrorShow.observe(this) { message ->
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
                "ARRAYS_AND_EST",
                RequestSeparationArraysAndaresEstante3(intentData.andares, mListEstantes)
            )
            intent.putExtra("DOC", ItemDocTrans(listDoc.items))
            intent.putExtra("TRANS", ItemDocTrans(listTrans.items))
            Log.e(
                "Enviando tela 3",
                "DOCUMENTOS:${listDoc.items}\nTRANSPORTADORA:${listTrans.items}"
            )
            mResponseBack.launch(intent)
            extensionSendActivityanimation()
        }
    }


    /**funcao que retorna a primeira tela de separacao a lista -->*/
    private fun returSeparation1() {
        val intent = Intent()
        intent.putExtra("ARRAY_BACK", intentData)
        intent.putExtra("DOC", ItemDocTrans(listDoc.items))
        intent.putExtra("TRANS", ItemDocTrans(listTrans.items))
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        returSeparation1()
        extensionBackActivityanimation(this)
    }
}