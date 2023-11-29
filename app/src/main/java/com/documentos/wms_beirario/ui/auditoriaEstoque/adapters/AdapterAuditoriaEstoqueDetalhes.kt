package com.documentos.wms_beirario.ui.auditoriaEstoque.adapters

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ItemRvAuditoriaDetalhesBinding
import com.documentos.wms_beirario.databinding.ItemRvAuditoriaEstoque1Binding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasEstoque
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasItem
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseAuditoriaEstoqueDetalhes
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.convertData

class AdapterAuditoriaEstoqueDetalhes() :
    RecyclerView.Adapter<AdapterAuditoriaEstoqueDetalhes.AdapterAuditoriaEstoqueDetalhesVh>() {

    private var list = mutableListOf<ResponseAuditoriaEstoqueDetalhes>()

    inner class AdapterAuditoriaEstoqueDetalhesVh(val binding: ItemRvAuditoriaDetalhesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseAuditoriaEstoqueDetalhes) {
            binding.endApontApi.text = item.enderecoVisualApontamento ?: "-"
            binding.endSistemaApi.text = item.enderecoVisualSistema ?: "-"
            binding.dataApi.text =
                if (item.dataHoraUltimoApontamento != null) AppExtensions.formatDataEHora(item.dataHoraUltimoApontamento) else "Não informada"
            binding.numeroSerieApi.text = item.numeroSerie ?: "-"
            binding.qtdApontQtdAuditadaApi.text =
                "${item.quantidadeApontada}/${item.quantidadeAuditada}"
            validaColorRow(item)
        }

        private fun validaColorRow(item: ResponseAuditoriaEstoqueDetalhes) {
            try {
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
            } catch (e: Exception) {
                Log.e("DETALHES", "ERRO AO VALIDAR COR ROW ADAPTER DETALHES")
            }

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterAuditoriaEstoqueDetalhesVh {
        val i = ItemRvAuditoriaDetalhesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdapterAuditoriaEstoqueDetalhesVh(i)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: AdapterAuditoriaEstoqueDetalhesVh, position: Int) {
        holder.bind(list[position])
    }

    fun update(listUpdate: List<ResponseAuditoriaEstoqueDetalhes>?) {
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