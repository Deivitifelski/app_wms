package com.documentos.wms_beirario.ui.auditoriaEstoque.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ItemRvAuditoriaProdutoApBinding
import com.documentos.wms_beirario.databinding.ItemRvDistribuicaoApBinding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.DistribuicaoAP
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseAuditoriaEstoqueAP

class AdapterAuditoriaEstoqueCv() :
    RecyclerView.Adapter<AdapterAuditoriaEstoqueCv.AdapterAuditoriaEstoqueCvVh>() {

    private var list = mutableListOf<ResponseAuditoriaEstoqueAP>()

    inner class AdapterAuditoriaEstoqueCvVh(val binding: ItemRvAuditoriaProdutoApBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseAuditoriaEstoqueAP) {
            validaColorRow(item)
            binding.gradeApi.text = item.codigoGrade
            binding.skuApi.text = item.skuProduto
            binding.volumesApi.text = "${item.quantidadeApontada}/${item.quantidadeAuditada}"
            binding.tipoProdutoApi.text = item.tipoProduto
            if (item.distribuicao != null) {
                binding.rowDist.visibility = View.VISIBLE
                binding.rvDistribuicao.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = InnerAuditoriaEstoqueAP(item.distribuicao)
                }
            } else {
                binding.rowDist.visibility = View.GONE
            }
        }

        private fun validaColorRow(item: ResponseAuditoriaEstoqueAP) {
            if (item.quantidadeApontamentosAtencao > 0) {
                binding.row.setBackgroundResource(R.color.color_yelon)
            }

            if (item.quantidadeApontamentosErro > 0) {
                binding.row.setBackgroundResource(R.color.red)
            }

            if (item.quantidadeApontada == item.quantidadeAuditada
                && item.quantidadeApontamentosErro == 0
                && item.quantidadeApontamentosAtencao == 0
            ) {
                binding.row.setBackgroundResource(R.color.green_verde_padrao)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterAuditoriaEstoqueCvVh {
        val i = ItemRvAuditoriaProdutoApBinding.inflate(
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