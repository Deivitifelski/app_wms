package com.documentos.wms_beirario.ui.auditoriaEstoque.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvEnderecosAuditoriaEstoqueBinding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListEnderecosAuditoriaEstoque3Item

class AdapterAuditoriaEstoque3() :
    RecyclerView.Adapter<AdapterAuditoriaEstoque3.AdapterAuditoriaEstoque3Vh>() {

    private var list = mutableListOf<ListEnderecosAuditoriaEstoque3Item>()

    inner class AdapterAuditoriaEstoque3Vh(val binding: ItemRvEnderecosAuditoriaEstoqueBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListEnderecosAuditoriaEstoque3Item) {
            binding.enderecoVisualApi.text = item.enderecoVisual
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterAuditoriaEstoque3Vh {
        val i = ItemRvEnderecosAuditoriaEstoqueBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdapterAuditoriaEstoque3Vh(i)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: AdapterAuditoriaEstoque3Vh, position: Int) {
        holder.bind(list[position])
    }

    fun update(listUpdate: List<ListEnderecosAuditoriaEstoque3Item>?) {
        list.clear()
        if (listUpdate != null) {
            list.addAll(listUpdate)
        }
        notifyDataSetChanged()
    }

    fun contaisInList(scan: String): ListEnderecosAuditoriaEstoque3Item? {
        return list.firstOrNull {
            it.codigoBarrasEndereco == scan
        }
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }
}