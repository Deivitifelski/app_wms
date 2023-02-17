package com.documentos.wms_beirario.ui.tipoTarefa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ItemRvArmazensBinding
import com.documentos.wms_beirario.model.tipo_tarefa.TipoTarefaResponseItem

class TipoTarefaAdapter(val onClick: (TipoTarefaResponseItem) -> Unit) :
    RecyclerView.Adapter<TipoTarefaAdapter.TipoTarefaViewHolder>() {

    private var mList = mutableListOf<TipoTarefaResponseItem>()

    inner class TipoTarefaViewHolder(val binding: ItemRvArmazensBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dados: TipoTarefaResponseItem) {
            when (dados.descricao) {
                "RECEBIMENTO" -> {
                    binding.image.setImageResource(R.drawable.recebido_1)
                }
                "CONSULTA CÓDIGO DE BARRAS" -> {
                    binding.image.setImageResource(R.drawable.ic_baseline_qr_code_scanner_consulta_cod_barras)
                }
                "INVENTARIO" -> {
                    binding.image.setImageResource(R.drawable.inventario)
                }
                "REIMPRESSÃO DE ETIQUETAS" -> {
                    binding.image.setImageResource(R.drawable.ic_impressora_print)
                }
                "CONFIGURAÇÕES" -> {
                    binding.image.setImageResource(R.drawable.ic_baseline_settings_config)
                }
                "ARMAZENAGEM" -> {
                    binding.image.setImageResource(R.drawable.armazenagem)
                }
                "ETIQUETAGEM" -> {
                    binding.image.setImageResource(R.drawable.etiquetagem)
                }
                "PICKING" -> {
                    binding.image.setImageResource(R.drawable.picking)
                }
                "MONTAGEM" -> {
                    binding.image.setImageResource(R.drawable.montagem)
                }
                "DESMONTAGEM" -> {
                    binding.image.setImageResource(R.drawable.desmontagem)
                }
                "MOVIMENTACAO" -> {
                    binding.image.setImageResource(R.drawable.ic_baseline_arm_alt_24)
                }
                "AUDITORIA" -> {
                    binding.image.setImageResource(R.drawable.auditoria)
                }
                "SEPARACAO" -> {
                    binding.image.setImageResource(R.drawable.separation)
                }
            }

            with(binding) {
                txtArmazem.text =
                    dados.descricao.replace("SEPARACAO", "SEPARAÇÃO")
                        .replace("MOVIMENTACAO", "MOVIMENTAÇÃO ENTRE ENDEREÇOS")
                        .replace("MONTAGEM", "MONTAGEM DE VOLUMES")
                        .replace("INVENTARIO", " INVENTÁRIO")
                        .replace("RECEBIMENTO DE PRODUCAO", "RECEBIMENTO DE PRODUÇÃO")
                        .replace("REIMPRESS¿O", "REIMPRESSÃO")
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
        TipoTarefaResponseItem("CONFIGURAÇÕES", 101, "CONFIG"),
        TipoTarefaResponseItem("RESERVA POR PEDIDO", 102, "RPED")
    )


    fun update(listTask: MutableList<TipoTarefaResponseItem>) {
        /**DELETANDO TAREFAS AINDA NAO IMPLEMENTADAS -->*/
        mList.clear()
        mList.addAll(listTask)
        listTask.forEach { Task ->
            if (Task.descricao == "NORMATIVA" || Task.descricao == "EXPEDICAO" || Task.descricao == "CONFERENCIA" || Task.descricao == "REESTOCAGEM") {
                mList.remove(Task)
            }
        }
        mList.addAll(getNewTipoTarefaArmazem())
        notifyDataSetChanged()
    }
}