package com.documentos.wms_beirario.ui.Tarefas.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvArmazensBinding
import com.documentos.wms_beirario.model.tipo_tarefa.TipoTarefaResponseItem

class AdapterTipoTarefa(val onClick: (TipoTarefaResponseItem) -> Unit) :
    RecyclerView.Adapter<AdapterTipoTarefa.TipoTarefaViewHolder>() {

    private var mList = mutableListOf<TipoTarefaResponseItem>()

    inner class TipoTarefaViewHolder(val binding: ItemRvArmazensBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dados: TipoTarefaResponseItem) {
            with(binding) {
                txtArmazem.text =
                    dados.descricao.replace("SEPARACAO", "SEPARAÇÃO")
                        .replace("MOVIMENTACAO", "MOVIMENTAÇÃO ENTRE ENDEREÇOS")
                        .replace("MONTAGEM", "MONTAGEM DE VOLUMES")
                        .replace("DESMONTAGEM", "DESMONTAGEM DE VOLUMES")
                        .replace("INVENTARIO", " INVENTÁRIO")
                        .replace("RECEBIMENTO DE PRODUCAO", "RECEBIMENTO DE PRODUÇÃO")
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


    /** itens fixos -->*/
    private fun getNewTipoTarefaArmazem() = listOf(
        TipoTarefaResponseItem("CONSULTA CÓDIGO DE BARRAS", 100, "CCB"),
        TipoTarefaResponseItem("CONFIGURAÇÕES", 101, "CONFIG")
    )


    fun update(listTask: List<TipoTarefaResponseItem>) {
        /**DELETANDO TAREFAS AINDA NAO IMPLEMENTADAS -->*/
        mList.clear()
        mList.addAll(listTask)
        listTask.map { Task ->
            if (Task.descricao == "NORMATIVA"||Task.descricao == "EXPEDICAO") {
                mList.remove(Task)
            }
        }
        mList.addAll(getNewTipoTarefaArmazem())
        notifyDataSetChanged()
    }
}