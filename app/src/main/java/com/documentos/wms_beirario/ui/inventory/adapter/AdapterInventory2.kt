package com.documentos.wms_beirario.ui.inventory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvMovimentacao2Binding
import com.documentos.wms_beirario.model.inventario.LeituraEndInventario2List
import com.documentos.wms_beirario.utils.extensions.AppExtensions

class AdapterInventory2() :
    ListAdapter<LeituraEndInventario2List, AdapterInventory2.AdapterInventoryViewHolder2>(
        CallDiffUtilInventory2()
    ) {

    inner class AdapterInventoryViewHolder2(private val mBinding: ItemRvMovimentacao2Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(itemRv: LeituraEndInventario2List?) {
            mBinding.txtApiData.text = itemRv?.sku
            mBinding.txtApiEndereO.text = itemRv!!.codigoBarras
            mBinding.txtApiNumeroserie.text = AppExtensions.formatDataEHora(itemRv.criadoEm)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterInventoryViewHolder2 {
        val binding =
            ItemRvMovimentacao2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterInventoryViewHolder2(binding)
    }

    override fun onBindViewHolder(holder: AdapterInventoryViewHolder2, position: Int) {
        holder.bind(getItem(position))
    }
}

class CallDiffUtilInventory2 : DiffUtil.ItemCallback<LeituraEndInventario2List>() {
    override fun areItemsTheSame(
        oldItem: LeituraEndInventario2List,
        newItem: LeituraEndInventario2List
    ): Boolean {
        return oldItem.codigoBarras == newItem.codigoBarras
    }

    override fun areContentsTheSame(
        oldItem: LeituraEndInventario2List,
        newItem: LeituraEndInventario2List
    ): Boolean {
        return oldItem == newItem
    }

}
