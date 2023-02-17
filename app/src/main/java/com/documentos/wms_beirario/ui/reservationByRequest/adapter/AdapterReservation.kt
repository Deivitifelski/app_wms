package com.documentos.wms_beirario.ui.reservationByRequest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemReservationRequest2Binding
import com.documentos.wms_beirario.model.reservationByRequest.ResponseReservationByPedido2

class AdapterReservation() :
    ListAdapter<ResponseReservationByPedido2, AdapterReservation.AdapterReservationVh>(
        DiffReservation()
    ) {

    inner class AdapterReservationVh(val binding: ItemReservationRequest2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseReservationByPedido2) {
            with(binding) {
                dataApi.text = item.dataInclusao
                endReservaApi.text = item.enderecoReserva
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

class DiffReservation() : DiffUtil.ItemCallback<ResponseReservationByPedido2>() {
    override fun areItemsTheSame(
        oldItem: ResponseReservationByPedido2,
        newItem: ResponseReservationByPedido2
    ): Boolean {
        return oldItem.sku == newItem.sku && oldItem.dataInclusao == newItem.dataInclusao &&
                oldItem.numeroSerie == newItem.numeroSerie
    }

    override fun areContentsTheSame(
        oldItem: ResponseReservationByPedido2,
        newItem: ResponseReservationByPedido2
    ): Boolean {
        return oldItem == newItem
    }

}
