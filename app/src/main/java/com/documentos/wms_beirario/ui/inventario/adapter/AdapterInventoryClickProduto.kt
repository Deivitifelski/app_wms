package com.documentos.wms_beirario.ui.inventario.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvInventarioProdutoBinding
import com.documentos.wms_beirario.model.inventario.ProdutoResponseInventarioItem


class AdapterInventoryClickProduto() :
    ListAdapter<ProdutoResponseInventarioItem, AdapterInventoryClickProduto.AdapterInventoryViewHolderProduto>(
        CallDiffUtilInventoryRvProdutos()
    ) {

    inner class AdapterInventoryViewHolderProduto(private val mBinding: ItemRvInventarioProdutoBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(itemRvProduto: ProdutoResponseInventarioItem?) {

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterInventoryViewHolderProduto {
        val binding =
            ItemRvInventarioProdutoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AdapterInventoryViewHolderProduto(binding)
    }

    override fun onBindViewHolder(holder: AdapterInventoryViewHolderProduto, position: Int) {
        holder.bind(getItem(position))
    }
}

class CallDiffUtilInventoryRvProdutos : DiffUtil.ItemCallback<ProdutoResponseInventarioItem>() {
    override fun areItemsTheSame(
        oldItem: ProdutoResponseInventarioItem,
        newItem: ProdutoResponseInventarioItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ProdutoResponseInventarioItem,
        newItem: ProdutoResponseInventarioItem
    ): Boolean {
        return oldItem == newItem
    }


}

