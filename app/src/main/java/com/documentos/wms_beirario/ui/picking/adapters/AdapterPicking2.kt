package com.documentos.wms_beirario.ui.picking.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvPicking1Binding
import com.documentos.wms_beirario.model.picking.PickingResponse2


class AdapterPicking2() :
    ListAdapter<PickingResponse2, AdapterPicking2.PickingViewHolder2>(DiffUtilPicking2()) {


    inner class PickingViewHolder2(val mBinding: ItemRvPicking1Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(it: PickingResponse2) {
            with(mBinding) {
                apiNumeroDeSeriePicking2.text = it.numeroSerie
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingViewHolder2 {
        val mBinding =
            ItemRvPicking1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PickingViewHolder2(mBinding)
    }

    override fun onBindViewHolder(holder: PickingViewHolder2, position: Int) {
        holder.bind(getItem(position))
    }


}

class DiffUtilPicking2() : DiffUtil.ItemCallback<PickingResponse2>() {
    override fun areItemsTheSame(oldItem: PickingResponse2, newItem: PickingResponse2): Boolean {
        return oldItem.numeroSerie == newItem.numeroSerie
    }

    override fun areContentsTheSame(oldItem: PickingResponse2, newItem: PickingResponse2): Boolean {
        return oldItem == newItem
    }

}
