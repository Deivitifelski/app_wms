package com.documentos.wms_beirario.ui.qualityControl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemNotApontedQualityBinding
import com.documentos.wms_beirario.databinding.ItemRejectQualityBinding
import com.documentos.wms_beirario.model.qualityControl.Apontado
import com.documentos.wms_beirario.model.qualityControl.NaoApontado
import com.documentos.wms_beirario.model.qualityControl.NaoAprovado
import com.documentos.wms_beirario.utils.extensions.AppExtensions

class AdapterQualityControlReject() :
    ListAdapter<NaoAprovado, AdapterQualityControlReject.AdapterQualityControlRejectVh>(
        RejectQuality()
    ) {

    inner class AdapterQualityControlRejectVh(val binding: ItemRejectQualityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NaoAprovado) {
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
    ): AdapterQualityControlRejectVh {
        val i = ItemRejectQualityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterQualityControlRejectVh(i)
    }

    override fun onBindViewHolder(holder: AdapterQualityControlRejectVh, position: Int) {
        holder.bind(getItem(position))
    }
}


class RejectQuality() : DiffUtil.ItemCallback<NaoAprovado>() {
    override fun areItemsTheSame(oldItem: NaoAprovado, newItem: NaoAprovado): Boolean {
        return oldItem.sku == newItem.sku && oldItem.idEnderecoOrigem == newItem.idEnderecoOrigem && oldItem.sequencial == newItem.sequencial
    }

    override fun areContentsTheSame(oldItem: NaoAprovado, newItem: NaoAprovado): Boolean {
        return oldItem == newItem
    }

}
