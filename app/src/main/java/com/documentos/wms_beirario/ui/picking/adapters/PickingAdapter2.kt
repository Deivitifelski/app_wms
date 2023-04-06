package com.documentos.wms_beirario.ui.picking.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvNumSeriePicking2Binding
import com.documentos.wms_beirario.model.picking.PickingResponse2


class PickingAdapter2() : RecyclerView.Adapter<PickingAdapter2.PickingViewHolder2>() {

    private var mListPickingResponse2: MutableList<PickingResponse2> = mutableListOf()

    inner class PickingViewHolder2(val mBinding: ItemRvNumSeriePicking2Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(it: PickingResponse2) {
            with(mBinding) {
                apiNumeroDeSeriePicking2.text = it.numeroSerie
                apiEndVisualPicking2.text = it.enderecoVisualOrigem
                apiPedidoPicking2.text = it.pedido
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingViewHolder2 {
        val mBinding = ItemRvNumSeriePicking2Binding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PickingViewHolder2(mBinding)
    }

    override fun onBindViewHolder(holder: PickingViewHolder2, position: Int) {
        holder.bind(mListPickingResponse2[position])
    }

    override fun getItemCount() = mListPickingResponse2.size

    //Update adapter -->
    fun update(it: List<PickingResponse2>) {
        it.sortedBy { it.pedido }
        mListPickingResponse2.addAll(it)
        notifyDataSetChanged()
    }
}