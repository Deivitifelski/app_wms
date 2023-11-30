package com.documentos.wms_beirario.ui.auditoriaEstoque.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ItemRvAuditoriaProdutoApBinding
import com.documentos.wms_beirario.databinding.ItemRvAuditoriaProdutoCvBinding
import com.documentos.wms_beirario.databinding.ItemRvDistribuicaoApBinding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.DistribuicaoAP
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseAuditoriaEstoqueAP
import com.documentos.wms_beirario.utils.extensions.AppExtensions

class AdapterAuditoriaEstoqueCv() :
    RecyclerView.Adapter<AdapterAuditoriaEstoqueCv.AdapterAuditoriaEstoqueCvVh>() {

    private var list = mutableListOf<ResponseAuditoriaEstoqueAP>()

    inner class AdapterAuditoriaEstoqueCvVh(val binding: ItemRvAuditoriaProdutoCvBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseAuditoriaEstoqueAP) {
            binding.gradeApi.text = item.codigoGrade
            binding.skuApi.text = item.skuProduto
            binding.qtdApontApi.text =
                if (item.quantidadeApontada != null) item.quantidadeApontada.toString() else "-"

            binding.typeProdApi.text = item.tipoProduto
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterAuditoriaEstoqueCvVh {
        val i = ItemRvAuditoriaProdutoCvBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdapterAuditoriaEstoqueCvVh(i)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: AdapterAuditoriaEstoqueCvVh, position: Int) {
        holder.bind(list[position])
    }

    fun update(listUpdate: List<ResponseAuditoriaEstoqueAP>?) {
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

    override fun getItemViewType(position: Int): Int {
        return position
    }
}


class InnerAuditoriaEstoqueCv(distribuicao: List<DistribuicaoAP>) :
    RecyclerView.Adapter<InnerAuditoriaEstoqueCv.InnerAuditoriaEstoqueCvVh>() {

    private val list = distribuicao


    inner class InnerAuditoriaEstoqueCvVh(val binding: ItemRvDistribuicaoApBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindInner(distribuicaoInner: DistribuicaoAP) {
            with(binding) {
                quantidadeParesApi.text = distribuicaoInner.quantidade
                numeroCalAdoApi.text = distribuicaoInner.tamanho
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerAuditoriaEstoqueCvVh {
        val i =
            ItemRvDistribuicaoApBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InnerAuditoriaEstoqueCvVh(i)
    }

    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: InnerAuditoriaEstoqueCvVh, position: Int) {
        holder.bindInner(list[position])
    }

}