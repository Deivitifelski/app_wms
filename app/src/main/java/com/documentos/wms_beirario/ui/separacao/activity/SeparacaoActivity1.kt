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
import com.documentos.wms_beirario.model.separation.filtros.BodyAndaresFiltro
import com.documentos.wms_beirario.model.separation.filtros.ItemDocTrans
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.ui.separacao.adapter.AdapterAndares
import com.documentos.wms_beirario.ui.separacao.filter.FilterSeparationActivity
import com.documentos.wms_beirario.ui.separacao.viewModel.SeparacaoViewModel1
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.alertDefaulError
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension


class SeparacaoActivity1 : AppCompatActivity() {

    private lateinit var binding: ActivitySeparacao1Binding
    private val TAG = "TESTE DE ITENS SEPARAÇAO -------->"
    private lateinit var adapterAndares: AdapterAndares
    private lateinit var viewModel: SeparacaoViewModel1
    private lateinit var token: String
    private var idArmazem: Int = 0
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private var listDoc: ItemDocTrans? = null
    private var listTrans: ItemDocTrans? = null
    private lateinit var mToast: CustomSnackBarCustom
    private var listDeAndares = mutableListOf<String>()
    private var getResult: RequestSeparationArraysAndares1? = null
    private val mResponseBack =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    getResult =
                        result.data!!.getSerializableExtra("ARRAY_BACK") as RequestSeparationArraysAndares1
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


    private val responseFilter =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    listDoc = data.getSerializableExtra("DOC") as ItemDocTrans
                    listTrans = data.getSerializableExtra("TRANS") as ItemDocTrans
                    Log.e(
                        "RETORNO FILTRO",
                        "DOCUMENTOS:${listDoc?.items}\nTRANSPORTADORA:${listTrans?.items}"
                    )
                    adapterAndares.clear()
                    callApi()
                }
            }
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
        val doc = listDoc?.items ?: listOf(null)
        val trans = listTrans?.items ?: listOf(null)
        val body = BodyAndaresFiltro(
            listatiposdocumentos = doc,
            listatransportadoras = trans
        )
        viewModel.getAndaresFiltro(
            token = token,
            idArmazem = idArmazem,
            body = body
        )
    }


    private fun initViewModel() {
        viewModel = ViewModelProvider(
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
                adapterAndares.selectAll()
            } else {
                adapterAndares.unSelectAll()
            }
        }
    }

    /**
     * INICIANDO OS ADAPTER -->
     */
    private fun initRv() {
        adapterAndares = AdapterAndares { lista ->
            validadCheckAll(lista)
            validateButton(lista)
            setupListSend(lista)
        }
        binding.apply {
            rvSeparationEstanteItems.apply {
                layoutManager = LinearLayoutManager(this@SeparacaoActivity1)
                adapter = adapterAndares
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
        listDeAndares.clear()
        lista.forEach {
            if (it.status) {
                listDeAndares.add(it.andar)
            }
        }
        listDeAndares.forEach {
            Log.e(TAG, it)
        }
    }


    private fun setupObservables() {
        viewModel.mValidaProgressShow.observe(this) { validProgress ->
            binding.progress.isVisible = validProgress
        }
        //ANDARES -->
        viewModel.mShowShow.observe(this) { itensCheckBox ->
            if (itensCheckBox.isEmpty()) {
                binding.apply {
                    txtInf.visibility = View.VISIBLE
                    filterSeparation.visibility = View.INVISIBLE
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
                binding.filterSeparation.visibility = View.VISIBLE
                binding.linearInfTotal.visibility = View.VISIBLE
                binding.linearInf.visibility = View.VISIBLE
                binding.selectAllEstantes.visibility = View.VISIBLE
                binding.txtInf.visibility = View.GONE
                binding.view.visibility = View.VISIBLE
                setTotalTxt(itensCheckBox)
                adapterAndares.update(itensCheckBox)
                if (getResult != null) {
                    adapterAndares.setCkeckBox(getResult!!.andares)
                }
            }
        }

        viewModel.mErrorShow.observe(this) { message ->
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
            intent.putExtra("ARRAYS_AND_EST", RequestSeparationArraysAndares1(listDeAndares))
            intent.putExtra("DOC", ItemDocTrans(listDoc?.items))
            intent.putExtra("TRANS", ItemDocTrans(listTrans?.items))
            Log.e(
                "Enviando tela 2",
                "DOCUMENTOS:${listDoc?.items}\nTRANSPORTADORA:${listTrans?.items}"
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