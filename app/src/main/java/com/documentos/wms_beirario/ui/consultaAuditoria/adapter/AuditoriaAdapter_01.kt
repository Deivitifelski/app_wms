package com.documentos.wms_beirario.ui.consultaAuditoria.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvAuditoria01Binding
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoria1

class AuditoriaAdapter_01(val onClick: (ResponseAuditoria1) -> Unit) :
    RecyclerView.Adapter<AuditoriaAdapter_01.AuditoriaAdapter_01_VH>() {


    val mList = mutableListOf<ResponseAuditoria1>()

    inner class AuditoriaAdapter_01_VH(val mBInding: ItemRvAuditoria01Binding) :
        RecyclerView.ViewHolder(mBInding.root) {
        fun bind(auditoriaRes: ResponseAuditoria1) {
            mBInding.armazemApi.text = auditoriaRes.idArmazem.toString()
            mBInding.dataInclusaoApi.text = auditoriaRes.dataInclusao
            mBInding.usuarioApi.text = auditoriaRes.usuarioInclusao


            itemView.setOnClickListener {
                onClick.invoke(auditoriaRes)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditoriaAdapter_01_VH {
        val binding =
            ItemRvAuditoria01Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AuditoriaAdapter_01_VH(binding)
    }

    override fun onBindViewHolder(holder: AuditoriaAdapter_01_VH, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    fun update(mListAuditoria: ResponseAuditoria1) {
        mList.clear()
        mList.addAll(listOf(mListAuditoria))
        notifyDataSetChanged()

    }
}