package com.documentos.wms_beirario.ui.desmontagemdevolumes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvDesmontagemVol1Binding
import com.documentos.wms_beirario.model.desmontagemdevolumes.DisassemblyResponse1

class AdapterDisassembly1() :
    ListAdapter<DisassemblyResponse1, AdapterDisassembly1.AdapterDisassemblyVH1>(
        DiffUtilDisaseembly1()
    ) {

    inner class AdapterDisassemblyVH1(val mBInding: ItemRvDesmontagemVol1Binding) :
        RecyclerView.ViewHolder(mBInding.root) {
        fun bind(item: DisassemblyResponse1?) {
            mBInding.itAreaDesmontagemVol.text = item!!.siglaArea
            mBInding.itEndereOvisualDesmontagemVol.text = item.enderecoVisual
            mBInding.itQuantidadeDesmontagemVol.text = item.quantidadeVolumes.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDisassemblyVH1 {
        val binding =
            ItemRvDesmontagemVol1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterDisassemblyVH1(binding)
    }

    override fun onBindViewHolder(holder: AdapterDisassemblyVH1, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUtilDisaseembly1() : DiffUtil.ItemCallback<DisassemblyResponse1>() {
    override fun areItemsTheSame(
        oldItem: DisassemblyResponse1,
        newItem: DisassemblyResponse1
    ): Boolean {
        return oldItem.idArea == newItem.idArea
    }

    override fun areContentsTheSame(
        oldItem: DisassemblyResponse1,
        newItem: DisassemblyResponse1
    ): Boolean {
        return oldItem == newItem
    }

}
