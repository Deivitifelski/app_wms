package com.documentos.wms_beirario.ui.Tarefas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvArmazensBinding
import com.documentos.wms_beirario.model1.tipo_tarefa.TipoTarefaResponse
import com.documentos.wms_beirario.model1.tipo_tarefa.TipoTarefaResponseItem

class AdapterTipoTarefa(val onClick : (TipoTarefaResponseItem) ->Unit) : RecyclerView.Adapter<AdapterTipoTarefa.TipoTarefaViewHolder>() {

    private var mList = mutableListOf<TipoTarefaResponseItem>()

    inner class TipoTarefaViewHolder(val binding: ItemRvArmazensBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dados: TipoTarefaResponseItem) {
            with(binding) {
                this.txtArmazem.text = dados.descricao
            }
            itemView.setOnClickListener {
                onClick.invoke(dados)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipoTarefaViewHolder {
        val bind = ItemRvArmazensBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TipoTarefaViewHolder(bind)
    }

    override fun onBindViewHolder(holder: TipoTarefaViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size
    fun update(list: List<TipoTarefaResponseItem>) {
        mList.clear()
        mList.addAll(list)
    }
}