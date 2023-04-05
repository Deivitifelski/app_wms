package com.documentos.wms_beirario.ui.picking.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvNumSeriePicking2Binding
import com.documentos.wms_beirario.databinding.ItemRvPedidoPicking2Binding
import com.documentos.wms_beirario.model.picking.PickingRv2

class PickingAdapterNew2() : RecyclerView.Adapter<PickingAdapterVhCab2>() {

    private var mListPickingResponse2: MutableList<PickingRv2> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingAdapterVhCab2 {
        return when (viewType) {
            TIPO_ITEM -> PickingAdapterVhCab2.ItemVh(
                ItemRvNumSeriePicking2Binding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            TIPO_CAB -> PickingAdapterVhCab2.VhCabVh(
                ItemRvPedidoPicking2Binding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalAccessError("Tipo de view inválid")
        }
    }

    override fun onBindViewHolder(holder: PickingAdapterVhCab2, position: Int) {
        when (holder) {
            is PickingAdapterVhCab2.VhCabVh -> holder.bind(mListPickingResponse2[position] as PickingRv2.CabecalhoPicking2)
            is PickingAdapterVhCab2.ItemVh -> holder.bind(mListPickingResponse2[position] as PickingRv2.ItemPicking2)

        }
    }

    override fun getItemViewType(position: Int) = when (mListPickingResponse2[position]) {
        is PickingRv2.CabecalhoPicking2 -> TIPO_CAB
        is PickingRv2.ItemPicking2 -> TIPO_ITEM
    }


    override fun getItemCount() = mListPickingResponse2.size

    companion object {
        const val TIPO_ITEM = 1
        const val TIPO_CAB = 0

    }

    fun updateData(list: MutableList<PickingRv2>) {
        this.mListPickingResponse2.clear()
        this.mListPickingResponse2 = list
        notifyDataSetChanged()
    }
}