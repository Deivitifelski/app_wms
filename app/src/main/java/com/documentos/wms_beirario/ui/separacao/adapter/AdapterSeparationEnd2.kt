package com.documentos.wms_beirario.ui.separacao.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvSeparacaoEndBinding
import com.documentos.wms_beirario.model.separation.ResponseEstantesAndaresSeparation2Item
import com.documentos.wms_beirario.model.separation.ResponseSeparationNew


class AdapterSeparationEnd2 : RecyclerView.Adapter<AdapterSeparationEnd2.ViewHolderSeparacao2>() {

    private var mListSeparacao2: MutableList<ResponseEstantesAndaresSeparation2Item> =
        mutableListOf()

    inner class ViewHolderSeparacao2(val mBinding: ItemRvSeparacaoEndBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun geraItem(it: ResponseEstantesAndaresSeparation2Item) {

            mBinding.itEnderecoSeparacao2.text = it.ENDERECO_VISUAL_ORIGEM
            mBinding.itQuantidadeSeparacao2.text = it.QUANTIDADE.toString()
            if (it.FLAG_RESTANTE_SALDO == 0) {
                mBinding.imagemOkSeparacao2.visibility = View.INVISIBLE
            } else {
                mBinding.imagemOkSeparacao2.visibility = View.VISIBLE
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

    fun update(list: ResponseSeparationNew) {
        mListSeparacao2.clear()
        list.sortedBy { it.ENDERECO_VISUAL_ORIGEM }
        mListSeparacao2.addAll(list)
        notifyDataSetChanged()
    }

    fun getSize() = mListSeparacao2

    fun searchSeparation(qrCode: String): ResponseEstantesAndaresSeparation2Item? {
        return mListSeparacao2.firstOrNull { list ->
            list.CODIGO_BARRAS_ENDERECO_ORIGEM == qrCode
        }
    }
}







