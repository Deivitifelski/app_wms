package com.documentos.wms_beirario.ui.consultaAuditoria.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvAuditoria3Binding

import com.documentos.wms_beirario.model.auditoria.ResponseAuditoria3
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoriaItem3


class AuditoriaAdapter3() : RecyclerView.Adapter<AuditoriaAdapter3.AuditoriaAdapterVH3>() {

    val mList = mutableListOf<ResponseAuditoriaItem3>()

    inner class AuditoriaAdapterVH3(val binding: ItemRvAuditoria3Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseAuditoriaItem3) {
            with(binding) {
                sequencialApi.text = item.sequencial.toString()
                qntApi.text = item.quantidade.toString()
                endVisualApi.text = item.enderecoVisual
                qntApontadaApi.text = item.quantidadeApontada.toString()
                skuApi.text = item.sku
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditoriaAdapterVH3 {
        val mBinding = ItemRvAuditoria3Binding.inflate(LayoutInflater.from(parent.context))
        return AuditoriaAdapterVH3(mBinding)
    }

    override fun onBindViewHolder(holder: AuditoriaAdapterVH3, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    fun update(list: ResponseAuditoria3) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }
}