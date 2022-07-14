package com.documentos.wms_beirario.ui.mountingVol.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvMontagemVol1Binding
import com.documentos.wms_beirario.model.mountingVol.MountingTaskResponse1

class AdapterMounting1(private val onClick: (MountingTaskResponse1) -> Unit) :
    ListAdapter<MountingTaskResponse1, AdapterMounting1.AdapterMounting1VH>(DiffUtillMOunting1()) {

    inner class AdapterMounting1VH(val mBinding: ItemRvMontagemVol1Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: MountingTaskResponse1?) {
            mBinding.itNomeMontagemVol1.text = item?.nome
            mBinding.itQuantidadeMontagemVol1.text = item?.quantidade.toString()
            itemView.setOnClickListener {
                if (item != null) {
                    onClick.invoke(item)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMounting1VH {
        val binding =
            ItemRvMontagemVol1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterMounting1VH(binding)
    }

    override fun onBindViewHolder(holder: AdapterMounting1VH, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUtillMOunting1() : DiffUtil.ItemCallback<MountingTaskResponse1>() {
    override fun areItemsTheSame(
        oldItem: MountingTaskResponse1,
        newItem: MountingTaskResponse1
    ): Boolean {
        return oldItem.idProdutoKit == newItem.idProdutoKit
    }

    override fun areContentsTheSame(
        oldItem: MountingTaskResponse1,
        newItem: MountingTaskResponse1
    ): Boolean {
        return oldItem == newItem
    }

}
