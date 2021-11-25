package com.documentos.wms_beirario.ui.separacao.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvSeparacaoEndBinding
import com.documentos.wms_beirario.model.separation.ResponseListCheckBoxItem

class AdapterSeparationEnd : RecyclerView.Adapter<AdapterSeparationEnd.ViewHolderSeparacao2>() {

    private var mListSeparacao2: MutableList<ResponseListCheckBoxItem> = mutableListOf()

    inner class ViewHolderSeparacao2(val mBinding: ItemRvSeparacaoEndBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun geraItem(it: ResponseListCheckBoxItem) {

            mBinding.itEnderecoSeparacao2.text = it.enderecoVisualOrigem
            mBinding.itQuantidadeSeparacao2.text = it.quantidadeSeparar.toString()
            if (it.flagRestanteSaldo == 0) {
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


    fun update(list: List<ResponseListCheckBoxItem>) {
        mListSeparacao2.clear()
        mListSeparacao2.addAll(list)
        notifyDataSetChanged()

    }

    fun searchSeparation(qrCode: String): ResponseListCheckBoxItem? {
        return mListSeparacao2.firstOrNull { list->
            list.codigoBarrasEnderecoOrigem == qrCode
        }
    }
}







