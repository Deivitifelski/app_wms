package com.documentos.wms_beirario.ui.etiquetagem.activitys

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.ActivityPendencyNf2Binding
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import com.documentos.wms_beirario.ui.etiquetagem.adapter.AdapterPending2
import com.documentos.wms_beirario.ui.etiquetagem.viewmodel.LabelingPendingFragment2ViewModel
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation

class EtiquetagemPendenciaNFActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityPendencyNf2Binding
    private lateinit var mViewModel: LabelingPendingFragment2ViewModel
    private lateinit var mAdapter: AdapterPending2


    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityPendencyNf2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        initViewModel()
        setupRecyclerView()
        mViewModel.getLabeling()
        setObservables()
        setToolbar()
    }


    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            LabelingPendingFragment2ViewModel.LabelingViewModelFactory(EtiquetagemRepository())
        )[LabelingPendingFragment2ViewModel::class.java]
    }

    private fun setToolbar() {
        mBinding.toolbar.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setObservables() {
        mViewModel.mSucessShow.observe(this) { listSucess ->
            if (listSucess.isEmpty()) {
                mBinding.linearTopTotais.visibility = View.INVISIBLE
                mBinding.lottiePendency2.visibility = View.VISIBLE
                mBinding.txtInf.visibility = View.VISIBLE
                mBinding.linearTopTotais.visibility = View.INVISIBLE
            } else {
                var totalPendencias = 0
                var totalNotas = 0
                listSucess.forEach { list ->
                    totalPendencias += list.quantidadeVolumesPendentes
                    totalNotas += list.quantidadeVolumes
                }
                mBinding.totalPedidos.text = listSucess.size.toString()
                mBinding.totalVolumes.text = totalNotas.toString()
                mBinding.totalPendencias.text = totalPendencias.toString()
                mBinding.txtInf.visibility = View.INVISIBLE
                mBinding.lottiePendency2.visibility = View.INVISIBLE
                mAdapter.submitList(listSucess)
            }

        }
        mViewModel.mErrorShow.observe(this) { messageError ->
            CustomSnackBarCustom().snackBarErrorSimples(mBinding.root, messageError)
        }
        mViewModel.mProgressShow.observe(this) { validProgress ->
            mBinding.progress.isVisible = validProgress
        }
        mViewModel.mErrorAllShow.observe(this) { messageError ->
            CustomSnackBarCustom().snackBarErrorSimples(mBinding.root, messageError)
        }
    }

    private fun setupRecyclerView() {
        //CLICK PROXIMA ACTIVITY -->
        mAdapter = AdapterPending2 { itemPendingClick ->
            val intent = Intent(this, EtiquetagemPendenciaNfActivity2::class.java)
            intent.putExtra("ITEM_ETIQUETAGEM_NF", itemPendingClick)
            startActivity(intent)
            extensionSendActivityanimation()
        }
        mBinding.rvEtiquetagem2.apply {
            layoutManager = LinearLayoutManager(this@EtiquetagemPendenciaNFActivity)
            adapter = mAdapter
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}