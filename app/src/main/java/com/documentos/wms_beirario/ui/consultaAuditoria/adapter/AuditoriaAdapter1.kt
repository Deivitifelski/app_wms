package com.documentos.wms_beirario.ui.consultaAuditoria.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvAuditoria01Binding
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoria1Item
import com.documentos.wms_beirario.utils.extensions.AppExtensions

class AuditoriaAdapter1(val onClick: (ResponseAuditoria1Item) -> Unit) :
    RecyclerView.Adapter<AuditoriaAdapter1.AuditoriaAdapter01VH>() {


    val mList = mutableListOf<ResponseAuditoria1Item>()

    inner class AuditoriaAdapter01VH(val mBInding: ItemRvAuditoria01Binding) :
        RecyclerView.ViewHolder(mBInding.root) {
        fun bind(auditoriaRes: ResponseAuditoria1Item) {
            mBInding.armazemApi.text = auditoriaRes.idArmazem.toString()
            mBInding.dataInclusaoApi.text = AppExtensions.formatDataEHora(auditoriaRes.dataInclusao)
            mBInding.usuarioApi.text = auditoriaRes.usuarioInclusao.replace("_", " ")

            itemView.setOnClickListener {
                onClick.invoke(auditoriaRes)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditoriaAdapter01VH {
        val binding =
            ItemRvAuditoria01Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AuditoriaAdapter01VH(binding)
    }

    override fun onBindViewHolder(holder: AuditoriaAdapter01VH, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    fun update(mListAuditoria: MutableList<ResponseAuditoria1Item>) {
        mList.clear()
        mList.addAll(mListAuditoria)
        notifyDataSetChanged()

    }
}