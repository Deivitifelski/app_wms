package com.documentos.wms_beirario.ui.inventory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvCorrugadosBinding
import com.documentos.wms_beirario.model.inventario.InventoryResponseCorrugadosItem

class AdapterCorrugadosInventory(private val onClick: (InventoryResponseCorrugadosItem) -> Unit) :
    androidx.recyclerview.widget.ListAdapter<InventoryResponseCorrugadosItem, AdapterCorrugadosInventory.AdapterCorrugadosInventoryViewHolder>(
        DiffUtilCorrugado()
    ) {


    inner class AdapterCorrugadosInventoryViewHolder(val mBinding: ItemRvCorrugadosBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: InventoryResponseCorrugadosItem?) {
            mBinding.idApiDescricao.text = item!!.descricao
            mBinding.idApiQuantidade.text = item.quantidadePares.toString()
            itemView.setOnClickListener {
                onClick.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterCorrugadosInventoryViewHolder {
        val binding =
            ItemRvCorrugadosBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AdapterCorrugadosInventoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterCorrugadosInventoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUtilCorrugado() : DiffUtil.ItemCallback<InventoryResponseCorrugadosItem>() {
    override fun areItemsTheSame(
        oldItem: InventoryResponseCorrugadosItem,
        newItem: InventoryResponseCorrugadosItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: InventoryResponseCorrugadosItem,
        newItem: InventoryResponseCorrugadosItem
    ): Boolean {
        return oldItem == newItem
    }


}