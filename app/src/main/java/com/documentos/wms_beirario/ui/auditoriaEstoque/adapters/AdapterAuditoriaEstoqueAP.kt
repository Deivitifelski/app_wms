package com.documentos.wms_beirario.ui.auditoriaEstoque.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvAuditoriaProdutoApBinding
import com.documentos.wms_beirario.databinding.ItemRvDistribuicaoApBinding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.DistribuicaoAp
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseProdutoEnderecoAuditoriaEstoqueApCreate

class AdapterAuditoriaEstoqueAP() :
    RecyclerView.Adapter<AdapterAuditoriaEstoqueAP.AdapterAuditoriaEstoqueAPVH>() {

    private var list = mutableListOf<ResponseProdutoEnderecoAuditoriaEstoqueApCreate>()

    inner class AdapterAuditoriaEstoqueAPVH(val binding: ItemRvAuditoriaProdutoApBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseProdutoEnderecoAuditoriaEstoqueApCreate) {
            binding.gradeApi.text = item.codigoGrade
            binding.skuApi.text = item.skuProduto
            binding.volumesApi.text = "${item.quantidadeApontada}/${item.quantidadeAuditada}"
            binding.tipoProdutoApi.text = item.tipoProduto
            binding.rvDistribuicao.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = InnerAuditoriaEstoqueAP(item.listDist)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterAuditoriaEstoqueAPVH {
        val i = ItemRvAuditoriaProdutoApBinding.inflate(
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

    fun update(listUpdate: List<ResponseProdutoEnderecoAuditoriaEstoqueApCreate>?) {
        list.clear()
        if (listUpdate != null) {
            list.addAll(listUpdate)
        }
        notifyDataSetChanged()
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }
}


class InnerAuditoriaEstoqueAP(distribuicao: DistribuicaoAp) :
    RecyclerView.Adapter<InnerAuditoriaEstoqueAP.InnerAuditoriaEstoqueAPVH>() {

    private val list = mutableListOf<DistribuicaoAp>()

    inner class InnerAuditoriaEstoqueAPVH(val binding: ItemRvDistribuicaoApBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindInner(distribuicaoInner: DistribuicaoAp) {
            with(binding) {
                quantidadeParesApi.text = distribuicaoInner.listaQuantidade.toString()
                numeroCalAdoApi.text = distribuicaoInner.listaTamanho.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerAuditoriaEstoqueAPVH {
        val i =
            ItemRvDistribuicaoApBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InnerAuditoriaEstoqueAPVH(i)
    }

    override fun onBindViewHolder(holder: InnerAuditoriaEstoqueAPVH, position: Int) {
        holder.bindInner(list[position])
    }

    override fun getItemCount() = list.size

}