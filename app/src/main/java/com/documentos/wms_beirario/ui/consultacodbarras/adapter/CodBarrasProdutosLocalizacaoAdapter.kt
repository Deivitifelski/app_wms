package com.documentos.wms_beirario.ui.consultacodbarras.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvLocalizacaoProdutoBinding
import com.documentos.wms_beirario.model.codBarras.Produtolocalizacoes


class CodBarrasProdutosLocalizacaoAdapter :
    RecyclerView.Adapter<CodBarrasProdutosLocalizacaoAdapter.ProdutosLocalizacaoViewHolder>() {

    private val mLIstProdutoLocalizacao = mutableListOf<Produtolocalizacoes>()

    inner class ProdutosLocalizacaoViewHolder(val mBinding: ItemRvLocalizacaoProdutoBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun geraItem(it: Produtolocalizacoes) {
            with(mBinding) {
                itAreaProdutoLocalizacao.text = it.area
                itEnderecoVisualProdutoLocalizacao.text = it.enderecoVisual
                itQuantidadeProdutoLocalizacao.text = it.quantidade.toString()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProdutosLocalizacaoViewHolder {
        val mBinding = ItemRvLocalizacaoProdutoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProdutosLocalizacaoViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ProdutosLocalizacaoViewHolder, position: Int) {
        holder.geraItem(mLIstProdutoLocalizacao[position])
    }


    override fun getItemCount() = mLIstProdutoLocalizacao.size

    fun update(produtolocalizacoes: List<Produtolocalizacoes>) {
        mLIstProdutoLocalizacao.clear()
        this.mLIstProdutoLocalizacao.addAll(produtolocalizacoes)
        notifyDataSetChanged()
    }


}




