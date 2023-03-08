package com.documentos.wms_beirario.ui.separacao.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvSeparacaoEndBinding
import com.documentos.wms_beirario.model.separation.ResponseEstantesAndaresSeparation3Item
import com.documentos.wms_beirario.model.separation.ResponseTarefasANdaresSEparation3


class AdapterSeparationEnd2 : RecyclerView.Adapter<AdapterSeparationEnd2.ViewHolderSeparacao2>() {

    private var mListSeparacao2: MutableList<ResponseEstantesAndaresSeparation3Item> =
        mutableListOf()

    inner class ViewHolderSeparacao2(val mBinding: ItemRvSeparacaoEndBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun geraItem(it: ResponseEstantesAndaresSeparation3Item) {

            mBinding.itEnderecoSeparacao2.text = it.enderecoVisual
            mBinding.itQuantidadeSeparacao2.text = it.quantidadeProdutos.toString()
            if (it.esvaziar == 0) {
                mBinding.imagemOkSeparacao2.visibility = View.VISIBLE
            } else {
                mBinding.imagemOkSeparacao2.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSeparacao2 {
        val mBinding =
            ItemRvSeparacaoEndBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderSeparacao2(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolderSeparacao2, position: Int) {
        holder.geraItem(mListSeparacao2[position])
    }

    override fun getItemCount() = mListSeparacao2.size

    fun update(list: ResponseTarefasANdaresSEparation3) {
        mListSeparacao2.clear()
        list.sortedBy { it.enderecoVisual }
        mListSeparacao2.addAll(list)
        notifyDataSetChanged()
    }

    fun getSize() = mListSeparacao2

    fun searchSeparation(qrCode: String): ResponseEstantesAndaresSeparation3Item? {
        return mListSeparacao2.firstOrNull { list ->
            list.codBarrasEndOrigem == qrCode
        }
    }
}







