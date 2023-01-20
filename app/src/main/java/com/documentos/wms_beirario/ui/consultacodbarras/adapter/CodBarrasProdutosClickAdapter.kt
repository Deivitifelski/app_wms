package com.documentos.wms_beirario.ui.consultacodbarras.adapter

import com.documentos.wms_beirario.model.codBarras.CodBarrasProdutoClick
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvProdutoClickBinding


class CodBarrasProdutosClickAdapter :
    RecyclerView.Adapter<CodBarrasProdutosClickAdapter.ProdutosViewHolder>() {

    private val mLIstProdutoClick = mutableListOf<CodBarrasProdutoClick>()

    inner class ProdutosViewHolder(val mBinding: ItemRvProdutoClickBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun geraItem(it: CodBarrasProdutoClick) {
            with(mBinding) {
                itSkuProdutoClick.text = it.sku
                itQuantidadeProdutoClick.text = it.quantidade.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutosViewHolder {
        val mBinding =
            ItemRvProdutoClickBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProdutosViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ProdutosViewHolder, position: Int) {
        holder.geraItem(mLIstProdutoClick[position])
    }


    override fun getItemCount() = mLIstProdutoClick.size

    fun update(produtos: List<CodBarrasProdutoClick>) {
        mLIstProdutoClick.clear()
        this.mLIstProdutoClick.addAll(produtos)
        notifyDataSetChanged()
    }


}




