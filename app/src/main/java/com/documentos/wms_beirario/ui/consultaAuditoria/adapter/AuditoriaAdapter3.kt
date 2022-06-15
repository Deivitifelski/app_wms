package com.documentos.wms_beirario.ui.consultaAuditoria.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvAuditoria3Binding
import com.documentos.wms_beirario.databinding.ItemRvAuditoriaFinishBinding

import com.documentos.wms_beirario.model.auditoria.ResponseAuditoria3
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoriaItem3

class AuditoriaAdapter3() :
    ListAdapter<ResponseAuditoriaItem3, AuditoriaAdapter3.AuditoriaAdapterVH3>(
        DiffUtillAuditoriaFinish()
    ) {

    inner class AuditoriaAdapterVH3(val mBinding: ItemRvAuditoria3Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ResponseAuditoriaItem3) {
            with(mBinding) {
                sequencialApi.text = item.sequencial.toString()
                qntApi.text = item.quantidade.toString()
                endVisualApi.text = item.enderecoVisual
                qntApontadaApi.text = item.quantidadeApontada.toString()
                skuApi.text = item.sku
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditoriaAdapterVH3 {
        val binding =
            ItemRvAuditoria3Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AuditoriaAdapterVH3(binding)
    }

    override fun onBindViewHolder(holder: AuditoriaAdapterVH3, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUtillAuditoriaFinish() : DiffUtil.ItemCallback<ResponseAuditoriaItem3>() {
    override fun areItemsTheSame(
        oldItem: ResponseAuditoriaItem3,
        newItem: ResponseAuditoriaItem3
    ): Boolean {
        return oldItem.idTarefa == newItem.idTarefa
    }

    override fun areContentsTheSame(
        oldItem: ResponseAuditoriaItem3,
        newItem: ResponseAuditoriaItem3
    ): Boolean {
        return oldItem == newItem
    }

}
