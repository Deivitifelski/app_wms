package com.documentos.wms_beirario.ui.separacao.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvSeparationProdAndressBinding
import com.documentos.wms_beirario.model.separation.SeparacaoProdAndress4Item


class AdapterSeparation3 : RecyclerView.Adapter<AdapterSeparation3.ViewHolderSeparacao3>() {

    private var mListSeparacao2: MutableList<SeparacaoProdAndress4Item> = mutableListOf()

    inner class ViewHolderSeparacao3(val mBinding: ItemRvSeparationProdAndressBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun geraItem(produto: SeparacaoProdAndress4Item) {
            mBinding.pedidoApi.text = produto.pedido ?: " - "
            mBinding.numeroSerieApi.text = produto.numeroSerie ?: " - "
            mBinding.skuApi.text = produto.sku
            if (produto.codigodistribuicao == null) {
                mBinding.gradeApi.text = " - "
            } else {
                mBinding.gradeApi.text = produto.codigodistribuicao.toString()
            }
            mBinding.qntPendenteApi.text = returnCalculo(produto)
        }
    }

    private fun returnCalculo(it: SeparacaoProdAndress4Item): String {
        val result = it.quantidade - it.quantidadeApontada
        return "$result/${it.quantidade}"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSeparacao3 {
        val mBinding =
            ItemRvSeparationProdAndressBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolderSeparacao3(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolderSeparacao3, position: Int) {
        holder.geraItem(mListSeparacao2[position])
    }

    override fun getItemCount() = mListSeparacao2.size

    fun update(list: List<SeparacaoProdAndress4Item>) {
        mListSeparacao2.clear()
        mListSeparacao2.sortBy { it.codigodistribuicao }
        mListSeparacao2.addAll(list)
        notifyDataSetChanged()
    }

}







