package com.documentos.wms_beirario.ui.picking.adapters

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.documentos.wms_beirario.databinding.ItemRvNumSeriePicking2Binding
import com.documentos.wms_beirario.databinding.ItemRvPedidoPicking2Binding
import com.documentos.wms_beirario.model.picking.PickingRv2

sealed class PickingAdapterVhCab2(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {


    class VhCabVh(private val binding: ItemRvPedidoPicking2Binding) :
        PickingAdapterVhCab2(binding) {

        fun bind(item: PickingRv2.CabecalhoPicking2) {
            binding.apiPedidoPicking2.text = item.pedido
            binding.apiEndVisualPicking2.text = item.enderecoVisualOrigem
        }

    }

    class ItemVh(private val binding: ItemRvNumSeriePicking2Binding) :
        PickingAdapterVhCab2(binding) {
        fun bind(item: PickingRv2.ItemPicking2) {
            binding.apiNumeroDeSeriePicking2.text = item.numeroSerie
        }

    }
}