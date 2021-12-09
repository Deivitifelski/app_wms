package com.documentos.wms_beirario.ui.armazens.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvArmazensBinding
import com.documentos.wms_beirario.model.armazens.ArmazensResponse

class AdapterArmazens(val onClick : (ArmazensResponse) ->Unit) : RecyclerView.Adapter<AdapterArmazens.ArmazensViewHolder>() {

    private var mList = mutableListOf<ArmazensResponse>()

    inner class ArmazensViewHolder(val binding: ItemRvArmazensBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dados: ArmazensResponse) {
            with(binding) {
                this.txtArmazem.text = dados.nome.replace("É","E")
            }
            itemView.setOnClickListener {
                onClick.invoke(dados)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArmazensViewHolder {
        val bind = ItemRvArmazensBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArmazensViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ArmazensViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size
    fun update(nome: List<ArmazensResponse>) {
        mList.clear()
        mList.addAll(nome)
    }
}