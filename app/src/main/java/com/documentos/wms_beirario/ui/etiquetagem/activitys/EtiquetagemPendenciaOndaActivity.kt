package com.documentos.wms_beirario.ui.etiquetagem.activitys

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.ActivityEtiquetagemPedidoOndaBinding
import com.documentos.wms_beirario.databinding.ActivityPendencyNf2Binding
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoriaItem3
import com.documentos.wms_beirario.model.etiquetagem.ResponsePendencyOndaEtiquetagemItem
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import com.documentos.wms_beirario.ui.etiquetagem.adapter.AdapterPending2
import com.documentos.wms_beirario.ui.etiquetagem.adapter.AdapterPendingOnda
import com.documentos.wms_beirario.ui.etiquetagem.viewmodel.LabelingPendingFragment2ViewModel
import com.documentos.wms_beirario.ui.etiquetagem.viewmodel.LabelingPendingOndaViewModel
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation

class EtiquetagemPendenciaOndaActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityEtiquetagemPedidoOndaBinding

    private lateinit var mViewModel: LabelingPendingOndaViewModel
    private lateinit var mAdapter: AdapterPendingOnda


    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityEtiquetagemPedidoOndaBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        initViewModel()
        setupRecyclerView()
        mViewModel.getLabeling()
        setObservables()
        setToolbar()
        mBinding.progress.isVisible = false
    }


    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            LabelingPendingOndaViewModel.LabelingViewModelOndaFactory(EtiquetagemRepository())
        )[LabelingPendingOndaViewModel::class.java]
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
                    totalPendencias += list.quantidadePendente
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

    private fun mockList() {
        val list = mutableListOf<ResponsePendencyOndaEtiquetagemItem>()
        list.add(ResponsePendencyOndaEtiquetagemItem("2239973493", 12, 12, 3))
        list.add(ResponsePendencyOndaEtiquetagemItem("293829389", 2, 9, 32))
        list.add(ResponsePendencyOndaEtiquetagemItem("2239973493", 19, 92, 5))
        mAdapter.submitList(list)
    }

    private fun setupRecyclerView() {
        //CLICK PROXIMA ACTIVITY -->
        mAdapter = AdapterPendingOnda()
        mBinding.rvEtiquetagem2.apply {
            layoutManager = LinearLayoutManager(this@EtiquetagemPendenciaOndaActivity)
            adapter = mAdapter
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}