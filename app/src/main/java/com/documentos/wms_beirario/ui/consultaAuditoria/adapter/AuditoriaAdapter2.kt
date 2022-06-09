package com.documentos.wms_beirario.ui.consultaAuditoria.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvAuditoria02Binding
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoria1


class AuditoriaAdapter2() : RecyclerView.Adapter<AuditoriaAdapter2.AuditoriaAdapter02VH>() {


    val mList = mutableListOf<ResponseAuditoria1>()

    inner class AuditoriaAdapter02VH(val mBInding: ItemRvAuditoria02Binding) :
        RecyclerView.ViewHolder(mBInding.root) {
        fun bind(auditoriaRes: ResponseAuditoria1) {

        }

    }

    fun update(mListAuditoria: MutableList<ResponseAuditoria1>) {
        mList.clear()
        mList.addAll(mListAuditoria)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditoriaAdapter02VH {
        val binding =
            ItemRvAuditoria02Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AuditoriaAdapter02VH(binding)
    }

    override fun onBindViewHolder(holder: AuditoriaAdapter02VH, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size


}