package com.documentos.wms_beirario.ui.separacao.activity

import SeparacaoViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.appwmsbeirario.model.separation.SeparationListCheckBox
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivitySeparacao1Binding
import com.documentos.wms_beirario.model.separation.ResponseItemsSeparationItem
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.ui.separacao.adapter.AdapterSeparacaoItens
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersion
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension


class SeparacaoActivity1 : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivitySeparacao1Binding
    private val TAG = "TESTE DE ITENS SEPARAÇAO -------->"
    private lateinit var mAdapter: AdapterSeparacaoItens
    private lateinit var mViewModel: SeparacaoViewModel
    private var mListstreets = mutableListOf<String>()
    private lateinit var mShared: CustomSharedPreferences
    private var mValidaCheckBox: Boolean = false
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private val mResponseBack =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val result =
                    result.data!!.getSerializableExtra("DATA_SEPARATION") as SeparationListCheckBox
                Log.e(TAG, "RESULT ACTIVITY 2 --> $result")
                mAdapter.setCkeckBox(result.estantesCheckBox)
                for (element in result.estantesCheckBox) {
                    Log.e(TAG, element)
                }
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
        setupObservables()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this, SeparacaoViewModel.SeparacaoItensViewModelFactory(
                SeparacaoRepository()
            )
        )[SeparacaoViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        callApi()
        initRv()
        validateButton()
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
        mBinding.lottie.visibility = View.INVISIBLE
        mBinding.txtInf.isVisible = false
        mBinding.buttonNext.setOnClickListener(this)
        mShared = CustomSharedPreferences(this)
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }


    private fun initRv() {
        mAdapter = AdapterSeparacaoItens { _, _ ->
//            setupSelectAll()
            validateButton()
        }
        mBinding.rvSeparationItems.apply {
            layoutManager = LinearLayoutManager(this@SeparacaoActivity1)
            adapter = mAdapter
        }
    }

    private fun validateButton() {
        mBinding.buttonNext.isEnabled =
            mAdapter.mListItensClicksSelect.isNotEmpty()
    }

    private fun callApi() {
        mViewModel.getItemsSeparation()
    }

    private fun setupObservables() {
        mViewModel.mValidaTxtShow.observe(this, { validaTxt ->
            if (validaTxt) {
                mBinding.txtInf.visibility = View.VISIBLE
            } else {
                mBinding.txtInf.text = "Você não possui tarefas"
            }
        })
        mViewModel.mValidaProgressShow.observe(this) { validProgress ->
            mBinding.progress.isVisible = validProgress
        }
        mViewModel.mShowShow.observe(this, { itensCheckBox ->
            mBinding.txtInf.isVisible = true
            if (itensCheckBox.isEmpty()) {
                mBinding.txtInf.text = "Você não possui tarefas"
                mBinding.lottie.visibility = View.VISIBLE
            } else {
                mBinding.txtInf.text = "Selecione a rua"
                mBinding.txtInf.visibility = View.VISIBLE
                mBinding.lottie.visibility = View.INVISIBLE
                itensCheckBox.map { list ->
                    mListstreets.add(list.estante)
                }
                setRecyclerView(itensCheckBox)
            }
        })

        mViewModel.mErrorShow.observe(this, { message ->
            CustomSnackBarCustom().snackBarPadraoSimplesBlack(mBinding.layoutParent, message)
        })
    }

    private fun setRecyclerView(itensCheckBox: List<ResponseItemsSeparationItem>) {
        mAdapter.submitList(itensCheckBox)
    }

    //CLICK BUTTON -->
    override fun onClick(button: View?) {
        when (button) {
            mBinding.buttonNext -> {
                val intent = Intent(this, SeparacaoActivity2::class.java)
                intent.putExtra("send", SeparationListCheckBox(mAdapter.mListItensClicksSelect))
                Log.e(TAG, "enviando --> $intent")
                mResponseBack.launch(intent)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mListstreets.clear()
        Log.e(TAG, mListstreets.toString())
    }
}