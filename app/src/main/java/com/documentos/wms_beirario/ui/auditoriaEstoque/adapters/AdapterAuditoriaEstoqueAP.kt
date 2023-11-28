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
import com.documentos.wms_beirario.utils.extensions.AppExtensions

class AdapterAuditoriaEstoqueAP(private val onClick: (ResponseAuditoriaEstoqueAP) -> Unit) :
    RecyclerView.Adapter<AdapterAuditoriaEstoqueAP.AdapterAuditoriaEstoqueAPVH>() {

    private var list = mutableListOf<ResponseAuditoriaEstoqueAP>()

    inner class AdapterAuditoriaEstoqueAPVH(val binding: ItemRvAuditoriaProdutoApBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseAuditoriaEstoqueAP) {
            validaColorRow(item)
            binding.gradeApi.text = item.codigoGrade
            binding.txtUltimoAponApi.text =
                if (item.dataHoraUltimoApontamento != null) AppExtensions.formatDataEHora(item.dataHoraUltimoApontamento.toString()) else "Não informada"
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

            binding.row.setOnClickListener {
                onClick.invoke(item)
            }
        }

        private fun validaColorRow(item: ResponseAuditoriaEstoqueAP) {
            if (item.quantidadeApontamentosAtencao > 0) {
                binding.row.setBackgroundResource(R.color.color_yelon_clear)
            }

            if (item.quantidadeApontamentosErro > 0) {
                binding.row.setBackgroundResource(R.color.vermelho_beirario_clear)
            }

            if (item.quantidadeApontada == item.quantidadeAuditada
                && item.quantidadeApontamentosErro == 0
                && item.quantidadeApontamentosAtencao == 0
            ) {
                binding.row.setBackgroundResource(R.color.green_verde_clear)
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


class InnerAuditoriaEstoqueAP(distribuicao: List<DistribuicaoAP>) :
    RecyclerView.Adapter<InnerAuditoriaEstoqueAP.InnerAuditoriaEstoqueAPVH>() {

    private val list = distribuicao


    inner class InnerAuditoriaEstoqueAPVH(val binding: ItemRvDistribuicaoApBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindInner(distribuicaoInner: DistribuicaoAP) {
            with(binding) {
                quantidadeParesApi.text = distribuicaoInner.quantidade
                numeroCalAdoApi.text = distribuicaoInner.tamanho
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerAuditoriaEstoqueAPVH {
        val i =
            ItemRvDistribuicaoApBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InnerAuditoriaEstoqueAPVH(i)
    }

    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: InnerAuditoriaEstoqueAPVH, position: Int) {
        holder.bindInner(list[position])
    }

}