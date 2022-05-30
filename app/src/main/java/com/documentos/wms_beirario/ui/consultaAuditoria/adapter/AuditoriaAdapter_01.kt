package com.documentos.wms_beirario.ui.consultaAuditoria.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvAuditoria01Binding
import com.documentos.wms_beirario.model.armazenagem.ArmazemRequestFinish
import com.documentos.wms_beirario.repository.armazenagem.ArmazenagemRepository

class AuditoriaAdapter_01() : RecyclerView.Adapter<AuditoriaAdapter_01.AuditoriaAdapter_01_VH>() {


    val mList = mutableListOf<ArmazemRequestFinish>()

    inner class AuditoriaAdapter_01_VH(val mBInding: ItemRvAuditoria01Binding) :
        RecyclerView.ViewHolder(mBInding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditoriaAdapter_01_VH {
        val binding =
            ItemRvAuditoria01Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AuditoriaAdapter_01_VH(binding)
    }

    override fun onBindViewHolder(holder: AuditoriaAdapter_01_VH, position: Int) {

    }

    override fun getItemCount() = mList.size
}