package com.documentos.wms_beirario.ui.auditoriaEstoque.adapters

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ItemRvAuditoriaEstoque1Binding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasEstoque
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasItem
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.convertData

class AdapterAuditoriaEstoque1(private val onClick: (ListaAuditoriasItem) -> Unit) :
    RecyclerView.Adapter<AdapterAuditoriaEstoque1.AdapterAuditoriaEstoque1Vh>() {

    private var list = mutableListOf<ListaAuditoriasItem>()

    inner class AdapterAuditoriaEstoque1Vh(val binding: ItemRvAuditoriaEstoque1Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListaAuditoriasItem) {
            binding.numeroApi.text = item.numero.toString()
            binding.situacaoApi.text = item.situacao
            binding.tipoApi.text = item.tipoDescricao
            binding.dataApi.text = AppExtensions.formatDataEHora(item.data)
            binding.solicitanteApi.text = item.solicitante
            validaText(item)

            binding.row.setOnClickListener {
                onClick.invoke(item)
            }
        }

        private fun validaText(item: ListaAuditoriasItem) {
            binding.situacaoApi.setTextColor(Color.BLACK)
            binding.situacaoApi.setBackgroundResource(R.color.white)
            if (item.situacao == "Bloqueada") {
                binding.situacaoApi.setTextColor(Color.WHITE)
                binding.situacaoApi.setBackgroundResource(R.color.color_default)
            }

            if (item.situacao == "Iniciada") {
                binding.situacaoApi.setTextColor(Color.WHITE)
                binding.situacaoApi.setBackgroundResource(R.color.green_verde_padrao)
            }

            if (item.situacao == "Análise" || item.situacao == "Analise") {
                binding.situacaoApi.setTextColor(Color.WHITE)
                binding.situacaoApi.setBackgroundResource(R.color.color_yelon)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterAuditoriaEstoque1Vh {
        val i = ItemRvAuditoriaEstoque1Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdapterAuditoriaEstoque1Vh(i)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: AdapterAuditoriaEstoque1Vh, position: Int) {
        holder.bind(list[position])
    }

    fun update(listUpdate: ListaAuditoriasEstoque?) {
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