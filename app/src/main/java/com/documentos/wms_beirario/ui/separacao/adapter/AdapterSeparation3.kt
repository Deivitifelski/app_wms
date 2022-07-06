package com.documentos.wms_beirario.ui.separacao.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvSeparationProdAndressBinding
import com.documentos.wms_beirario.model.separation.ResponseListCheckBoxItem
import com.documentos.wms_beirario.model.separation.SeparacaoProdAndress4Item


class AdapterSeparation3 : RecyclerView.Adapter<AdapterSeparation3.ViewHolderSeparacao3>() {

    private var mListSeparacao2: MutableList<SeparacaoProdAndress4Item> = mutableListOf()

    inner class ViewHolderSeparacao3(val mBinding: ItemRvSeparationProdAndressBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun geraItem(it: SeparacaoProdAndress4Item) {
            mBinding.skuApi.text = it.sku
            mBinding.qntApi.text = it.quantidadeSeparar.toString()
        }
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
        mListSeparacao2.sortBy { it.quantidadeSeparar }
        mListSeparacao2.addAll(list)
        notifyDataSetChanged()
    }

}







