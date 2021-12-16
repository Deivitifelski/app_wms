package com.documentos.wms_beirario.ui.inventory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvInventario1Binding
import com.documentos.wms_beirario.model.inventario.ResponseInventoryPending1
import com.documentos.wms_beirario.utils.extensions.AppExtensions

class AdapterInventario1(private val onClick:(ResponseInventoryPending1) -> Unit):ListAdapter<ResponseInventoryPending1, AdapterInventario1.AdapterViewHolder1>(CallBackDiffUtil()) {


    inner class AdapterViewHolder1(val mBinding : ItemRvInventario1Binding):RecyclerView.ViewHolder(mBinding.root){
        fun bind(listpending: ResponseInventoryPending1?) {
            with(mBinding){
                itDocumentoInventario1.text = listpending!!.documento.toString()
                itDateInventario1.text = AppExtensions.formatData(listpending.dataHora)
                itHoraInventario1.text = AppExtensions.formatHora(listpending.dataHora)
            }

            itemView.setOnClickListener {
                onClick.invoke(listpending!!)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder1 {
        val binding = ItemRvInventario1Binding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AdapterViewHolder1(mBinding = binding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder1, position: Int) {
        val list = getItem(position)
        holder.bind(getItem(position))
    }
}

private class CallBackDiffUtil : DiffUtil.ItemCallback<ResponseInventoryPending1>() {
    override fun areItemsTheSame(
        oldItem: ResponseInventoryPending1,
        newItem: ResponseInventoryPending1
    ): Boolean {
        return newItem.id == oldItem.id
    }

    override fun areContentsTheSame(
        oldItem: ResponseInventoryPending1,
        newItem: ResponseInventoryPending1
    ): Boolean {
      return oldItem == newItem
    }

}
