package com.documentos.wms_beirario.ui.reservationByRequest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemReservationRequest2Binding
import com.documentos.wms_beirario.model.reservationByRequest.ListVolumerServPed1
import com.documentos.wms_beirario.utils.extensions.AppExtensions

class AdapterReservation() :
    ListAdapter<ListVolumerServPed1, AdapterReservation.AdapterReservationVh>(
        DiffReservation()
    ) {

    inner class AdapterReservationVh(val binding: ItemReservationRequest2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListVolumerServPed1) {
            with(binding) {
                dataApi.text = AppExtensions.formatDataMov(item.dataInclusaoVolume)
                endReservaApi.text = item.endereco
                numeroserieApi.text = item.numeroserie
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterReservationVh {
        val i = ItemReservationRequest2Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdapterReservationVh(i)
    }

    override fun onBindViewHolder(holder: AdapterReservationVh, position: Int) {
        holder.bind(getItem(position))
    }

}

class DiffReservation() : DiffUtil.ItemCallback<ListVolumerServPed1>() {
    override fun areItemsTheSame(
        oldItem: ListVolumerServPed1,
        newItem: ListVolumerServPed1
    ): Boolean {
        return oldItem.sku == newItem.sku && oldItem.dataInclusaoVolume == newItem.dataInclusaoVolume &&
                oldItem.numeroserie == newItem.numeroserie
    }

    override fun areContentsTheSame(
        oldItem: ListVolumerServPed1,
        newItem: ListVolumerServPed1
    ): Boolean {
        return oldItem == newItem
    }

}
