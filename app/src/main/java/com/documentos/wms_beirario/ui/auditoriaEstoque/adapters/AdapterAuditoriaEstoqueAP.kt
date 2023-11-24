package com.documentos.wms_beirario.ui.auditoriaEstoque.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvAuditoriaEstoque1Binding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasEstoque
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasItem
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseProdutoEnderecoAuditoriaEstoqueAp
import com.documentos.wms_beirario.utils.extensions.AppExtensions

class AdapterAuditoriaEstoqueAP() :
    RecyclerView.Adapter<AdapterAuditoriaEstoqueAP.AdapterAuditoriaEstoqueAPVH>() {

    private var list = mutableListOf<ResponseProdutoEnderecoAuditoriaEstoqueAp>()

    inner class AdapterAuditoriaEstoqueAPVH(val binding: ItemRvAuditoriaEstoque1Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseProdutoEnderecoAuditoriaEstoqueAp) {


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterAuditoriaEstoqueAPVH {
        val i = ItemRvAuditoriaEstoque1Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdapterAuditoriaEstoqueAPVH(i)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: AdapterAuditoriaEstoqueAPVH, position: Int) {
        holder.bind(list[position])
    }

    fun update(listUpdate: ResponseProdutoEnderecoAuditoriaEstoqueAp?) {
        list.clear()
        if (listUpdate != null) {
            list.addAll(listOf(listUpdate))
        }
        notifyDataSetChanged()
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }
}