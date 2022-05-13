package com.documentos.wms_beirario.ui.consultacodbarras.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.FragmentProdutoBinding
import com.documentos.wms_beirario.model.codBarras.CodBarrasProdutoResponseModel
import com.documentos.wms_beirario.ui.consultacodbarras.adapter.CodBarrasProdutosLocalizacaoAdapter
import com.documentos.wms_beirario.utils.CustomSnackBarCustom

class ProdutoFragment : Fragment() {

    private lateinit var mDados: CodBarrasProdutoResponseModel
    private lateinit var mAdapter: CodBarrasProdutosLocalizacaoAdapter
    private var Binding: FragmentProdutoBinding? = null
    private val mBinding get() = Binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Binding = FragmentProdutoBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        getArgs()
        setTxt()
        setRecyclerview()
    }

    private fun setRecyclerview() {
        mAdapter = CodBarrasProdutosLocalizacaoAdapter()
        mBinding.apply {
            rvProdutoFragment.adapter = mAdapter
        }
        if (mDados.Produtolocalizacoes.isNullOrEmpty()) {
            CustomSnackBarCustom().snackBarSimplesBlack(
                mBinding.layout,
                getString(R.string.list_emply)
            )
        } else {
            mAdapter.update(mDados.Produtolocalizacoes)
        }

    }

    private fun getArgs() {
        mDados = requireArguments().getSerializable("PRODUTO") as CodBarrasProdutoResponseModel
    }

    private fun setTxt() {
        mBinding.apply {
            itNomeCodbarrasProduto.text = mDados.nome
            itTamanhoCodbarrasProduto.text = mDados.tamanho
            itSkuCodbarrasProduto.text = mDados.sku
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Binding = null
    }

}