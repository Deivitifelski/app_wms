package com.documentos.wms_beirario.ui.etiquetagem.activitys

import AdapterLabeling3
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.ActivityEtiquetagemPendenciaNf2Binding
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequestModel3
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemResponse2
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import com.documentos.wms_beirario.ui.etiquetagem.viewmodel.Labeling3ViewModel
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.vibrateExtension

class EtiquetagemPendenciaNfActivity2 : AppCompatActivity() {
    private lateinit var mBinding: ActivityEtiquetagemPendenciaNf2Binding
    private lateinit var mViewModel: Labeling3ViewModel
    private lateinit var mAdapter: AdapterLabeling3
    private lateinit var mDataIntent: EtiquetagemResponse2


    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityEtiquetagemPendenciaNf2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        initViewModel()
        AppExtensions.visibilityProgressBar(mBinding.progress, visibility = true)
        initIntent()
        setRecyclerView()
        setupToolbar()
        callApi()
        setupObservables()
        mBinding.txtInf.visibility = View.GONE
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            Labeling3ViewModel.Etiquetagem3ViewModelFactory(EtiquetagemRepository())
        )[Labeling3ViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        setRecyclerView()
        callApi()
    }


    private fun setRecyclerView() {
        mAdapter = AdapterLabeling3()
        mBinding.rvLabeling3.apply {
            layoutManager = LinearLayoutManager(this@EtiquetagemPendenciaNfActivity2)
            adapter = mAdapter
        }
    }

    private fun initIntent() {
        try {
            if (intent != null) {
                val data =
                    intent.getSerializableExtra("ITEM_ETIQUETAGEM_NF") as EtiquetagemResponse2
                mDataIntent = data
            }
        } catch (e: Exception) {
            mErrorToast("Erro ao receber dados tela anterior: $e")
        }
    }

    private fun mErrorToast(toString: String) {
        vibrateExtension(500)
        CustomSnackBarCustom().toastCustomSucess(this, toString)
    }

    private fun setupToolbar() {
        mBinding.toolbar.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun callApi() {
        mViewModel.getLabeling3(
            etiquetagemRequestModel3 = EtiquetagemRequestModel3(
                mDataIntent.empresa,
                mDataIntent.filial,
                mDataIntent.numeroNotaFiscal,
                mDataIntent.serieNotaFiscal,
            )
        )
    }

    private fun setupObservables() {
        mViewModel.mErrorShow.observe(this) { messageError ->
            CustomSnackBarCustom().snackBarErrorSimples(mBinding.root, messageError)
        }
        mViewModel.mSucessShow.observe(this) { listSucess ->
            if (listSucess.isEmpty()) {
                mBinding.txtInf.visibility = View.VISIBLE
            } else {
                mBinding.txtInf.visibility = View.GONE
                mAdapter.submitList(listSucess)
            }
        }
        mViewModel.mValidProgressShow.observe(this) { validProgress ->
            if (validProgress)
                mBinding.progress.visibility = View.VISIBLE
            else
                mBinding.progress.visibility = View.INVISIBLE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

}