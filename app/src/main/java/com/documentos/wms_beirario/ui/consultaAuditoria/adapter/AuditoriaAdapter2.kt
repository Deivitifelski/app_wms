package com.documentos.wms_beirario.ui.consultaAuditoria.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvAuditoriaEstantesBinding
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoriaEstantes2


class AuditoriaAdapter2(val onClick: (ResponseAuditoriaEstantes2) -> Unit) :
    RecyclerView.Adapter<AuditoriaAdapter2.AuditoriaAdapter02VH>() {


    val mList = mutableListOf<ResponseAuditoriaEstantes2>()

    inner class AuditoriaAdapter02VH(val mBInding: ItemRvAuditoriaEstantesBinding) :
        RecyclerView.ViewHolder(mBInding.root) {
        fun bind(estantes: ResponseAuditoriaEstantes2) {
            mBInding.estantes.text = estantes.estante

            itemView.setOnClickListener {
                onClick.invoke(estantes)
            }
        }

    }

    fun update(mListAuditoria: MutableList<ResponseAuditoriaEstantes2>) {
        mList.clear()
        mList.addAll(mListAuditoria)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditoriaAdapter02VH {
        val binding =
            ItemRvAuditoriaEstantesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AuditoriaAdapter02VH(binding)
    }

    override fun onBindViewHolder(holder: AuditoriaAdapter02VH, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size


}