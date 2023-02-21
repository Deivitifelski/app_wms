package com.documentos.wms_beirario.ui.qualityControl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemNotApontedQualityBinding
import com.documentos.wms_beirario.model.qualityControl.Apontado
import com.documentos.wms_beirario.model.qualityControl.NaoApontado
import com.documentos.wms_beirario.utils.extensions.AppExtensions

class AdapterQualityControlAponted() :
    ListAdapter<Apontado, AdapterQualityControlAponted.AdapterQualityControlApontedVh>(
        APontedQuality()
    ) {

    inner class AdapterQualityControlApontedVh(val binding: ItemNotApontedQualityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Apontado) {
            with(binding) {
                skuApi.text = item.sku
                qntApi.text = item.quantidade.toString()
                sequencialApiApi.text = item.sequencial.toString()
            }

        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterQualityControlApontedVh {
        val i =
            ItemNotApontedQualityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterQualityControlApontedVh(i)
    }

    override fun onBindViewHolder(holder: AdapterQualityControlApontedVh, position: Int) {
        holder.bind(getItem(position))
    }
}

class APontedQuality() : DiffUtil.ItemCallback<Apontado>() {
    override fun areItemsTheSame(oldItem: Apontado, newItem: Apontado): Boolean {
        return oldItem.sku == newItem.sku && oldItem.idEnderecoOrigem == newItem.idEnderecoOrigem && oldItem.sequencial == newItem.sequencial
    }

    override fun areContentsTheSame(oldItem: Apontado, newItem: Apontado): Boolean {
        return oldItem == newItem
    }

}
