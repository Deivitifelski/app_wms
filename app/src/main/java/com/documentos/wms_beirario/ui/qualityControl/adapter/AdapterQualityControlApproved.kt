package com.documentos.wms_beirario.ui.qualityControl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemApprovedQualityBinding
import com.documentos.wms_beirario.databinding.ItemNotApontedQualityBinding
import com.documentos.wms_beirario.model.qualityControl.Apontado
import com.documentos.wms_beirario.model.qualityControl.Aprovado
import com.documentos.wms_beirario.model.qualityControl.NaoApontado
import com.documentos.wms_beirario.utils.extensions.AppExtensions

class AdapterQualityControlApproved() :
    ListAdapter<Aprovado, AdapterQualityControlApproved.AdapterQualityControlApprovedVh>(
        ApprovedQuality()
    ) {

    inner class AdapterQualityControlApprovedVh(val binding: ItemApprovedQualityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Aprovado) {
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
    ): AdapterQualityControlApprovedVh {
        val i =
            ItemApprovedQualityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterQualityControlApprovedVh(i)
    }

    override fun onBindViewHolder(holder: AdapterQualityControlApprovedVh, position: Int) {
        holder.bind(getItem(position))
    }
}

class ApprovedQuality() : DiffUtil.ItemCallback<Aprovado>() {
    override fun areItemsTheSame(oldItem: Aprovado, newItem: Aprovado): Boolean {
        return oldItem.sku == newItem.sku && oldItem.idEnderecoOrigem == newItem.idEnderecoOrigem && oldItem.sequencial == newItem.sequencial
    }

    override fun areContentsTheSame(oldItem: Aprovado, newItem: Aprovado): Boolean {
        return oldItem == newItem
    }

}
