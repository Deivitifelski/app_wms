package com.documentos.wms_beirario.ui.receipt.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvApontadosBinding
import com.documentos.wms_beirario.model.recebimento.response.NumberSeriePonted

class AdapterPointed :
    ListAdapter<NumberSeriePonted, AdapterPointed.AdapterPontedViewHolder>(
        DiffUtilApontados()
    ) {

    inner class AdapterPontedViewHolder(val mBinding: ItemRvApontadosBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: NumberSeriePonted?) {
            mBinding.itNumSerieReceiptPonted.text = item!!.numeroSerie
            mBinding.itSequenReceiptPonted.text = item.sequencial.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPontedViewHolder {
        val binding =
            ItemRvApontadosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterPontedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterPontedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUtilApontados : DiffUtil.ItemCallback<NumberSeriePonted>() {
    override fun areItemsTheSame(
        oldItem: NumberSeriePonted,
        newItem: NumberSeriePonted
    ): Boolean {
        return oldItem.sequencial == newItem.sequencial
    }

    override fun areContentsTheSame(
        oldItem: NumberSeriePonted,
        newItem: NumberSeriePonted
    ): Boolean {
        return oldItem == newItem
    }

}
