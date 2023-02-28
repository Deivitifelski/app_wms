package com.documentos.wms_beirario.ui.reservationByRequest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemReservationRequest2Binding
import com.documentos.wms_beirario.model.reservationByRequest.VolumesReservedRequest
import com.documentos.wms_beirario.utils.extensions.AppExtensions

class AdapterReservation() :
    ListAdapter<VolumesReservedRequest, AdapterReservation.AdapterReservationVh>(
        DiffReservation()
    ) {

    inner class AdapterReservationVh(val binding: ItemReservationRequest2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VolumesReservedRequest) {
            with(binding) {
                dataApi.text = AppExtensions.formatDataEHora(item.dataHoraInclusao)
                endReservaApi.text = item.endereco
                numeroserieApi.text = item.numeroSerie
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

class DiffReservation() : DiffUtil.ItemCallback<VolumesReservedRequest>() {
    override fun areItemsTheSame(
        oldItem: VolumesReservedRequest,
        newItem: VolumesReservedRequest
    ): Boolean {
        return oldItem.sku == newItem.sku && oldItem.dataHoraInclusao == newItem.dataHoraInclusao &&
                oldItem.numeroSerie == newItem.numeroSerie
    }

    override fun areContentsTheSame(
        oldItem: VolumesReservedRequest,
        newItem: VolumesReservedRequest
    ): Boolean {
        return oldItem == newItem
    }

}
