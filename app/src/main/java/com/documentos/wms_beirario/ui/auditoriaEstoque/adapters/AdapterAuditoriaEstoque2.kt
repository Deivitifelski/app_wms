package com.documentos.wms_beirario.ui.auditoriaEstoque.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvAuditoriaEstantesBinding
import com.documentos.wms_beirario.databinding.ItemRvAuditoriaEstoque1Binding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListEstantesAuditoriaEstoqueItem
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasEstoque

class AdapterAuditoriaEstoque2(private val onClick:(ListEstantesAuditoriaEstoqueItem) -> Unit) :
    RecyclerView.Adapter<AdapterAuditoriaEstoque2.AdapterAuditoriaEstoqueVh>() {

    private var list = mutableListOf<ListEstantesAuditoriaEstoqueItem>()

    inner class AdapterAuditoriaEstoqueVh(val binding: ItemRvAuditoriaEstantesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListEstantesAuditoriaEstoqueItem) {
               binding.estantes.text = item.estante

            binding.row.setOnClickListener {
                onClick.invoke(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterAuditoriaEstoqueVh {
        val i = ItemRvAuditoriaEstantesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdapterAuditoriaEstoqueVh(i)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: AdapterAuditoriaEstoqueVh, position: Int) {
        holder.bind(list[position])
    }

    fun update(listUpdate: List<ListEstantesAuditoriaEstoqueItem>?) {
        list.clear()
        if (listUpdate != null) {
            list.addAll(listUpdate)
        }
        notifyDataSetChanged()
    }
}