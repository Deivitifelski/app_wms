package com.documentos.wms_beirario.ui.consultacodbarras.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.FragmentVolumeBinding
import com.documentos.wms_beirario.extensions.AppExtensions
import com.documentos.wms_beirario.ui.consultacodbarras.adapter.CodBarrasVolumeAdapter
import com.example.coletorwms.model.codBarras.DistribuicaoModel
import com.example.coletorwms.model.codBarras.VolumeModelCB

class VolumeFragment : Fragment() {

    private lateinit var mAdapter : CodBarrasVolumeAdapter
    private lateinit var mData : VolumeModelCB
    private var Binding: FragmentVolumeBinding? = null
    private val mBinding get() = Binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Binding = FragmentVolumeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        getArgs()
        setTxt()
        setRecyclerView()
    }

    private fun setRecyclerView() {
        mAdapter = CodBarrasVolumeAdapter()
        mBinding.rvCodbarrasVolume.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }
        mAdapter.update(mData.distribuicao as List<DistribuicaoModel>)
    }

    private fun getArgs() {
        mData = requireArguments().getSerializable("VOLUME") as VolumeModelCB
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setTxt() {
        mBinding.apply {
            itNumerodeserieCodbarrasVol.text = mData.numeroSerie.toString()
            itDatacriacaoCodbarrasVol.text =  AppExtensions.formatDataEHora(mData.dataCriacao.toString())
            itCodEmbalagemCodbarrasVol.text = mData.codigoEmbalagem.toString()
            itDescEmbalagemCodbarrasVol.text = mData.descricaoEmbalagem.toString()
            itCodDistribuicaoCodbarrasVol.text = mData.codigoDistribuicao.toString()
            itDescDistribuicaoCodbarrasVol.text = mData.descricaoDistribuicao.toString()
            itEndVisualCodbarrasVol.text = mData.enderecoVisual.toString()
            itNomeAreaCodbarrasVol.text = mData.nomeArea.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Binding = null
    }

}