package com.documentos.wms_beirario.ui.recebimento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvNaoApontadosBinding
import com.documentos.wms_beirario.model.recebimento.NumberSerieNoPonted

class AdapterNoPointer :
    ListAdapter<NumberSerieNoPonted, AdapterNoPointer.AdapterNoPontedViewHolder>(
        DiffUtilNaoApontados()
    ) {

    inner class AdapterNoPontedViewHolder(val mBinding: ItemRvNaoApontadosBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: NumberSerieNoPonted?) {
            mBinding.itNumSerieReceiptPonted.text = item!!.numeroSerie
            mBinding.itSequenReceiptPonted.text = item.sequencial.toString()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterNoPontedViewHolder {
        val binding =
            ItemRvNaoApontadosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterNoPontedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterNoPontedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUtilNaoApontados : DiffUtil.ItemCallback<NumberSerieNoPonted>() {
    override fun areItemsTheSame(
        oldItem: NumberSerieNoPonted,
        newItem: NumberSerieNoPonted
    ): Boolean {
        return oldItem.sequencial == newItem.sequencial
    }

    override fun areContentsTheSame(
        oldItem: NumberSerieNoPonted,
        newItem: NumberSerieNoPonted
    ): Boolean {
        return oldItem == newItem
    }

}