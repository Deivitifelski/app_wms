package com.documentos.wms_beirario.ui.qualityControl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemNotApontedQualityBinding
import com.documentos.wms_beirario.model.qualityControl.NaoApontado

class AdapterQualityControlNotAponted() :
    ListAdapter<NaoApontado, AdapterQualityControlNotAponted.AdapterQualityControlNotApontedVh>(
        NotAPontedQuality()
    ) {

    inner class AdapterQualityControlNotApontedVh(val binding: ItemNotApontedQualityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NaoApontado) {
            with(binding) {
                skuApi.text = item.sku
                qntApi.text = item.quantidade.toString()
            }

        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterQualityControlNotApontedVh {
        val i =
            ItemNotApontedQualityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterQualityControlNotApontedVh(i)
    }

    override fun onBindViewHolder(holder: AdapterQualityControlNotApontedVh, position: Int) {
        holder.bind(getItem(position))
    }
}

class NotAPontedQuality() : DiffUtil.ItemCallback<NaoApontado>() {
    override fun areItemsTheSame(oldItem: NaoApontado, newItem: NaoApontado): Boolean {
        return oldItem.sku == newItem.sku && oldItem.idEnderecoOrigem == newItem.idEnderecoOrigem && oldItem.sequencial == newItem.sequencial
    }

    override fun areContentsTheSame(oldItem: NaoApontado, newItem: NaoApontado): Boolean {
        return oldItem == newItem
    }

}
